package com.rr_dns.rr_dns.exception;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        super("Token invalido.");
    }
}


