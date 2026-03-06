package com.shortner.url_shortner.services;

import com.shortner.url_shortner.dto.RegisterReponseDto;
import com.shortner.url_shortner.dto.RegisterRequestDto;
import com.shortner.url_shortner.entity.UserEntity;
import com.shortner.url_shortner.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegisterReponseDto registerUser(RegisterRequestDto request) {

        UserEntity user = UserEntity.builder().username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .build();

        userRepository.save(user);
        return new RegisterReponseDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }

}
