package com.example.graph.controller;

import com.example.graph.dto.ApiError;
import com.example.graph.exception.BadRequestException;
import com.example.graph.exception.EmailTakenException;
import com.example.graph.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ErrorControllerAdvice {
    @ExceptionHandler(value = BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(HttpServletRequest request, Exception e) {
        return new ApiError(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = EmailTakenException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiError handleEmailTakenException(HttpServletRequest request, Exception e) {
        return new ApiError(HttpStatus.OK, e.getMessage());
    }

    @ExceptionHandler(value = NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundError(HttpServletRequest request, Exception e) {
        return new ApiError(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
