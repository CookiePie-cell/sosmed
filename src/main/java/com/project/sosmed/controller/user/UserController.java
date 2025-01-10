package com.project.sosmed.controller.user;

import com.project.sosmed.entity.User;
import com.project.sosmed.model.WebResponse;
import com.project.sosmed.model.registration.RegisterRequest;
import com.project.sosmed.model.user.FollowRequest;
import com.project.sosmed.service.EmailService;
import com.project.sosmed.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    @PostMapping("/follow")
    public ResponseEntity<WebResponse<String>> follow(
            Authentication authentication,
            @RequestBody FollowRequest request
    ) {
        request.setFollowingUserId(authentication.getName());
        String message = userService.userFollow(request);
        return ResponseEntity.ok(WebResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message(message)
                .build());
    }

}
