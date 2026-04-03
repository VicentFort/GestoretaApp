package com.vfortro.gestoreta.dto.inventory.loans;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Data
public class ReturnLoanDto {
    private Long loanId;
    private Long storeId;
    private Long amount;
    private String message;
}
