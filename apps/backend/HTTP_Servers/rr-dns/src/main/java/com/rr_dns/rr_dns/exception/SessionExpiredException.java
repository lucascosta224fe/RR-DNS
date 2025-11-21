package com.rr_dns.rr_dns.exception;

public class SessionExpiredException extends RuntimeException {

    public SessionExpiredException() {
        super("Sessão expirada.");
    }
}
