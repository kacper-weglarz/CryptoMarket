package io.github.kacperweglarz.cryptomarket.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long userId;
    private String alias;
    private String email;
    private String token;
}
