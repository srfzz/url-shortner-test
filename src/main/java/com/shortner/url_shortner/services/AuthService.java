package com.shortner.url_shortner.services;

import com.shortner.url_shortner.dto.LoginRequestDto;
import com.shortner.url_shortner.dto.LoginResponseDto;
import com.shortner.url_shortner.dto.RegisterReponseDto;
import com.shortner.url_shortner.dto.RegisterRequestDto;
import com.shortner.url_shortner.entity.UserEntity;
import com.shortner.url_shortner.jwtUtil.JwtUtils;
import com.shortner.url_shortner.repository.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthService {
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final AuthenticationManager authenticationManager;
        private final JwtUtils jwtUtils;

        public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                        AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
                this.authenticationManager = authenticationManager;
                this.jwtUtils = jwtUtils;
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

        public LoginResponseDto loginUser(LoginRequestDto loginRequest) {

                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                                                loginRequest.getPassword()));

                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String token = jwtUtils.generateToken(userDetails);
                log.info("User {} logged in successfully", userDetails);
                String role = userDetails.getAuthorities()
                                .stream()
                                .findFirst()
                                .map(GrantedAuthority::getAuthority)
                                .orElse(null);

                return LoginResponseDto.builder()
                                .token(token)
                                .user(new RegisterReponseDto(userDetails.getId(), userDetails.getUsername(),
                                                userDetails.getEmail(), role))
                                .build();

        }

}
