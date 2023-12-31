package com.example.notesAndReminders.services;

import com.example.notesAndReminders.dtos.UserLoginDto;
import com.example.notesAndReminders.dtos.UserRegDto;
import com.example.notesAndReminders.entities.ActivationKey;
import com.example.notesAndReminders.entities.User;
import com.example.notesAndReminders.entities.enums.Role;
import com.example.notesAndReminders.entities.enums.Status;
import com.example.notesAndReminders.repositories.ActivationKeysRepositories;
import com.example.notesAndReminders.repositories.UsersRepository;
import com.example.notesAndReminders.security.JWTTokenProvider;
import com.example.notesAndReminders.util.MailUtils;
import com.example.notesAndReminders.util.exceptions.EmailVerifyingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UsersService {

    private final UsersRepository usersRepository;

    private final ActivationKeysRepositories activationKeysRepositories;

    private final MailSenderService mailSenderService;

    private final JWTTokenProvider jwtTokenProvider;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsersService(UsersRepository usersRepository, ActivationKeysRepositories activationKeysRepositories, MailSenderService mailSenderService, JWTTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.activationKeysRepositories = activationKeysRepositories;
        this.mailSenderService = mailSenderService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    public Map<Object, Object> login(UserLoginDto dto) throws BadCredentialsException, UsernameNotFoundException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
            Optional<User> userOptional = usersRepository.findByUsername(dto.getUsername());

            if (userOptional.isEmpty())
                throw new UsernameNotFoundException("This name don`t contain, check it again");

            User user = userOptional.get();
            String token = jwtTokenProvider.createToken(user.getId(),
                    user.getUsername(), user.getRole(), user.getEmail());

            Map<Object, Object> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("token", token);

            return response;
        }
        catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid password, check that");
        }
    }

    @Transactional
    public void registration(UserRegDto dto) {
        User user = mapFrom(dto);
        usersRepository.save(user);

        String activationCode = UUID.randomUUID().toString();
        ActivationKey key = ActivationKey.builder()
                .user(user)
                .activationCode(activationCode)
                .build();
        activationKeysRepositories.save(key);

        mailSenderService.send(
                dto.getEmail(),
                MailUtils.SUBJECT_FOR_REGISTRATION,
                MailUtils.getRegistrationText(dto.getUsername(), activationCode)
        );
    }

    @Transactional
    public void verifyUserEmail(String code) throws EmailVerifyingException {
        Optional<ActivationKey> activationKeyOptional = activationKeysRepositories.findByActivationCode(code);

        if (activationKeyOptional.isEmpty())
            throw new EmailVerifyingException("It is illegal activation code. Reregistration, please");

        ActivationKey activationKey = activationKeyOptional.get();

        activationKeysRepositories.deleteById(activationKey.getId());
        usersRepository.updateStatus(activationKey.getUser().getId(), Status.ACTIVE);
    }

    private User mapFrom(UserRegDto o) {
        return User.builder()
                .username(o.getUsername())
                .password(passwordEncoder.encode(o.getPassword()))
                .email(o.getEmail())
                .timeZone(o.getTimeZone())
                .role(Role.USER)
                .status(Status.VALIDATING)
                .build();
    }
}
