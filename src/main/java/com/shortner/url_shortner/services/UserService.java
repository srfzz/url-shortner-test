package com.shortner.url_shortner.services;

import java.util.Optional;

import com.shortner.url_shortner.entity.UserEntity;
import com.shortner.url_shortner.repository.UserRepository;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserEntity> findById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return Optional.of(user);
    }

}
