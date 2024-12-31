package com.kris.wall_e.service;

import com.kris.wall_e.dto.AuthRequest;
import com.kris.wall_e.dto.AuthResponse;

public interface AuthService {

    public AuthResponse register(AuthRequest request);

    public AuthResponse login(AuthRequest request);

}
