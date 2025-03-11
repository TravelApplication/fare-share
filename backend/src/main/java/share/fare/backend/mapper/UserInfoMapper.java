package share.fare.backend.mapper;

import share.fare.backend.dto.request.UserInfoRequest;
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

    public static UserInfo toEntity(UserInfoRequest userInfoRequest) {
        return UserInfo.builder()
                .id(userInfoRequest.getId())
                .firstName(userInfoRequest.getFirstName())
                .lastName(userInfoRequest.getLastName())
                .dateOfBirth(userInfoRequest.getDateOfBirth())
                .phoneNumber(userInfoRequest.getPhoneNumber())
                .bio(userInfoRequest.getBio())
                .build();
    }
}