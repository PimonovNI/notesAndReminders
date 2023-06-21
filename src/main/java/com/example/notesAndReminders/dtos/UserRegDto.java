package com.example.notesAndReminders.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRegDto {

    @NotEmpty(message = "Must not be empty")
    @Size(min = 2, max = 128, message = "Illegal length. Length = [2, 128]")
    private String username;

    @NotEmpty(message = "Must not be empty")
    @Size(min = 4, max = 255, message = "Illegal length. Length = [4, 255]")
    private String password;

    @NotEmpty(message = "Must not be empty")
    @Email(message = "Illegal email format")
    private String email;

    @NotEmpty(message = "Must not be empty")
    @Pattern(regexp = "GMT[+-]((0\\d)|(1[0-2])):(([0-5]\\d)|(60))", message = "Time zone have illegal format. Example `GMT+11:30`")
    private String timeZone;

}
