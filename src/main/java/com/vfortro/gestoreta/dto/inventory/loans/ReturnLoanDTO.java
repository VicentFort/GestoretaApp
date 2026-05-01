package com.vfortro.gestoreta.dto.inventory.loans;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Data
public class ReturnLoanDTO {
    private Long loanId;
    private Long storeId;
    private Long amount;
    private String message;
}
