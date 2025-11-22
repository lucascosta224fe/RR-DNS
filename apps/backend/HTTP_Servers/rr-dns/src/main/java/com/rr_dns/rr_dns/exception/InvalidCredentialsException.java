package com.rr_dns.rr_dns.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("E-mail ou senha inválidos!");
    }
}
