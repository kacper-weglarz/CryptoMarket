package io.github.kacperweglarz.cryptomarket.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kacperweglarz.cryptomarket.DTO.request.LoginRequest;
import io.github.kacperweglarz.cryptomarket.DTO.request.RegisterRequest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import io.github.kacperweglarz.cryptomarket.entity.User;
import io.github.kacperweglarz.cryptomarket.entity.Wallet;
import io.github.kacperweglarz.cryptomarket.repository.UserRepository;
import io.github.kacperweglarz.cryptomarket.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

public class AuthControllerTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private WalletRepository walletRepository;

    protected ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        walletRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterNewUser() throws Exception  {

        RegisterRequest request = new RegisterRequest();
        request.setName("adminName");
        request.setSurname("adminSurname");
        request.setAlias("adminAlias");
        request.setEmail("admin@admin");
        request.setPassword("password");

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@admin"))
                .andExpect(jsonPath("$.token").exists());

        User savedUser = userRepository.findUserByEmail("admin@admin").orElseThrow();

        assertThat(savedUser.getName()).isEqualTo("adminName");
        assertThat(savedUser.getSurname()).isEqualTo("adminSurname");
        assertThat(savedUser.getAlias()).isEqualTo("adminAlias");

        Optional<Wallet> userWallet = walletRepository.findByUserId(savedUser.getId());

        assertThat(userWallet).isPresent();
    }

    @Test
    void shouldNotRegisterUserWithPasswordLessThanEightCharacters() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("adminName");
        request.setSurname("adminSurname");
        request.setAlias("adminAlias");
        request.setEmail("admin@admin");
        request.setPassword("123");

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotRegisterUserWithExistingEmail() throws Exception {

        User existingUser = new User();
        existingUser.setAlias("existingAlias");
        existingUser.setEmail("existing@email");
        userRepository.save(existingUser);

        RegisterRequest request = new RegisterRequest();
        request.setName("adminName");
        request.setSurname("adminSurname");
        request.setAlias("adminAlias");
        request.setEmail("existing@email");
        request.setPassword("password");

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/auth/register")

                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldNotRegisterUserWithExistingAlias() throws Exception {

        User existingUser = new User();
        existingUser.setAlias("existingAlias");
        existingUser.setEmail("existing@email");
        userRepository.save(existingUser);

        RegisterRequest request = new RegisterRequest();
        request.setName("adminName");
        request.setSurname("adminSurname");
        request.setAlias("existingAlias");
        request.setEmail("admin@email");
        request.setPassword("password");

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {

        User existingUser = new User();
        existingUser.setAlias("loginAlias");
        existingUser.setEmail("login@email");
        existingUser.setPasswordHash(passwordEncoder.encode("hashedPassword"));
        userRepository.save(existingUser);

        LoginRequest request = new LoginRequest();
        request.setEmail("login@email");
        request.setPassword("hashedPassword");

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("login@email"))
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void shouldNotLoginWithWrongPassword() throws Exception {

        User existingUser = new User();
        existingUser.setAlias("loginAlias");
        existingUser.setEmail("login@email");
        existingUser.setPasswordHash(passwordEncoder.encode("hashedPassword"));
        userRepository.save(existingUser);

        LoginRequest request = new LoginRequest();
        request.setEmail("login@email");
        request.setPassword("wrongPassword");

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldNotLoginValidationWhenEmailIsInvalid() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("wrongEmail");
        request.setPassword("hashedPassword");

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }
}
