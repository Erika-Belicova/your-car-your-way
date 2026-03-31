package com.yourcaryourway.backend.service;

import com.yourcaryourway.backend.dto.user.LoginRequestDTO;
import com.yourcaryourway.backend.response.AuthResponse;
import com.yourcaryourway.backend.security.JWTService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Service responsible for handling user authentication.
 * Validates credentials and returns JWT access and refresh tokens.
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public AuthService(AuthenticationManager authenticationManager, JWTService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse login(LoginRequestDTO loginRequest) {
        // authenticate user with provided login credentials
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // generate and return access and refresh tokens
        String accessToken = jwtService.generateAccessToken(authentication);
        String refreshToken = jwtService.generateRefreshToken(authentication);

        return new AuthResponse(accessToken, refreshToken);
    }

}