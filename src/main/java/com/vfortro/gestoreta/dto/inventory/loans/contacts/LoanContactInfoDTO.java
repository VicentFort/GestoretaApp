package com.vfortro.gestoreta.dto.inventory.loans.contacts;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Data
public class LoanContactInfoDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String dniCif;
}
