package com.rr_dns.rr_dns.dtos;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record LoginUserDto (
        @NotBlank(message = "Preenchimento obrigatório") @Email @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Email inválido!") String email,
        @NotBlank String password
){
}
