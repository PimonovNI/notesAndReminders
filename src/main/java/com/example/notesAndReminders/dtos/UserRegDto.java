package com.example.notesAndReminders.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegDto {

    private String username;
    private String password;
    private String email;
    private String timeZone;

}
