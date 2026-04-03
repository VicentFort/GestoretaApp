package com.vfortro.gestoreta.conversor;

import com.vfortro.gestoreta.dto.inventory.loans.LoanInfoDto;
import com.vfortro.gestoreta.model.enums.LoanState;
import com.vfortro.gestoreta.model.inventory.Loan;
import org.springframework.stereotype.Service;

@Service
public class LoanConversor {

    public LoanInfoDto fromEntity2Dto(Loan loan) {
        LoanInfoDto dto = new LoanInfoDto();
        dto.setId(loan.getLoanId());
        dto.setAmount(loan.getAmount());
        dto.setItemId(loan.getItem().getItemId());
        dto.setAcquisitionDate(loan.getAcquisitionDate());
        dto.setIdealReturnDate(loan.getIdealReturnDate());
        if(loan.getRealReturnDate() != null) dto.setRealReturnDate(loan.getRealReturnDate());
        dto.setState(loan.getState().getValue());
        return dto;
    }
}
