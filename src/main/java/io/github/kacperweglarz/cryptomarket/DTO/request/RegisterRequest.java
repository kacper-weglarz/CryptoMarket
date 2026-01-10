package io.github.kacperweglarz.cryptomarket.DTO.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    @NotBlank
    private String alias;
    @NotBlank
    private String email;
    @NotBlank
    private String password;

}
