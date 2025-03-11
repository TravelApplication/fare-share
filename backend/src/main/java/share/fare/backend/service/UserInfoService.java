package share.fare.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import share.fare.backend.dto.request.UserInfoRequest;
import share.fare.backend.dto.response.UserInfoResponse;
import share.fare.backend.dto.response.UserResponse;
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

    public UserInfoResponse addOrUpdateUserInfo(UserInfoRequest userInfoRequest, Long userId) {
        if (!Objects.equals(userId, userInfoRequest.getId())) {
            throw new IllegalArgumentException("User ID does not match the request ID");
        }

        UserInfo userInfo = UserInfoMapper.toEntity(userInfoRequest);
        UserInfo savedUserInfo = userInfoRepository.save(userInfo);
        return UserInfoMapper.toResponse(savedUserInfo);
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

    public UserInfoResponse getUserProfile(Long currentUserId, Long targetUserId) {
        UserInfo userInfo = userInfoRepository.findByUserId(targetUserId)
                .orElseThrow(() -> new UserNotFoundException(targetUserId));

        return UserInfoMapper.toResponse(userInfo);
    }
}
