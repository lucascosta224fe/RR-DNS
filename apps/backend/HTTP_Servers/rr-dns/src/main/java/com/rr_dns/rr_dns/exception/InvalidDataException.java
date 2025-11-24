package com.rr_dns.rr_dns.exception;

public class InvalidDataException extends RuntimeException {

    public InvalidDataException() {
        super("Data de nascimento inválida!");
    }
}
