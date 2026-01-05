package io.github.kacperweglarz.cryptomarket.security.controller;

import io.github.kacperweglarz.cryptomarket.DTO.request.LoginRequest;
import io.github.kacperweglarz.cryptomarket.DTO.request.RegisterRequest;
import io.github.kacperweglarz.cryptomarket.DTO.response.UserResponse;
import io.github.kacperweglarz.cryptomarket.security.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;


    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest registerRequest) {

        UserResponse registerResponse = authService.registerUser(registerRequest);

        return ResponseEntity.ok(registerResponse);
    }


    @PostMapping("/login")
    public ResponseEntity<UserResponse> login (@RequestBody LoginRequest loginRequest) {

        UserResponse loginResponse = authService.loginUser(loginRequest);

        return ResponseEntity.ok(loginResponse);
    }


}
