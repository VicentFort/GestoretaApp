package com.vfortro.gestoreta.dto.fallas.info;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Data
@NoArgsConstructor
public class FallaAdInfoDTO {
    private Long id;
    private String name;
    private Integer memberCount;
    private String shield;
    private LocalDate creationDate;
}
