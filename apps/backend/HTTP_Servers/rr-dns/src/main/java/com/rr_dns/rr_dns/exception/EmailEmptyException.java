package com.rr_dns.rr_dns.exception;

public class EmailEmptyException extends RuntimeException {
    public EmailEmptyException() { super("Email vazio."); }
}
