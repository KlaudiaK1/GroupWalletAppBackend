package com.example.graph.dto.login;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserObject {
    private String username;
    private String email;
    private String password;
    private String passwordConfirmation;
}
