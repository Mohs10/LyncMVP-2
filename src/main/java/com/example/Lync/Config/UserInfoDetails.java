package com.example.Lync.Config;

import com.example.Lync.Entity.UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserInfoDetails implements UserDetails {

    private String username;
    private String password;
    private String mobileNumber; // Added to support OTP authentication
    private List<GrantedAuthority> authorities;
    private boolean otpAuthenticated = false; // Flag for OTP authentication

    // Constructor for traditional username/password authentication
    public UserInfoDetails(UserInfo userInfo) {
        this.username = userInfo.getEmail();
        this.password = userInfo.getPassword();
        this.mobileNumber = userInfo.getMobileNumber(); // Store the mobile number

        // Handle roles (same as before, with null/empty check)
        if (userInfo.getRoles() != null && !userInfo.getRoles().isEmpty()) {
            this.authorities = List.of(userInfo.getRoles().split(","))
                    .stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        } else {
            this.authorities = List.of();
        }
    }

    // Constructor for OTP-based authentication (doesn't require password)
    public UserInfoDetails(UserInfo userInfo, boolean otpAuthenticated) {
        this.username = userInfo.getEmail();
        this.mobileNumber = userInfo.getMobileNumber();
        this.password = null; // No password required for OTP login
        this.otpAuthenticated = otpAuthenticated;

        // Handle roles (same as before, with null/empty check)
        if (userInfo.getRoles() != null && !userInfo.getRoles().isEmpty()) {
            this.authorities = List.of(userInfo.getRoles().split(","))
                    .stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        } else {
            this.authorities = List.of();
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        // Return null if OTP authenticated
        return otpAuthenticated ? null : password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Customize logic if needed
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Customize logic if needed
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Customize logic if needed
    }

    @Override
    public boolean isEnabled() {
        return true; // Customize logic if needed
    }

    public boolean isOtpAuthenticated() {
        return otpAuthenticated;
    }
}

