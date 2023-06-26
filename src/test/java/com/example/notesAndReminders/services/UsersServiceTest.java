package com.example.notesAndReminders.services;

import com.example.notesAndReminders.dtos.UserRegDto;
import com.example.notesAndReminders.entities.ActivationKey;
import com.example.notesAndReminders.entities.User;
import com.example.notesAndReminders.entities.enums.Role;
import com.example.notesAndReminders.entities.enums.Status;
import com.example.notesAndReminders.repositories.ActivationKeysRepositories;
import com.example.notesAndReminders.repositories.UsersRepository;
import com.example.notesAndReminders.security.JWTTokenProvider;
import com.example.notesAndReminders.util.MailUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UsersServiceTest {

    private final UsersService usersService;

    @Autowired
    UsersServiceTest(UsersService usersService) {
        this.usersService = usersService;
    }

    @MockBean
    private UsersRepository usersRepository;

    @MockBean
    private ActivationKeysRepositories activationKeysRepositories;

    @MockBean
    private MailSenderService mailSenderService;

    @MockBean
    private JWTTokenProvider jwtTokenProvider;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void registration() {
        String email = "test@gmail.com";
        String name = "testName";
        String password = "testPassword";
        String timeZone = "GMT+03:00";
        UserRegDto dto = new UserRegDto();
        dto.setEmail(email);
        dto.setUsername(name);
        dto.setPassword(password);
        dto.setTimeZone(timeZone);

        usersService.registration(dto);

        ArgumentCaptor<User> personCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<ActivationKey> keyCaptor = ArgumentCaptor.forClass(ActivationKey.class);

        Mockito.verify(passwordEncoder, Mockito.times(1))
                .encode(ArgumentMatchers.anyString());
        Mockito.verify(mailSenderService, Mockito.times(1)).send(
                ArgumentMatchers.eq(email),
                ArgumentMatchers.eq(MailUtils.SUBJECT_FOR_REGISTRATION),
                ArgumentMatchers.startsWith("Hi, " + name)
        );
        Mockito.verify(usersRepository, Mockito.times(1))
                .save(personCaptor.capture());
        Mockito.verify(activationKeysRepositories, Mockito.times(1))
                .save(keyCaptor.capture());

        User user = personCaptor.getValue();
        ActivationKey key = keyCaptor.getValue();

        Assertions.assertEquals(name, user.getUsername());
        Assertions.assertEquals(Role.USER, user.getRole());

        Assertions.assertEquals(user, key.getUser());
        Assertions.assertNotNull(key.getActivationCode());
    }

    @Test
    void verifyUserEmail() {
        Long keyId = 1L;
        Long userId = 1L;
        String testActivationCode = "testActivationCode";
        ActivationKey key = ActivationKey.builder()
                .id(keyId)
                .activationCode(testActivationCode)
                .user(User.builder()
                        .id(userId)
                        .build())
                .build();

        Mockito.doReturn(Optional.of(key))
                .when(activationKeysRepositories)
                .findByActivationCode(ArgumentMatchers.eq(testActivationCode));

        usersService.verifyUserEmail(testActivationCode);

        Mockito.verify(activationKeysRepositories, Mockito.times(1))
                .deleteById(ArgumentMatchers.eq(keyId));
        Mockito.verify(usersRepository, Mockito.times(1))
                .updateStatus(ArgumentMatchers.eq(userId), ArgumentMatchers.eq(Status.ACTIVE));
    }
}