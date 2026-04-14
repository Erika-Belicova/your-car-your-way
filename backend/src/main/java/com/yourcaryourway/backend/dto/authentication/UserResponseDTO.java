package com.yourcaryourway.backend.dto.authentication;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO representing the authenticated user's basic information.
 */
@Data
public class UserResponseDTO {

    @Schema(description = "Email of the authenticated user", example = "jean@martin.fr")
    private String email;

    @Schema(description = "Role of the authenticated user", example = "ROLE_USER")
    private String role;

}