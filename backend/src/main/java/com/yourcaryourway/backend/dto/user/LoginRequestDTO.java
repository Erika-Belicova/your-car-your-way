package com.yourcaryourway.backend.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for login requests. Requires email and password.
 */
@Data
public class LoginRequestDTO {

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    @Schema(description = "Email address of the user", example = "jean@martin.fr")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Schema(description = "Password of the user", example = "mot-de-passe16")
    private String password;

}
