package com.example.graph.security;

public class SecurityConstants {
    public static final String SECRET = "SECRET_KEY";
    public static final long EXPIRATION_TIME = 9_000_000; // 15 mins
    public static final String JWT_TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/api/services/controller/user/register";
    public static final String LOGIN_URL = "/api/services/controller/user/login";
}
