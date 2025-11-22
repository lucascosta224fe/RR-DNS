package com.rr_dns.rr_dns.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException() {
        super("Este e-mail já está cadastrado.");
    }
}
