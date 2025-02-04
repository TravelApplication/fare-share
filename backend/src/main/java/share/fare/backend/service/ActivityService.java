package share.fare.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import share.fare.backend.dto.request.ActivityRequest;
import share.fare.backend.dto.response.ActivityResponse;
import share.fare.backend.entity.Activity;
import share.fare.backend.entity.Group;
import share.fare.backend.entity.User;
import share.fare.backend.exception.ActivityNotFoundException;
import share.fare.backend.exception.GroupNotFoundException;
import share.fare.backend.exception.UserNotFoundException;
import share.fare.backend.mapper.ActivityMapper;
import share.fare.backend.repository.ActivityRepository;
import share.fare.backend.repository.GroupRepository;
import share.fare.backend.repository.UserRepository;
import share.fare.backend.repository.VoteRepository;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;

    public ActivityResponse createActivity(ActivityRequest activityRequest, Long createdByUserId, Long groupId) {
        Group group = groupRepository
                .findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        User user = userRepository
                .findById(createdByUserId)
                .orElseThrow(() -> new UserNotFoundException(createdByUserId));

        Activity activity = ActivityMapper.toEntity(activityRequest, group);

        return ActivityMapper.toResponse(activityRepository.save(activity));
    }

    public ActivityResponse updateActivity(ActivityRequest request, Long activityId) {
        Activity activity = activityRepository
                .findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException("Activity with ID: " + activityId + " not found"));

        activity.setName(request.getName());
        activity.setDescription(request.getDescription());
        activity.setLink(request.getLink());

        return ActivityMapper.toResponse(activityRepository.save(activity));
    }

    public void deleteActivity(Long activityId) {
        activityRepository.deleteById(activityId);
    }

    public Page<ActivityResponse> getActivitiesForGroup(Long groupId, Pageable pageable) {
        return activityRepository.findByGroup_Id(groupId, pageable).map(ActivityMapper::toResponse);
    }
}
