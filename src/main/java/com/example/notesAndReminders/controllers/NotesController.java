package com.example.notesAndReminders.controllers;

import com.example.notesAndReminders.dtos.NoteCreateDto;
import com.example.notesAndReminders.dtos.NoteReadDto;
import com.example.notesAndReminders.security.UserDetails;
import com.example.notesAndReminders.services.NotesService;
import com.example.notesAndReminders.util.ResponseUtils;
import com.example.notesAndReminders.util.exceptions.ValidationLocalException;
import com.example.notesAndReminders.util.responses.ValidateFailedResponse;
import com.example.notesAndReminders.util.validators.NotesValidator;
import jakarta.validation.Valid;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notes")
public class NotesController {

    private final NotesService notesService;

    private final NotesValidator notesValidator;

    @Autowired
    public NotesController(NotesService notesService, NotesValidator notesValidator) {
        this.notesService = notesService;
        this.notesValidator = notesValidator;
    }

    @GetMapping("")
    public ResponseEntity<List<NoteReadDto>> showAllNotes(){
        return new ResponseEntity<>(notesService.findAllByUserId(getUserId()), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<HttpStatus> addNewNote(@RequestBody @Valid NoteCreateDto dto, BindingResult bindingResult)
            throws ValidationLocalException {
        notesValidator.validate(dto, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationLocalException("Failed validation due to incorrect data",
                    ResponseUtils.buildValidateError(bindingResult));
        }
        notesService.save(dto, getUserDetails());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HttpStatus> updateNote(@RequestBody @Valid NoteCreateDto dto, BindingResult bindingResult,
                                                 @PathVariable("id") Long id)
            throws ValidationLocalException, SchedulerException {
        notesValidator.validate(dto, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationLocalException("Failed validation due to incorrect data",
                    ResponseUtils.buildValidateError(bindingResult));
        }
        notesService.update(id, dto, getUserDetails());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteNote(@PathVariable("id") Long id) throws SchedulerException {
        notesService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private UserDetails getUserDetails() {
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private Long getUserId() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUser().getId();
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

}
