package com.project.sosmed.service;

import com.project.sosmed.entity.User;
import com.project.sosmed.model.registration.RegisterRequest;
import com.project.sosmed.model.user.FollowRequest;
import jakarta.mail.MessagingException;

public interface UserService {
    User registerUser(RegisterRequest registerRequest);

    void createVerificationToken(User user) throws MessagingException;

    void userFollow(FollowRequest request);
}
