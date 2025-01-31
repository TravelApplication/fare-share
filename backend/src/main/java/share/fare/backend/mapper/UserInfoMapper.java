package share.fare.backend.mapper;

import share.fare.backend.dto.response.UserInfoResponse;
import share.fare.backend.entity.UserInfo;

public class UserInfoMapper {

    public static UserInfoResponse toResponse(UserInfo userInfo) {
        return UserInfoResponse.builder()
                .id(userInfo.getId())
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .dateOfBirth(userInfo.getDateOfBirth())
                .phoneNumber(userInfo.getPhoneNumber())
                .bio(userInfo.getBio())
                .build();
    }
}