package share.fare.backend.mapper;

import share.fare.backend.dto.request.UserRequest;
import share.fare.backend.dto.response.UserResponse;
import share.fare.backend.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class UserMapper {
    public static User toEntity(UserRequest userRequest) {
        return User.builder()
                .email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .userInfo(user.getUserInfo() != null ? UserInfoMapper.toResponse(user.getUserInfo()) : null)
                .groupsCreated(user.getGroupsCreated() != null
                        ? user.getGroupsCreated().stream()
                        .map(GroupMapper::toResponse)
                        .collect(Collectors.toList())
                        : null)
                .memberships(user.getMemberships() != null
                        ? user.getMemberships().stream()
                        .map(GroupMembershipMapper::toResponse)
                        .collect(Collectors.toList())
                        : null)
                .build();
    }

    public static UserRequest toUserRequest(User user) {
        return UserRequest.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }
}