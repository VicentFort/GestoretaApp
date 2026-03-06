package com.vfortro.gestoreta.dto.users;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserCreateDto {
    private Long userId;
    private @NotNull String name;
    private @NotNull String surname;
    private Long fallaId;
    private String urlPfp;
    private @NotNull LocalDate birthday;
    private @NotNull Boolean showBday;
    private @NotNull String email;
    private @NotNull String password;
    private Boolean adminAccess;
}
