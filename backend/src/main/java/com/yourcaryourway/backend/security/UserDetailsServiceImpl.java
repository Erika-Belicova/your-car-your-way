package com.yourcaryourway.backend.security;

import com.yourcaryourway.backend.model.User;
import com.yourcaryourway.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for loading user-specific data.
 * Loads user details by email for authentication purposes.
 * Throws UsernameNotFoundException if the user does not exist,
 * or if the user registered via an external provider.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // find user by email, throw if not found
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // no password login for OAuth users
        if (user.getAuthProvider() != null && user.getPassword() == null) {
            throw new UsernameNotFoundException("Please login via " + user.getAuthProvider());
        }

        return new CustomUserDetails(user);
    }

}
