package com.rr_dns.rr_dns.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateUserDto(
       @NotBlank String nome,
       @NotBlank String email,
       @NotBlank String password,
        @JsonFormat(pattern = "dd-MM-yyyy")
       @NotNull  LocalDate dataNascimento,
       @NotBlank String descricao
) {
}
