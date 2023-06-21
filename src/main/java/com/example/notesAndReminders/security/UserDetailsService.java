package com.example.notesAndReminders.security;

import com.example.notesAndReminders.entities.User;
import com.example.notesAndReminders.entities.enums.Status;
import com.example.notesAndReminders.repositories.UsersRepository;
import com.example.notesAndReminders.util.exceptions.IdNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UsersRepository usersRepository;

    @Autowired
    public UserDetailsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = usersRepository.findByUsername(username);

        if (userOptional.isEmpty())
            throw new UsernameNotFoundException("This name don`t contain, check it again");

        if (userOptional.get().getStatus() == Status.VALIDATING)
            throw new UsernameNotFoundException("Please, verify your email");

        return new UserDetails(userOptional.get());
    }

    public UserDetails loadUserById(Long id) throws IdNotFoundException {
        Optional<User> userOptional = usersRepository.findById(id);

        if (userOptional.isEmpty())
            throw new IdNotFoundException("User with this ID did not find, login again!");

        if (userOptional.get().getStatus() == Status.VALIDATING)
            throw new IdNotFoundException("Please, verify your email");

        return new UserDetails(userOptional.get());
    }
}
