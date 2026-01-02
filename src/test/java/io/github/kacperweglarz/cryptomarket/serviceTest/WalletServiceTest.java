package io.github.kacperweglarz.cryptomarket.serviceTest;

import io.github.kacperweglarz.cryptomarket.entity.User;
import io.github.kacperweglarz.cryptomarket.entity.Wallet;
import io.github.kacperweglarz.cryptomarket.repository.WalletRepository;
import io.github.kacperweglarz.cryptomarket.service.WalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    @Test
    void shouldCreateNewWallet() {

        User user = new User();
        Wallet wallet = walletService.createWallet(user);

        assertNotNull(wallet);
        assertEquals(user, wallet.getUser());

        assertNotNull(wallet.getWalletItems());
        assertTrue(wallet.getWalletItems().isEmpty());
    }
}
