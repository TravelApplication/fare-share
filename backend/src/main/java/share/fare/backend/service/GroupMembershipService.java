package share.fare.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import share.fare.backend.dto.response.GroupMembershipResponse;
import share.fare.backend.entity.Group;
import share.fare.backend.entity.GroupMembership;
import share.fare.backend.entity.GroupRole;
import share.fare.backend.entity.User;
import share.fare.backend.exception.GroupNotFoundException;
import share.fare.backend.exception.UserAlreadyInGroupException;
import share.fare.backend.exception.UserIsNotInGroupException;
import share.fare.backend.exception.UserNotFoundException;
import share.fare.backend.mapper.GroupMembershipMapper;
import share.fare.backend.repository.GroupMembershipRepository;
import share.fare.backend.repository.GroupRepository;
import share.fare.backend.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupMembershipService {
    private final GroupMembershipRepository groupMembershipRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;

    public GroupMembershipResponse addMemberToGroup(Long groupId, Long userId, GroupRole role, User currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        securityService.checkIfUserIsInGroup(currentUser, group);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (groupMembershipRepository.existsByGroupAndUser(group, user)) {
            throw new UserAlreadyInGroupException("User is already a member of this group");
        }

        GroupMembership membership = GroupMembership.builder()
                .group(group)
                .user(user)
                .role(role)
                .build();

        GroupMembership savedMembership = groupMembershipRepository.save(membership);
        return GroupMembershipMapper.toResponse(savedMembership);
    }

    public void removeMemberFromGroup(Long groupId, Long userId, User currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        GroupMembership membership = groupMembershipRepository.findByGroupAndUser(group, user)
                .orElseThrow(() -> new UserIsNotInGroupException("User is not a member of the group"));

        securityService.checkIfUserCanRemoveMember(currentUser, user, group);

        if (membership.getRole() == GroupRole.OWNER) {
            throw new IllegalArgumentException("Owner cannot be removed from the group");
        }

        groupMembershipRepository.delete(membership);
    }

    public GroupMembershipResponse updateMemberRole(Long groupId, Long userId, GroupRole newRole, User currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        securityService.checkIfUserIsGroupOwner(currentUser, group);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        GroupMembership membership = groupMembershipRepository.findByGroupAndUser(group, user)
                .orElseThrow(() -> new UserIsNotInGroupException("User is not a member of the group"));

        membership.setRole(newRole);
        GroupMembership updatedMembership = groupMembershipRepository.save(membership);
        return GroupMembershipMapper.toResponse(updatedMembership);
    }

    public List<GroupMembershipResponse> getGroupMembers(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        return groupMembershipRepository.findByGroup(group).stream()
                .map(GroupMembershipMapper::toResponse)
                .collect(Collectors.toList());
    }
}
