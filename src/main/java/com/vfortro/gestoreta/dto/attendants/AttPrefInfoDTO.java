package com.vfortro.gestoreta.dto.attendants;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AttPrefInfoDTO {
    private Long prefId;
    private Long tagId;
    private String tagName;
}
