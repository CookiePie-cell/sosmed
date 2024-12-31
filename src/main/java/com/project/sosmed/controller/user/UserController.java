package com.project.sosmed.controller.user;

import com.project.sosmed.entity.User;
import com.project.sosmed.model.registration.RegisterRequest;
import com.project.sosmed.service.EmailService;
import com.project.sosmed.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

    private UserService userService;
    private EmailService emailService;

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        return userService.registerUser(request);
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String token) {
        emailService.verifyEmail(token);
        return "Email Verified";
    }
}
