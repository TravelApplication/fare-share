package share.fare.backend.mapper;

import share.fare.backend.dto.request.GroupRequest;
import share.fare.backend.dto.response.GroupMembershipResponse;
import share.fare.backend.dto.response.GroupResponse;
import share.fare.backend.entity.Group;
import share.fare.backend.entity.GroupMembership;
import share.fare.backend.entity.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GroupMapper {
    public static GroupResponse toResponse(Group group) {
        if (group == null) {
            return null;
        }

        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .createdByUserId(group.getCreatedBy() != null ? group.getCreatedBy().getId() : null)
                .createdAt(group.getCreatedAt())
                .tripStartDate(group.getTripStartDate())
                .tripEndDate(group.getTripEndDate())
                .tags(group.getTags())
                .groupImageUrl(group.getGroupImageUrl())
                .links(group.getLinks())
                .memberships(mapMemberships(group.getMemberships()))
                .activities(ActivityMapper.mapActivities(group.getActivities()))
                .build();
    }

    public static Group toEntity(GroupRequest groupRequest, User createdBy) {
        if (groupRequest == null) {
            return null;
        }

        return Group.builder()
                .name(groupRequest.getName())
                .description(groupRequest.getDescription())
                .createdBy(createdBy)
                .tripStartDate(groupRequest.getTripStartDate())
                .tripEndDate(groupRequest.getTripEndDate())
                .tags(groupRequest.getTags())
                .groupImageUrl(groupRequest.getGroupImageUrl())
                .links(groupRequest.getLinks())
                .memberships(new ArrayList<>())
                .build();
    }

    private static List<GroupMembershipResponse> mapMemberships(List<GroupMembership> memberships) {
        if (memberships == null) {
            return Collections.emptyList();
        }

        return memberships.stream()
                .map(GroupMapper::toMembershipResponse)
                .collect(Collectors.toList());
    }

    private static GroupMembershipResponse toMembershipResponse(GroupMembership membership) {
        if (membership == null) {
            return null;
        }

        return GroupMembershipResponse.builder()
                .userId(membership.getUser() != null ? membership.getUser().getId() : null)
                .userEmail(membership.getUser() != null ? membership.getUser().getEmail() : null)
                .role(membership.getRole())
                .groupId(membership.getGroup().getId() != null ? membership.getGroup().getId() : null)
                .joinedAt(membership.getJoinedAt())
                .build();
    }
}