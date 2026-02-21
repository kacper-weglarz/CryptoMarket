package io.github.kacperweglarz.cryptomarket.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kacperweglarz.cryptomarket.DTO.request.RegisterRequest;
import io.github.kacperweglarz.cryptomarket.DTO.response.UserResponse;
import io.github.kacperweglarz.cryptomarket.repository.OrderRepository;
import io.github.kacperweglarz.cryptomarket.repository.UserRepository;
import io.github.kacperweglarz.cryptomarket.repository.WalletRepository;
import io.github.kacperweglarz.cryptomarket.security.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.empty;

public class WalletControllerIT extends BaseIntegrationIT {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private OrderRepository orderRepository;

    protected ObjectMapper objectMapper = new ObjectMapper();

    private String validToken;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        walletRepository.deleteAll();
        userRepository.deleteAll();

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("userName");
        registerRequest.setSurname("userSurname");
        registerRequest.setAlias("userAlias");
        registerRequest.setEmail("user@user");
        registerRequest.setPassword("password");

        UserResponse userResponse = authService.registerUser(registerRequest);
        this.validToken = userResponse.getToken();
    }

    @Test
    void shouldReturnUserWallet() throws Exception {

        mockMvc.perform(get("/api/v1/wallet")

                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.initialized").value(false))
                .andExpect(jsonPath("$.items", empty()));
    }

    @Test
    void shouldInitializeWallet() throws Exception {

        mockMvc.perform(post("/api/v1/wallet/initialize")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
