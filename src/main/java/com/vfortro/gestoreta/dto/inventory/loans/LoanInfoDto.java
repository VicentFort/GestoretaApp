package com.vfortro.gestoreta.dto.inventory.loans;

import com.vfortro.gestoreta.dto.inventory.loans.contacts.LoanContactInfoDto;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@Data
public class LoanInfoDto {
    private Long id;
    private Long amount;
    private LocalDateTime acquisitionDate;
    private LocalDateTime idealReturnDate;
    private LocalDateTime realReturnDate;
    private String state;
    private Long contactId;
    private Long itemId;
}
