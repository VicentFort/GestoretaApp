package com.vfortro.gestoreta.conversor.inventory;

import com.vfortro.gestoreta.dto.inventory.loans.LoanInfoDTO;
import com.vfortro.gestoreta.model.enums.LoanState;
import com.vfortro.gestoreta.model.enums.NotificationType;
import com.vfortro.gestoreta.model.inventory.Loan;
import org.springframework.stereotype.Service;

@Service
public class LoanConversor {

    public LoanInfoDTO fromEntity2Dto(Loan loan) {
        LoanInfoDTO dto = new LoanInfoDTO();
        dto.setId(loan.getLoanId());
        dto.setAmount(loan.getAmount());
        dto.setItemId(loan.getItem().getItemId());
        dto.setItemName(loan.getItem().getName());
        dto.setAcquisitionDate(loan.getAcquisitionDate());
        dto.setIdealReturnDate(loan.getIdealReturnDate());
        if(loan.getRealReturnDate() != null) dto.setRealReturnDate(loan.getRealReturnDate());
        dto.setState(loan.getState().getValue());
        dto.setContactId(loan.getContact().getContactId());
        dto.setContactName(loan.getContact().getName());
        dto.setContactPhone(loan.getContact().getPhone());
        dto.setContactEmail(loan.getContact().getEmail());
        dto.setContactDniCif(loan.getContact().getDniCif());
        if(loan.getState() == LoanState.DELAYED && loan.getNotifications().stream().noneMatch(n -> n.getType() == NotificationType.DELAY)) {
            dto.setHasDelayedNotifications(false);
        }
        dto.setHasDelayedNotifications(true);
        return dto;
    }
}
