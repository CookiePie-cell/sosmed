package com.project.sosmed.service.impl;

import com.project.sosmed.entity.User;
import com.project.sosmed.entity.VerificationToken;
import com.project.sosmed.exception.TokenExpiredException;
import com.project.sosmed.exception.TokenInvalidException;
import com.project.sosmed.repository.UserRepository;
import com.project.sosmed.repository.VerificationTokenRepository;
import com.project.sosmed.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    private final VerificationTokenRepository verificationTokenRepository;

    private final UserRepository userRepository;

    @Override
    public void sendEmail(String to, String verificationUrl) throws MessagingException {
        //MIME - HTML message
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject("Email Verification");
        helper.setText("<html>" +
                        "<body>" +
                        "<h2>Dear user,</h2>"
                        + "<br/> We're excited to have you get started. " +
                        "Please click on below link to confirm your account."
                        + "<br/> " + verificationUrl +
                        "<br/> Regards,<br/>" +
                        "Registration team" +
                        "</body>" +
                        "</html>"
                , true);

        javaMailSender.send(message);
    }

    @Override
    public void verifyEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenInvalidException("Invalid token"));

        if (verificationToken.isExpired()) {
            throw new TokenExpiredException("Token has expired");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
    }
}
