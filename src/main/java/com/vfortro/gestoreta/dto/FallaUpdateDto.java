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
public class FallaUpdateDto {
    private String name;
    private LocalDate creationDate;
    private String shieldUrl;
}
