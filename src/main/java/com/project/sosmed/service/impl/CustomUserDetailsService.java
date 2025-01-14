package com.project.sosmed.service.impl;

import com.project.sosmed.entity.CustomUserDetails;
import com.project.sosmed.entity.User;
import com.project.sosmed.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Logging for debugging
        System.out.println("Authorities from CustomUserDetailsService: " + email + " - " + user.getRoles());

        return new CustomUserDetails(user);
    }
}

