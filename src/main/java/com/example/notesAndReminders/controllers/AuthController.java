package com.example.notesAndReminders.controllers;

import com.example.notesAndReminders.dtos.UserLoginDto;
import com.example.notesAndReminders.dtos.UserRegDto;
import com.example.notesAndReminders.services.UsersService;
import com.example.notesAndReminders.util.exceptions.EmailNotVerifiedException;
import com.example.notesAndReminders.util.exceptions.EmailVerifyingException;
import com.example.notesAndReminders.util.exceptions.IdNotFoundException;
import com.example.notesAndReminders.util.exceptions.ValidationLocalException;
import com.example.notesAndReminders.util.responses.LoginFailedResponse;
import com.example.notesAndReminders.util.responses.ValidateFailedResponse;
import com.example.notesAndReminders.util.validators.UsersValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UsersService usersService;

    private final UsersValidator usersValidator;

    @Autowired
    public AuthController(UsersService usersService, UsersValidator usersValidator) {
        this.usersService = usersService;
        this.usersValidator = usersValidator;
    }

    @GetMapping("activation/{code}")
    public ResponseEntity<HttpStatus> activation(@PathVariable("code") String code)
            throws EmailVerifyingException {

        usersService.verifyUserEmail(code);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Map<Object, Object>> login(@RequestBody @Valid UserLoginDto dto, BindingResult bindingResult)
            throws AuthenticationException, ValidationLocalException {
        if (bindingResult.hasErrors()) {
            throw new ValidationLocalException("Failed validation due to incorrect data", buildValidateError(bindingResult));
        }

        Map<Object, Object> response = usersService.login(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reg")
    public ResponseEntity<HttpStatus> registration(@RequestBody @Valid UserRegDto dto, BindingResult bindingResult)
            throws ValidationLocalException{
        usersValidator.validate(dto, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationLocalException("Failed validation due to incorrect data", buildValidateError(bindingResult));
        }

        usersService.registration(dto);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler({IdNotFoundException.class, UsernameNotFoundException.class, BadCredentialsException.class,
            EmailNotVerifiedException.class, EmailVerifyingException.class})
    private ResponseEntity<LoginFailedResponse> authException(AuthenticationException e) {

        LoginFailedResponse response = new LoginFailedResponse(
                e.getClass().getSimpleName(),
                e.getMessage(),
                Instant.now()
        );

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ValidationLocalException.class)
    private ResponseEntity<ValidateFailedResponse> validException(ValidationLocalException e) {

        ValidateFailedResponse response = new ValidateFailedResponse(
                e.getClass().getSimpleName(),
                e.getMessage(),
                e.getErrorsInfo(),
                Instant.now()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private Map<String, List<String>> buildValidateError(Errors errors) {
        Map<String, List<String>> info = new HashMap<>();

        errors.getFieldErrors()
                .forEach(fieldError -> {
                    if (info.containsKey(fieldError.getField()))
                        info.get(fieldError.getField()).add(fieldError.getDefaultMessage());
                    else
                        info.put(fieldError.getField(), new ArrayList<>(Collections.singletonList(fieldError.getDefaultMessage())));
                });

        return info;
    }
}
