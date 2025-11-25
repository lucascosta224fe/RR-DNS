package com.rr_dns.rr_dns.exception;

public class PasswordEmptyException extends RuntimeException {
    public PasswordEmptyException() { super("A senha não pode ser vazia."); }
}
