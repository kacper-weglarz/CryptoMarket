package io.github.kacperweglarz.cryptomarket.security.service;

import io.github.kacperweglarz.cryptomarket.DTO.request.LoginRequest;
import io.github.kacperweglarz.cryptomarket.DTO.request.RegisterRequest;
import io.github.kacperweglarz.cryptomarket.DTO.response.UserResponse;
import io.github.kacperweglarz.cryptomarket.entity.User;
import io.github.kacperweglarz.cryptomarket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthService(UserService userService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager =  authenticationManager;
    }

    public UserResponse registerUser(RegisterRequest registerRequest) {

        User createdUser = userService.createUserWithWallet(registerRequest);

        String generatedToken = jwtService.generateToken(createdUser.getEmail());

        return new UserResponse(
                createdUser.getId(),
                createdUser.getAlias(),
                createdUser.getEmail(),
                generatedToken
        );
    }

    public UserResponse loginUser(LoginRequest loginRequest) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        User authUser = (User) auth.getPrincipal();

        String generatedToken = jwtService.generateToken(authUser.getEmail());

        return new UserResponse(
                authUser.getId(),
                authUser.getAlias(),
                authUser.getEmail(),
                generatedToken
        );
    }

}
