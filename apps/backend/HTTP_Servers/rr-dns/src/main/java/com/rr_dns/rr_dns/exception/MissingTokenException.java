package com.rr_dns.rr_dns.exception;

public class MissingTokenException extends RuntimeException {
    public MissingTokenException() {
        super("Token ausente");
    }
}