package dev.project.userservice.service;

import dev.project.userservice.dto.AuthResponse;
import dev.project.userservice.dto.LoginRequest;
import dev.project.userservice.dto.RegisterRequest;
import dev.project.userservice.entity.Role;
import dev.project.userservice.entity.UserEntity;
import dev.project.userservice.exception.UserAlreadyExistsException;
import dev.project.userservice.repository.UserRepository;
import dev.project.userservice.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    public UserService(JwtUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public String registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username()))
            throw new UserAlreadyExistsException("Username '" + request.username() + "' is already taken");
        if (userRepository.existsByEmail(request.email()))
            throw new UserAlreadyExistsException("Email '" + request.email() + "' is already taken");
        String encodedPassword = passwordEncoder.encode(request.password());
        UserEntity user = new UserEntity(
                request.username(),
                request.email(),
                encodedPassword,
                Role.ROLE_USER
        );
        userRepository.save(user);
        return "User registered successfully";
    }

    public AuthResponse authenticate(LoginRequest loginRequest){
        authenticationManager.authenticate(new
                UsernamePasswordAuthenticationToken(loginRequest.username(),loginRequest.password()));

        return new AuthResponse(jwtUtil.generateToken(loginRequest.username()));
    }
}
