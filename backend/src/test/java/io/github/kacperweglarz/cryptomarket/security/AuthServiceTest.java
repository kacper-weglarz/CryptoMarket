package io.github.kacperweglarz.cryptomarket.security;

import io.github.kacperweglarz.cryptomarket.DTO.request.LoginRequest;
import io.github.kacperweglarz.cryptomarket.DTO.request.RegisterRequest;
import io.github.kacperweglarz.cryptomarket.DTO.response.UserResponse;
import io.github.kacperweglarz.cryptomarket.entity.User;
import io.github.kacperweglarz.cryptomarket.security.service.AuthService;
import io.github.kacperweglarz.cryptomarket.security.service.JwtService;
import io.github.kacperweglarz.cryptomarket.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterUserSuccessfully() {

        RegisterRequest request = new RegisterRequest();
        request.setEmail("email@example.pl");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setAlias("alias");
        savedUser.setEmail("email@example.pl");

        String token = "mockedToken";

        when(userService.createUserWithWallet(request)).thenReturn(savedUser);
        when(jwtService.generateToken(request.getEmail())).thenReturn(token);

        UserResponse response = authService.registerUser(request);

        assertNotNull(response);
        assertEquals(token, response.getToken());
        assertEquals("email@example.pl",  response.getEmail());

        verify(userService, times(1)).createUserWithWallet(request);
        verify(jwtService, times(1)).generateToken(request.getEmail());
    }

    @Test
    void shouldNotRegisterUser_WhenUserAlreadyExists() {

        RegisterRequest request = new RegisterRequest();
        request.setEmail("email@example.pl");

        when(userService.createUserWithWallet(request))
                .thenThrow(new RuntimeException("User with this email already exist"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.registerUser(request));

        assertEquals("User with this email already exist", exception.getMessage());

        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void shouldLoginUserSuccessfully() {
        LoginRequest request = new LoginRequest();
        request.setEmail("email@example.pl");
        request.setPassword("password");

        User userDB = new User();
        userDB.setId(1L);
        userDB.setAlias("alias");
        userDB.setEmail("email@example.pl");

        String token = "mockedToken";

        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(userDB);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        when(jwtService.generateToken("email@example.pl")).thenReturn(token);

        UserResponse response = authService.loginUser(request);

        assertNotNull(response);
        assertEquals(token, response.getToken());
        assertEquals("email@example.pl", response.getEmail());

        verify(authenticationManager).authenticate(any());
    }

    @Test
    void shouldNotLoginUser_WhenBadCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("email@example.pl");
        request.setPassword("password");

        when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException("Bad credentials"));

        assertThrows(RuntimeException.class, () -> authService.loginUser(request));

        verify(jwtService, never()).generateToken(any());
    }
}
