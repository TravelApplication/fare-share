package share.fare.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import share.fare.backend.dto.request.AuthenticationRequest;
import share.fare.backend.dto.request.RegisterRequest;
import share.fare.backend.dto.response.AuthenticationResponse;
import share.fare.backend.entity.Role;
import share.fare.backend.entity.User;
import share.fare.backend.entity.UserInfo;
import share.fare.backend.exception.InvalidCredentialsException;
import share.fare.backend.exception.UserAlreadyExistsException;
import share.fare.backend.exception.UserNotFoundException;
import share.fare.backend.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already in use");
        }

        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();

        UserInfo userInfo = UserInfo.builder()
                .user(user)
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .dateOfBirth(registerRequest.getDateOfBirth())
                .phoneNumber(registerRequest.getPhoneNumber())
                .bio(registerRequest.getBio())
                .build();

        user.setUserInfo(userInfo);

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return new AuthenticationResponse(token, user.getId());
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        User user = userRepository.findByEmail(authenticationRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException(authenticationRequest.getEmail()));

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getId().toString(),
                            authenticationRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        String token = jwtService.generateToken(user);

        return new AuthenticationResponse(token, user.getId());
    }
}
