package io.github.kacperweglarz.cryptomarket.controller;

import io.github.kacperweglarz.cryptomarket.DTO.request.SpotOrderRequest;
import io.github.kacperweglarz.cryptomarket.DTO.response.SpotOrderResponse;
import io.github.kacperweglarz.cryptomarket.entity.User;
import io.github.kacperweglarz.cryptomarket.exception.UserNotFoundException;
import io.github.kacperweglarz.cryptomarket.service.OrderService;
import io.github.kacperweglarz.cryptomarket.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;


    @Autowired
    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping("/spot")
    public ResponseEntity<SpotOrderResponse> placeSpotOrder(@Valid @RequestBody SpotOrderRequest spotOrderRequest, Authentication authentication) {

        String email = authentication.getName();

        User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        SpotOrderResponse response = orderService.placeSpotOrder(user.getId(), spotOrderRequest);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/spot/cancel/{orderId}")
    public ResponseEntity<String> cancelSpotOrder(@PathVariable Long orderId, Authentication authentication) {

        String email = authentication.getName();

        User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        orderService.cancelOrder(user.getId(), orderId);

        return ResponseEntity.ok("Order canceled successfully");
    }

    @GetMapping
    public ResponseEntity<List<SpotOrderResponse>> getUserOrders(Authentication authentication) {

        String email = authentication.getName();
        User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        List<SpotOrderResponse> orders = orderService.getUserOrders(user.getId());

        return ResponseEntity.ok(orders);
    }
}
