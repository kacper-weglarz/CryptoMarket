package io.github.kacperweglarz.cryptomarket.DTO.request;

import lombok.Data;

@Data
public class RegisterRequest {

    private String name;
    private String surname;
    private String alias;
    private String email;
    private String password;

}
