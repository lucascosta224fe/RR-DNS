package com.rr_dns.rr_dns.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SessionResponseDto{
    String sessionId;
    LocalDateTime expiration;
    String ipServer;
    UserDto user;

}
