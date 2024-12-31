package com.project.sosmed.service;

import com.project.sosmed.entity.User;
import com.project.sosmed.model.registration.RegisterRequest;
import jakarta.mail.MessagingException;

public interface UserService {
    User registerUser(RegisterRequest registerRequest);

    void createVerificationToken(User user) throws MessagingException;
}
