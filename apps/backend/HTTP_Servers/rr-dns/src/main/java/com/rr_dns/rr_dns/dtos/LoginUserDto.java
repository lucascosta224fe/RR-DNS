package com.rr_dns.rr_dns.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginUserDto (
        @NotBlank String email,
        @NotBlank String password
){
}
