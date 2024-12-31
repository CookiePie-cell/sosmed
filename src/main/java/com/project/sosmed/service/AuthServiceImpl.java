package com.project.sosmed.service;

import com.project.sosmed.model.auth.LoginRequest;
import com.project.sosmed.model.auth.LoginResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()

                )
        );

        System.out.println("Authoritessss from AuthServiceImpl: " + "admin@example.com" + " - " +  authentication.getName());
        String token = tokenService.generateToken(authentication);

        return LoginResponse.builder()
                .accessToken(token)
                .build();
    }
}
