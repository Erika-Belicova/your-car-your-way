package com.yourcaryourway.backend.service.user;

import com.yourcaryourway.backend.dto.authentication.UserResponseDTO;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Service responsible for retrieving the authenticated user's profile information
 * from the security context.
 */
@Service
public class UserService {

    public UserResponseDTO getCurrentUser(Authentication authentication) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setEmail(authentication.getName());
        userResponseDTO.setRole(authentication.getAuthorities().iterator().next().getAuthority());
        return userResponseDTO;
    }

}