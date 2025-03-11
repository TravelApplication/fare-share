package share.fare.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import share.fare.backend.dto.request.UserInfoRequest;
import share.fare.backend.dto.response.UserInfoResponse;
import share.fare.backend.dto.response.UserResponse;
import share.fare.backend.entity.User;
import share.fare.backend.entity.UserInfo;
import share.fare.backend.exception.UserNotFoundException;
import share.fare.backend.mapper.UserInfoMapper;
import share.fare.backend.repository.UserInfoRepository;
import share.fare.backend.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserInfoService {
    private final UserInfoRepository userInfoRepository;
    private final UserRepository userRepository;

    @Transactional
    public UserInfoResponse addOrUpdateUserInfo(UserInfoRequest userInfoRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        UserInfo userInfo = userInfoRepository.findByUserId(userId)
                .orElse(new UserInfo());

        userInfo.setUser(user);
        userInfo.setFirstName(userInfoRequest.getFirstName());
        userInfo.setLastName(userInfoRequest.getLastName());
        userInfo.setPhoneNumber(userInfoRequest.getPhoneNumber());
        userInfo.setBio(userInfoRequest.getBio());
        userInfo.setDateOfBirth(userInfoRequest.getDateOfBirth());

        userInfoRepository.save(userInfo);
        return UserInfoMapper.toResponse(userInfo);
    }

    public List<UserInfoResponse> searchTop8Users(String name) {
        List<UserInfo> users = userInfoRepository.findTop8ByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name);
        return users.stream().map(UserInfoMapper::toResponse).toList();
    }

    public Page<UserInfoResponse> searchUsersPaginated(String name, Pageable pageable) {
        return userInfoRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name, pageable)
                .map(UserInfoMapper::toResponse);
    }

    public UserInfoResponse getUserProfile(Long targetUserId) {
        UserInfo userInfo = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException(targetUserId)).getUserInfo();

        if (Objects.isNull(userInfo)) {
            return UserInfoResponse.builder().build();
        }
        return UserInfoMapper.toResponse(userInfo);
    }
}
