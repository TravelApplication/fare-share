package share.fare.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import share.fare.backend.dto.request.GroupRequest;
import share.fare.backend.dto.response.GroupResponse;
import share.fare.backend.entity.Group;
import share.fare.backend.entity.GroupRole;
import share.fare.backend.entity.User;
import share.fare.backend.exception.GroupNotFoundException;
import share.fare.backend.exception.UserNotFoundException;
import share.fare.backend.mapper.GroupMapper;
import share.fare.backend.repository.GroupRepository;
import share.fare.backend.repository.UserRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public GroupResponse createGroup(GroupRequest groupRequest, Long createdByUserId) {
        validateTripDates(groupRequest.getTripStartDate(), groupRequest.getTripEndDate());
        log.info("Created by user ID: {}", createdByUserId);

        User createdBy = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + createdByUserId + " not found"));

        Group newGroup = GroupMapper.toEntity(groupRequest, createdBy);

        newGroup.addMember(createdBy, GroupRole.OWNER);

        return GroupMapper.toResponse(groupRepository.save(newGroup));
    }

    public Page<GroupResponse> getAllGroups(Pageable pageable) {
        return groupRepository.findAll(pageable).map(GroupMapper::toResponse);
    }

    public GroupResponse getGroupById(Long groupId) {
        return GroupMapper.toResponse(groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group with ID " + groupId + " not found")));
    }

    public void deleteGroup(Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new GroupNotFoundException("Group not found with ID: " + groupId);
        }
        groupRepository.deleteById(groupId);
    }

    public GroupResponse updateGroup(Long groupId, GroupRequest groupRequest) {
        validateTripDates(groupRequest.getTripStartDate(), groupRequest.getTripEndDate());

        Group existingGroup = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found with ID: " + groupId));

        updateGroupFields(existingGroup, groupRequest);

        Group updatedGroup = groupRepository.save(existingGroup);
        return GroupMapper.toResponse(updatedGroup);
    }

    private void updateGroupFields(Group group, GroupRequest request) {
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setTripStartDate(request.getTripStartDate());
        group.setTripEndDate(request.getTripEndDate());
        group.setTags(request.getTags());
        group.setGroupImageUrl(request.getGroupImageUrl());
        group.setLinks(request.getLinks());
    }

    private void validateTripDates(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Trip start date must be before end date");
        }
    }

}
