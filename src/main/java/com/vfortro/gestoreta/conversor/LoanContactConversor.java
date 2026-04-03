package com.vfortro.gestoreta.conversor;

import com.vfortro.gestoreta.dto.inventory.loans.contacts.LoanContactInfoDto;
import com.vfortro.gestoreta.model.inventory.LoanContact;
import org.springframework.stereotype.Service;

@Service
public class LoanContactConversor {
    public LoanContactInfoDto fromEntity2Dto(LoanContact contact) {
        LoanContactInfoDto dto = new LoanContactInfoDto();
        dto.setId(contact.getContactId());
        dto.setName(contact.getName());
        dto.setEmail(contact.getEmail());
        dto.setPhone(contact.getPhone());
        dto.setDniCif(contact.getDniCif());
        return dto;
    }
}
