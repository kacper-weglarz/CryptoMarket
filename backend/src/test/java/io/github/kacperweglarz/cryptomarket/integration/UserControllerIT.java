package io.github.kacperweglarz.cryptomarket.integration;

import io.github.kacperweglarz.cryptomarket.entity.User;
import io.github.kacperweglarz.cryptomarket.repository.OrderRepository;
import io.github.kacperweglarz.cryptomarket.repository.UserRepository;
import io.github.kacperweglarz.cryptomarket.repository.WalletRepository;
import io.github.kacperweglarz.cryptomarket.security.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class UserControllerIT extends BaseIntegrationIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WalletRepository walletRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        walletRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "user@user")
    void shouldReturnUserProfileForAuthenticatedUser() throws Exception {

        User user = new User();
        user.setEmail("user@user");
        user.setName("name");
        user.setAlias("alias");
        userRepository.save(user);

        String token = jwtService.generateToken("user@user");

        mockMvc.perform(get("/api/v1/user/profile")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@user"));

    }

    @Test
    void shouldReturnIsUnauthorizedWhenUserIsNotAuthenticated() throws Exception {

        mockMvc.perform(get("/api/v1/user/profile"))
                .andExpect(status().isUnauthorized());
    }
}
