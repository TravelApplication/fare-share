package share.fare.backend.mapper;

import share.fare.backend.dto.request.ActivityRequest;
import share.fare.backend.dto.response.ActivityResponse;
import share.fare.backend.dto.response.VoteResponse;
import share.fare.backend.entity.Activity;
import share.fare.backend.entity.Group;
import share.fare.backend.entity.Vote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ActivityMapper {
    public static ActivityResponse toResponse(Activity activity) {
        if (activity == null) {
            return null;
        }

        return ActivityResponse.builder()
                .id(activity.getId())
                .name(activity.getName())
                .description(activity.getDescription())
                .location(activity.getLocation())
                .link(activity.getLink())
                .groupId(activity.getGroup() != null ? activity.getGroup().getId() : null)
                .createdAt(activity.getCreatedAt())
                .votes(mapVotes(activity.getVotes()))
                .build();
    }

    public static Activity toEntity(ActivityRequest request, Group group) {
        if (request == null) {
            return null;
        }

        return Activity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .location(request.getLocation())
                .link(request.getLink())
                .group(group)
                .votes(new ArrayList<>())
                .build();
    }

    private static List<VoteResponse> mapVotes(List<Vote> votes) {
        if (votes == null) {
            return Collections.emptyList();
        }

        return votes.stream()
                .map(VoteMapper::toResponse)
                .collect(Collectors.toList());
    }

    protected static List<ActivityResponse> mapActivities(List<Activity> activities) {
        if (activities == null) {
            return Collections.emptyList();
        }

        return activities.stream()
                .map(ActivityMapper::toResponse)
                .collect(Collectors.toList());
    }
}
