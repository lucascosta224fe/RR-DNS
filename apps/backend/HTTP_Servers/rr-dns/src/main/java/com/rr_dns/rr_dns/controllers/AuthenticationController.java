package com.rr_dns.rr_dns.controllers;

import com.rr_dns.rr_dns.dtos.CreateUserDto;
import com.rr_dns.rr_dns.dtos.LoginUserDto;
import com.rr_dns.rr_dns.dtos.RecoveryJwtTokenDto;
import com.rr_dns.rr_dns.dtos.SessionResponseDto;
import com.rr_dns.rr_dns.services.UserService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private UserService userService;


    @PostMapping("/login")
    public ResponseEntity<RecoveryJwtTokenDto> authenticateUser(@RequestBody LoginUserDto loginUserDto,HttpServletRequest request)
    {
        RecoveryJwtTokenDto tokenDto = userService.authenticateUser(loginUserDto);

        return new ResponseEntity<>(tokenDto, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@Valid @RequestBody CreateUserDto createUserDto) {
        userService.registerUser(createUserDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<SessionResponseDto> getProfile(HttpServletRequest request, @PathVariable Long id) {
        try {
            SessionResponseDto response = userService.getProfile(request, id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
