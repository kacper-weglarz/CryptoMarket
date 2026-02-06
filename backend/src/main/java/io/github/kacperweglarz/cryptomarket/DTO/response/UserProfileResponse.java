package io.github.kacperweglarz.cryptomarket.DTO.response;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileResponse {

    private Long id;
    private String name;
    private String surname;
    private String alias;
    private String email;
}
