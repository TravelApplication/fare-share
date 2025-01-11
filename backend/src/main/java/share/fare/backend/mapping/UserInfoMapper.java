package share.fare.backend.mapping;

import share.fare.backend.dto.response.UserInfoResponseDto;
import share.fare.backend.entity.UserInfo;

public class UserInfoMapper {

    public static UserInfoResponseDto toResponseDto(UserInfo userInfo) {
        return UserInfoResponseDto.builder()
                .id(userInfo.getId())
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .dateOfBirth(userInfo.getDateOfBirth())
                .phoneNumber(userInfo.getPhoneNumber())
                .bio(userInfo.getBio())
                .build();
    }
}