package com.yourcaryourway.backend.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Response object for authentication responses. Contains access and refresh tokens.
 */
@Data
@Schema(description = "Response object containing the authentication token")
public class AuthResponse {

    @Schema(description = "Short-duration JWT access token", example = "eyKzfAmr1RU2Vw...")
    private final String accessToken;

    @Schema(description = "Long-duration JWT refresh token", example = "eyKzfAmr1RU2Vw...")
    private final String refreshToken;

    public AuthResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}
