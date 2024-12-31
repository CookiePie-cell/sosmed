package com.project.sosmed.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmail(String to, String verificationUrl) throws MessagingException;

    void verifyEmail(String token);
}
