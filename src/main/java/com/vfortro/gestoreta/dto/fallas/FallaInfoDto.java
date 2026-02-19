package com.vfortro.gestoreta.dto.fallas;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FallaInfoDto {
    private Long fallaId;
    private String name;
    private LocalDate creationDate;

}
