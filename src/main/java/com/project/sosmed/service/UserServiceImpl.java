package com.project.sosmed.service;

import com.project.sosmed.entity.Role;
import com.project.sosmed.entity.RoleName;
import com.project.sosmed.entity.User;
import com.project.sosmed.entity.VerificationToken;
import com.project.sosmed.exception.DuplicateResourceException;
import com.project.sosmed.exception.EmailSendException;
import com.project.sosmed.model.registration.RegisterRequest;
import com.project.sosmed.repository.RoleRepository;
import com.project.sosmed.repository.UserRepository;
import com.project.sosmed.repository.VerificationTokenRepository;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private VerificationTokenRepository verificationTokenRepository;

    private EmailService emailService;

    private RoleRepository roleRepository;

    @Override
    public User registerUser(RegisterRequest registerRequest) {

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new DuplicateResourceException("Username already existed");
        }

        Role userRole = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new RuntimeException("User role not found"));

        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .roles(new HashSet<>())
                .isEnabled(false)
                .build();

        user.getRoles().add(userRole);

        User savedUser = userRepository.save(user);

        createVerificationToken(savedUser);

        return savedUser;
    }

    @Override
    public void createVerificationToken(User user)  {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();

        verificationTokenRepository.save(verificationToken);

        String verificationUrl = "http://localhost:8080/api/v1.0/user/verify-email?token=" + token;
        try {
            emailService.sendEmail(user.getEmail(), verificationUrl);
        } catch (MessagingException e) {
            throw new EmailSendException("Failed to send verification email: " + e.getMessage());
        }
    }
}
