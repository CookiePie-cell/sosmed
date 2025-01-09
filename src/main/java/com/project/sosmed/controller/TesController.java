package com.project.sosmed.controller;

import com.project.sosmed.entity.User;
import com.project.sosmed.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/test")
public class TesController {

    private static final Logger LOG = LoggerFactory.getLogger(TesController.class);

    private final UserRepository userRepository;

    @GetMapping
    public String test() {
        return "System running";
    }

    @GetMapping("/hello")
    @PreAuthorize("hasRole('USER')")
    public String sayHello(Authentication authentication) {
        LOG.debug("Roles: '{}'", authentication.getAuthorities());
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + authentication.getName()));

        // Logging for debugging
        System.out.println("Authorities from TestController: " + authentication.getAuthorities());
        return "Hello, world!";
    }
}
