package io.github.kacperweglarz.cryptomarket.DTO.request;

import lombok.Data;

@Data
public class RegisterRequest {

    private String name;
    private String username;
    private String alias;
    private String email;
    private String password;

}
