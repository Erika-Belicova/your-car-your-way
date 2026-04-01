package com.yourcaryourway.backend.dto.authentication;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for refresh token requests.
 */
@Data
public class RefreshTokenRequestDTO {

    @NotBlank(message = "Refresh token cannot be blank")
    @Schema(description = "JWT refresh token", example = "eyKzfAmr1RU2Vw...")
    private String refreshToken;

}