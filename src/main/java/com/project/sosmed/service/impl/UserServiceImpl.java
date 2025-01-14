package com.project.sosmed.service.impl;

import com.project.sosmed.entity.*;
import com.project.sosmed.exception.DuplicateResourceException;
import com.project.sosmed.exception.EmailSendException;
import com.project.sosmed.exception.ResourceNotFoundException;
import com.project.sosmed.model.registration.RegisterRequest;
import com.project.sosmed.model.user.FollowRequest;
import com.project.sosmed.repository.FollowRepository;
import com.project.sosmed.repository.RoleRepository;
import com.project.sosmed.repository.UserRepository;
import com.project.sosmed.repository.VerificationTokenRepository;
import com.project.sosmed.service.EmailService;
import com.project.sosmed.service.UserService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private VerificationTokenRepository verificationTokenRepository;

    private EmailService emailService;

    private RoleRepository roleRepository;

    private FollowRepository followRepository;

    @Transactional
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

    @Override
    public String userFollow(FollowRequest request) {
        UUID followedUserId = UUID.fromString(request.getFollowedUserId());
        UUID followingUserId = UUID.fromString(request.getFollowingUserId());

        User followedUser = userRepository.findById(followedUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User to follow does not exist!"));

        User followingUser = userRepository.findById(followingUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Following user does not exist!"));

        Optional<Follow> existingFollow = followRepository.findExistingFollow(followedUser, followingUser);

        if (existingFollow.isPresent()) {
            followRepository.delete(existingFollow.get());
            return "User unfollowed";
        }

        Follow follow = Follow.builder()
                .followedUser(followedUser)
                .followingUser(followingUser)
                .build();

        followRepository.save(follow);
        return "User followed";
    }
}
