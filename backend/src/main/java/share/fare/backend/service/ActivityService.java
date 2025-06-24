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
import share.fare.backend.mapper.ActivityMapper;
import share.fare.backend.repository.ActivityRepository;
import share.fare.backend.repository.GroupRepository;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final GroupRepository groupRepository;
    private final SecurityService securityService;

    public ActivityResponse createActivity(ActivityRequest activityRequest, User currentUser, Long groupId) {
        if (activityRequest.getStartDate() != null && activityRequest.getEndDate() != null) {
            if (activityRequest.getStartDate().isAfter(activityRequest.getEndDate())) {
                throw new IllegalArgumentException("Start date must be before end date.");
            }
        }
        Group group = groupRepository
                .findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        securityService.checkIfUserIsInGroup(currentUser, group);

        Activity activity = ActivityMapper.toEntity(activityRequest, group);

        return ActivityMapper.toResponse(activityRepository.save(activity));
    }

    public ActivityResponse updateActivity(ActivityRequest request, Long activityId, User currentUser) {
        Activity activity = activityRepository
                .findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException("Activity with ID: " + activityId + " not found"));

        securityService.checkIfUserIsInGroup(currentUser, activity.getGroup());

        activity.setName(request.getName());
        activity.setDescription(request.getDescription());
        activity.setLink(request.getLink());

        return ActivityMapper.toResponse(activityRepository.save(activity));
    }

    public void deleteActivity(Long activityId, User currentUser) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException("Activity with ID: " + activityId + " not found"));

        securityService.checkIfUserIsInGroup(currentUser, activity.getGroup());

        activityRepository.deleteById(activityId);
    }

    public Page<ActivityResponse> getActivitiesForGroup(Long groupId, Pageable pageable) {
        return activityRepository.findByGroup_Id(groupId, pageable).map(ActivityMapper::toResponse);
    }
}
