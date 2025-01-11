package share.fare.backend.mapping;

import share.fare.backend.dto.request.UserRequestDto;
import share.fare.backend.dto.response.UserResponseDto;
import share.fare.backend.entity.User;

import java.time.LocalDate;
import java.util.stream.Collectors;

public class UserMapper {
    public static User toEntity(UserRequestDto userRequestDto) {
        return User.builder()
                .email(userRequestDto.getEmail())
                .password(userRequestDto.getPassword())
                .createdAt(LocalDate.now())
                .build();
    }

    public static UserResponseDto toResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .userInfo(user.getUserInfo() != null ? UserInfoMapper.toResponseDto(user.getUserInfo()) : null)
                .groupsCreated(user.getGroupsCreated() != null
                        ? user.getGroupsCreated().stream()
                        .map(GroupMapper::toResponseDto)
                        .collect(Collectors.toList())
                        : null)
                .memberships(user.getMemberships() != null
                        ? user.getMemberships().stream()
                        .map(GroupMembershipMapper::toResponseDto)
                        .collect(Collectors.toList())
                        : null)
                .build();
    }

    public static UserRequestDto toUserRequestDto(User user) {
        return UserRequestDto.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }
}