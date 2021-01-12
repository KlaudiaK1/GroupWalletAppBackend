package com.example.graph.dto.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenObject {
    private String tokenType;
    private String accessToken;
    private long expiresIn;
}
