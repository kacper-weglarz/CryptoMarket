package io.github.kacperweglarz.cryptomarket.serviceTest;

import io.github.kacperweglarz.cryptomarket.DTO.request.RegisterRequest;
import io.github.kacperweglarz.cryptomarket.entity.User;
import io.github.kacperweglarz.cryptomarket.entity.Wallet;
import io.github.kacperweglarz.cryptomarket.exception.UserAlreadyExistException;
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
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    WalletService walletService;

    @InjectMocks
    UserService userService;


    @Test
    void shouldCreateNewUserAndNewWallet() {

        RegisterRequest request = new RegisterRequest();

        request.setName("XXX");
        request.setSurname("YYY");
        request.setAlias("XXX");
        request.setEmail("XXX@example.com");
        request.setPassword("password");

        Wallet mockWallet = new Wallet();
        String encoded = "encodedPassword";

        when(userRepository.findUserByAlias(request.getAlias())).thenReturn(Optional.empty());
        when(userRepository.findUserByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn(encoded);
        when(walletService.createWallet(any(User.class))).thenReturn(mockWallet);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.createUserWithWallet(request);

        assertNotNull(result);
        assertEquals(encoded, result.getPasswordHash());
        assertEquals(mockWallet, result.getWallet());

        verify(walletService).createWallet(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionIfEmailExists() {

        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        when(userRepository.findUserByEmail(request.getEmail())).thenReturn(Optional.of(new User()));

        UserAlreadyExistException ex = assertThrows(UserAlreadyExistException.class, () ->
                userService.createUserWithWallet(request)
        );

        assertTrue(ex.getMessage().contains("test@example.com"));
        verify(userRepository, never()).save(any());
    }
}
