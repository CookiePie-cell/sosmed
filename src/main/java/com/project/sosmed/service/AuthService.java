package com.project.sosmed.service;

import com.project.sosmed.model.auth.LoginRequest;
import com.project.sosmed.model.auth.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest loginRequest);
}
