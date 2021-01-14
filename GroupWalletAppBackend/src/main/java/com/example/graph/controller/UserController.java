package com.example.graph.controller;

import com.example.graph.dto.login.RegisterUserObject;
import com.example.graph.dto.user.FindUserObject;
import com.example.graph.exception.BadRequestException;
import com.example.graph.exception.NotFoundException;
import com.example.graph.model.User;
import com.example.graph.service.GraphUserDetailsService;
import com.example.graph.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/services/controller/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private GraphUserDetailsService graphUserDetailsService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public User findUser() {
        Optional<User> userFromRepository = userService.findById(graphUserDetailsService.getUserFromSession().getId());

        return userFromRepository.get();
    }

    @GetMapping("/find")
    @ResponseStatus(HttpStatus.OK)
    public User findUser(@RequestBody @Valid FindUserObject findUserObject) {
        Optional<User> userFromRepository = userService.findFirstByEmail(findUserObject.getEmail());

        if (!userFromRepository.isPresent()) {
            throw new NotFoundException("User " + findUserObject.getEmail()  + " not found.");
        }

        return userFromRepository.get();
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody RegisterUserObject registerUserObject) {
        if (!registerUserObject.getPassword().equals(registerUserObject.getPasswordConfirmation())) {
            throw new BadRequestException("Password confirmation doesn't match password.");
        }

        User entity = modelMapper.map(registerUserObject, User.class);

        try {
            userService.addNewUser(entity);
        } catch (IllegalArgumentException e) {
            //TODO:
        }
    }
}
