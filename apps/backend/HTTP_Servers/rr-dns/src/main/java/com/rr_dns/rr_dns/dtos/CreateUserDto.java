package com.rr_dns.rr_dns.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record CreateUserDto(
        String nome,
        String email,
        String password,
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate dataNascimento
) {
}
