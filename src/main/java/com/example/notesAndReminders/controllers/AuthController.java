package com.example.notesAndReminders.controllers;

import com.example.notesAndReminders.dtos.UserLoginDto;
import com.example.notesAndReminders.dtos.UserRegDto;
import com.example.notesAndReminders.services.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {

    private final UsersService usersService;

    @Autowired
    public AuthController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping()
    public ResponseEntity<Map<Object, Object>> login(@RequestBody UserLoginDto dto) {
        try {
            Map<Object, Object> response = usersService.login(dto);
            return ResponseEntity.ok(response);
        }
        catch (BadCredentialsException | UsernameNotFoundException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/reg")
    public ResponseEntity<HttpStatus> registration(@RequestBody UserRegDto dto) {
        usersService.registration(dto);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
