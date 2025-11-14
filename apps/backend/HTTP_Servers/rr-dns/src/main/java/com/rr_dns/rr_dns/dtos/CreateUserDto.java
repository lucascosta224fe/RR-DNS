package com.rr_dns.rr_dns.dtos;

public record CreateUserDto(
        String nome,
        String email,
        String password,
        String telefone
) {
}
