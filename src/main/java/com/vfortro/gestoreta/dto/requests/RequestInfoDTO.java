package com.vfortro.gestoreta.dto.requests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequestInfoDTO {
    private Long requestId;
    private Long fallaId;
    private Long userId;
    private String username;
    private String fallaName;
    private String message;
    private Boolean aproved;
    private String reply;
}
