package com.yourcaryourway.backend.security;

import com.yourcaryourway.backend.configuration.JwtProperties;
import com.yourcaryourway.backend.exception.JwtGenerationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Service responsible for generating JWT access and refresh tokens
 * signed with HS256 and including the user's role.
 */
@Service
public class JWTService {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;

    public JWTService(JwtEncoder jwtEncoder, JwtProperties jwtProperties) {
        this.jwtEncoder = jwtEncoder;
        this.jwtProperties = jwtProperties;
    }

    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, jwtProperties.getAccessTokenDuration());
    }

    public String generateRefreshToken(Authentication authentication) {
        return generateToken(authentication, jwtProperties.getRefreshTokenDuration());
    }

    private String generateToken(Authentication authentication, long duration) {
        Instant now = Instant.now();
        String subject = authentication.getName();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String role = userDetails.getRole(); // extract role from the user

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
            return this.jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
        }
        catch (Exception e) {
            throw new JwtGenerationException("Token generation failed. Please try again later.");
        }
    }

}