package com.vfortro.gestoreta.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserUpdateDto {
    private String name;
    private String surname;
    private LocalDate birthday;
    private Boolean showBday;
    private String urlPfp;
}
