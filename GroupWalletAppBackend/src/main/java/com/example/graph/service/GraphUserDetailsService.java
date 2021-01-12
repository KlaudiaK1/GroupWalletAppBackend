package com.example.graph.service;

import com.example.graph.model.User;
import com.example.graph.repository.UserRepository;
import com.example.graph.security.GraphUserDetails;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GraphUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login)
            throws UsernameNotFoundException, DataAccessException {
        final Optional<User> user = findUser(login);
        if (!user.isPresent()) throw new UsernameNotFoundException("Username not found: " + login);
        return new GraphUserDetails(user.get());
    }

    public Optional<User> findUser(String login) {
        return userRepository.findFirstByEmail(login);
    }

    public User getUserFromSession() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String principal = (String) authentication.getPrincipal();

        return userRepository.findFirstByEmail(principal).get();
    }
}
