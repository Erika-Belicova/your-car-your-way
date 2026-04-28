package com.yourcaryourway.backend.security;

import com.yourcaryourway.backend.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Custom implementation of Spring Security's UserDetails,
 * representing authenticated user information based on the application's User model.
 * Returns ROLE_USER or ROLE_SUPPORT_AGENT based on the user's support access flag.
 */
public class CustomUserDetails implements UserDetails {

    private final String email;
    private final String password;
    private final boolean emailVerified;
    private final boolean isActive;
    private final boolean supportAccess;

    public CustomUserDetails(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.emailVerified = user.getEmailVerified();
        this.isActive = user.getIsActive();
        this.supportAccess = user.getSupportAccess();
    }

    public String getRole() {
        return supportAccess ? "ROLE_SUPPORT_AGENT" : "ROLE_USER";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // return role based on support access flag
        return List.of(new SimpleGrantedAuthority(getRole()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() { // email is the identifier
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.emailVerified && this.isActive; // user must be verified and active
    }

}
