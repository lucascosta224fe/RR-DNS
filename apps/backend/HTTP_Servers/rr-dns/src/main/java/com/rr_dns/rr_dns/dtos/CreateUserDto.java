package com.rr_dns.rr_dns.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateUserDto(
        @NotBlank String nome,
        @NotBlank
        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Email inválido!") String email,
        @NotBlank String password,
        @Past(message = "A data de nascimento deve ser no passado")
        @JsonFormat(pattern = "dd-MM-yyyy")
        @NotNull LocalDate dataNascimento,
        @Size(max = 2000, message = "A descrição tem um limite de 2000 digitos.") @NotBlank String descricao
) {
}
