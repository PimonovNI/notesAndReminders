package com.example.notesAndReminders.util.validators;

import com.example.notesAndReminders.dtos.UserRegDto;
import com.example.notesAndReminders.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UsersValidator implements Validator {

    private final UsersRepository usersRepository;

    @Autowired
    public UsersValidator(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserRegDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserRegDto user = (UserRegDto) target;

        if (user.getUsername() != null && usersRepository.findByUsername(user.getUsername()).isPresent())
            errors.rejectValue("username", "", "This username have already used");

        if (
                user.getPassword() != null &&
                user.getPassword().matches("[a-z]") &&
                user.getPassword().matches("[A-Z]") &&
                user.getPassword().matches("\\d") &&
                user.getPassword().matches("[!@\"'#$%^&*()_/\\-+.,?\\s]")
        )
            errors.rejectValue("password", "", "Illegal password form. Must have lower and upper case letter, digit and special symbol like one of this (!@\"'#$%^&*_/\\-+.,?)");
    }
}
