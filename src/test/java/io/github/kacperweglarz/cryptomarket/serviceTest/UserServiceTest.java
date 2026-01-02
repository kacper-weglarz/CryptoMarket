package io.github.kacperweglarz.cryptomarket.serviceTest;

import io.github.kacperweglarz.cryptomarket.DTO.request.RegisterRequest;
import io.github.kacperweglarz.cryptomarket.entity.User;
import io.github.kacperweglarz.cryptomarket.entity.Wallet;
import io.github.kacperweglarz.cryptomarket.repository.UserRepository;
import io.github.kacperweglarz.cryptomarket.service.UserService;
import io.github.kacperweglarz.cryptomarket.service.WalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private UserService userService;


    @Test
    void shouldCreateNewUserAndNewWallet() {

        RegisterRequest request = new RegisterRequest();
        request.setName("name");
        request.setSurname("surname");
        request.setAlias("alias");
        request.setEmail("email@example.pl");
        request.setPassword("password");

        Wallet mockWallet = new Wallet();

        when(userRepository.findUserByAlias(request.getAlias())).thenReturn(Optional.empty());
        when(userRepository.findUserByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedpassword");
        when(walletService.createWallet(any(User.class))).thenReturn(mockWallet);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User newUser = userService.createUserWithWallet(request);

        assertNotNull(newUser);
        assertNotNull(newUser.getWallet());
        assertEquals(newUser.getName(), request.getName());
        assertEquals(newUser.getSurname(), request.getSurname());
        assertEquals(newUser.getAlias(), request.getAlias());
        assertEquals(newUser.getEmail(), request.getEmail());
        assertEquals("encodedpassword", newUser.getPassword());
        assertNotEquals("password", newUser.getPassword());
        assertEquals(0, newUser.getOrders().size());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldNotCreateNewUserIfAliasExists() {

        RegisterRequest request = new RegisterRequest();
        request.setAlias("alias");

        when(userRepository.findUserByAlias(request.getAlias())).thenReturn(Optional.of(new User()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUserWithWallet(request);
        });

        assertEquals("User with this alias already exist", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldNotCreateNewUserIfEmailExists() {

        RegisterRequest request = new RegisterRequest();
        request.setEmail("email@example.pl");

        when(userRepository.findUserByEmail(request.getEmail())).thenReturn(Optional.of(new User()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUserWithWallet(request);
        });

        assertEquals("User with this email already exist", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }
}
