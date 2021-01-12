package com.example.graph.service;

import com.example.graph.model.User;
import com.example.graph.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void addNewUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (userRepository.findFirstByEmail(user.getEmail()).isPresent()){
            throw new IllegalArgumentException("User email already exists in the database.");
        }

        userRepository.save(user);
    }

    public Optional<User> findFirstByEmail(String email) {
        return userRepository.findFirstByEmail(email);
    }


}
