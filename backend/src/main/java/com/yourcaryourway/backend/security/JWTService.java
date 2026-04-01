package com.yourcaryourway.backend.security;

import com.yourcaryourway.backend.configuration.security.JwtProperties;
import com.yourcaryourway.backend.exception.JwtGenerationException;
import com.yourcaryourway.backend.exception.JwtValidationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Service responsible for generating and validating JWT access and refresh tokens
 * signed with HS256 and including the user's role.
 */
@Service
public class JWTService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final JwtProperties jwtProperties;

    public JWTService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder,
                      JwtProperties jwtProperties) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.jwtProperties = jwtProperties;
    }

    public String generateAccessToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        // build and encode a new access token with the given parameters
        return buildAndEncode(authentication.getName(),
                userDetails.getRole(), // extract role from the user
                jwtProperties.getAccessTokenDuration());
    }

    public String generateRefreshToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        // build and encode a new refresh token with the given parameters
        return buildAndEncode(authentication.getName(),
                userDetails.getRole(), // extract role from the user
                jwtProperties.getRefreshTokenDuration());
    }

    public String generateAccessTokenFromRefreshToken(String refreshToken) {
        Jwt jwt;
        try {
            jwt = jwtDecoder.decode(refreshToken); // decode and validate the refresh token
        } catch (Exception e) {
            throw new JwtValidationException("Invalid or expired refresh token");
        }
        // extract subject and role from the refresh token to build the new token
        return buildAndEncode(jwt.getSubject(), jwt.getClaimAsString("role"),
                jwtProperties.getAccessTokenDuration());
    }

    // build and encode a signed JWT token with the given subject, role and duration
    private String buildAndEncode(String subject, String role, long duration) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusMillis(duration))
                .subject(subject)
                .claim("role", role) // include role in token
                .build();

        // create encoder parameters with header and claims for token generation
        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters
                .from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);

        try {
            // encode the claims into a signed JWT string
            return jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
        } catch (Exception e) {
            throw new JwtGenerationException("Token generation failed. Please try again later.");
        }
    }
}