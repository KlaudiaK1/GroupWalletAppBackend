package com.example.graph.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.graph.dto.ApiError;
import com.example.graph.dto.login.LoginObject;
import com.example.graph.dto.login.TokenObject;
import com.example.graph.exception.BadRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static com.example.graph.security.SecurityConstants.*;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    private Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;

        setFilterProcessesUrl(LOGIN_URL);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            LoginObject creds = new ObjectMapper()
                    .readValue(req.getInputStream(), LoginObject.class);

            logger.debug("Request for authentication received.", req);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>())
            );
        } catch (IOException e) {
            logger.debug("Authentication for request" + req + " failed due to a malformed request.");

            BadRequestException badRequestException = new BadRequestException(e);
            res.setContentType("application/json");
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try {
                ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Malformed request", badRequestException.getMessage());
                Gson gson = new Gson();

                res.getOutputStream().print(gson.toJson(apiError));
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
            return null;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException {
        String token = JWT.create()
                .withSubject(((GraphUserDetails) auth.getPrincipal()).getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(SECRET.getBytes()));

        Gson gson = new Gson();
        TokenObject tokenObject = new TokenObject("Bearer", token, EXPIRATION_TIME);

        String body = gson.toJson(tokenObject);

        res.setContentType("application/json");
        res.getWriter().write(body);
        res.getWriter().flush();

        logger.debug("Authentication successful. Sending JWT token.", body);
    }
}
