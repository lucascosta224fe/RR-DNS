package com.rr_dns.rr_dns.exception;

public class InvalidEmailFormatException extends RuntimeException {
    public InvalidEmailFormatException() {
        super("Email está em um formato inválido!");
    }
}
