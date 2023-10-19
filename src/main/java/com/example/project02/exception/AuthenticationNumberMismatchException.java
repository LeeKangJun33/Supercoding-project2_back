package com.example.project02.exception;

public class AuthenticationNumberMismatchException extends RuntimeException {
    public AuthenticationNumberMismatchException(String message) {
        super(message);
    }
}
