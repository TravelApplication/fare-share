package share.fare.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterRequest registerRequest;
    private AuthenticationRequest authenticationRequest;
    private User testUser;

    @BeforeEach
    public void setUp() {

        registerRequest = RegisterRequest.builder()
                .email("test@test.com")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.now())
                .phoneNumber("1234567890")
                .bio("Test bio")
                .build();

        authenticationRequest = AuthenticationRequest.builder()
                .email("test@test.com")
                .password("password")
                .build();

        testUser = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("encodedPassword")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .userInfo(UserInfo.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .dateOfBirth(LocalDate.now())
                        .phoneNumber("1234567890")
                        .bio("Test bio")
                        .build())
                .build();
    }

    @Test
    public void testRegisterSuccess() {
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

        AuthenticationResponse result = authenticationService.register(registerRequest);

        assertNotNull(result);
        assertEquals("jwtToken", result.getToken());
        verify(userRepository, times(1)).findByEmail(registerRequest.getEmail());
        verify(passwordEncoder, times(1)).encode(registerRequest.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtService, times(1)).generateToken(any(User.class));
    }

    @Test
    public void testRegisterEmailAlreadyInUse() {
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(testUser));

        assertThrows(UserAlreadyExistsException.class, () -> authenticationService.register(registerRequest));
        verify(userRepository, times(1)).findByEmail(registerRequest.getEmail());
    }

    @Test
    void testAuthenticateSuccess() {
        when(userRepository.findByEmail(authenticationRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(testUser)).thenReturn("jwt-token");

        AuthenticationResponse response = authenticationService.authenticate(authenticationRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
    }

    @Test
    public void testAuthenticateUserNotFound() {
        when(userRepository.findByEmail(authenticationRequest.getEmail())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authenticationService.authenticate(authenticationRequest));
        verify(userRepository, times(1)).findByEmail(authenticationRequest.getEmail());
    }

    @Test
    public void testAuthenticateInvalidCredentials() {
        when(userRepository.findByEmail(authenticationRequest.getEmail())).thenReturn(Optional.of(testUser));
        doThrow(BadCredentialsException.class).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(InvalidCredentialsException.class, () -> authenticationService.authenticate(authenticationRequest));
        verify(userRepository, times(1)).findByEmail(authenticationRequest.getEmail());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}