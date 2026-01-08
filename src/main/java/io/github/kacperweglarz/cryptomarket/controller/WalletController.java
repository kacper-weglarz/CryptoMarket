package io.github.kacperweglarz.cryptomarket.controller;

import io.github.kacperweglarz.cryptomarket.DTO.request.DepositRequest;
import io.github.kacperweglarz.cryptomarket.DTO.response.WalletResponse;
import io.github.kacperweglarz.cryptomarket.entity.User;
import io.github.kacperweglarz.cryptomarket.exception.UserNotFoundException;
import io.github.kacperweglarz.cryptomarket.service.UserService;
import io.github.kacperweglarz.cryptomarket.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

    private final WalletService walletService;
    private final UserService userService;

    @Autowired
    public WalletController(WalletService walletService, UserService userService) {
        this.walletService = walletService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<WalletResponse> getUserWallet(Authentication authentication) {

        String email = authentication.getName();

        User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        WalletResponse response = walletService.getUserWallet(user.getId());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<String> deposit(@RequestBody DepositRequest depositRequest, Authentication authentication) {

        String email = authentication.getName();

        User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        walletService.deposit(user.getId(), depositRequest.getAmount());

        return ResponseEntity.ok("Pomyślnie wpłacono: " + depositRequest.getAmount() + " USDT");
    }
}
