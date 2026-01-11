package io.github.kacperweglarz.cryptomarket.service;

import io.github.kacperweglarz.cryptomarket.DTO.request.RegisterRequest;
import io.github.kacperweglarz.cryptomarket.entity.User;
import io.github.kacperweglarz.cryptomarket.entity.Wallet;
import io.github.kacperweglarz.cryptomarket.exception.UserAlreadyExistException;
import io.github.kacperweglarz.cryptomarket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WalletService walletService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, WalletService walletService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.walletService = walletService;
    }

    public Optional<User> findUserByEmail(String email){
        return userRepository.findUserByEmail(email);
    }

    public Optional<User> findUserByAlias(String alias){
        return userRepository.findUserByAlias(alias);
    }

    @Transactional
    public User createUserWithWallet(RegisterRequest request) {

        if (findUserByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistException("User with this email already exist");
        }

        if (findUserByAlias(request.getAlias()).isPresent()) {
            throw new UserAlreadyExistException("User with this alias already exist");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setSurname(request.getSurname());
        newUser.setEmail(request.getEmail());
        newUser.setAlias(request.getAlias());
        newUser.setPasswordHash(encodedPassword);
        newUser.setOrders(new ArrayList<>());

        Wallet newWallet = walletService.createWallet(newUser);

        newUser.setWallet(newWallet);

        userRepository.save(newUser);

        return newUser;
    }
}
