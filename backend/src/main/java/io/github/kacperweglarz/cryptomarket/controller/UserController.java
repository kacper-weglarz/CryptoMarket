package io.github.kacperweglarz.cryptomarket.controller;

import io.github.kacperweglarz.cryptomarket.DTO.response.UserProfileResponse;
import io.github.kacperweglarz.cryptomarket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(Authentication authentication) {

        String email = authentication.getName();

        UserProfileResponse profile = userService.getUserProfile(email);

        return ResponseEntity.ok(profile);
    }
}
