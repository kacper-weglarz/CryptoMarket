package io.github.kacperweglarz.cryptomarket.service;

import io.github.kacperweglarz.cryptomarket.entity.User;
import io.github.kacperweglarz.cryptomarket.entity.Wallet;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class WalletService {


    public Wallet createWallet(User user) {
        Wallet newWallet = new Wallet();
        newWallet.setUser(user);
        newWallet.setWalletItems(new ArrayList<>());



        return newWallet;
    }
}
