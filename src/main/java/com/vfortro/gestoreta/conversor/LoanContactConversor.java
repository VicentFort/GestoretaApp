package com.vfortro.gestoreta.conversor;

import com.vfortro.gestoreta.dto.inventory.loans.contacts.LoanContactCreateDto;
import com.vfortro.gestoreta.dto.inventory.loans.contacts.LoanContactInfoDto;
import com.vfortro.gestoreta.model.Falla;
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

    public LoanContact fromDto2Entity(LoanContactCreateDto dto, Falla falla) {
        LoanContact contact = new LoanContact();
        contact.setName(dto.getName());
        contact.setEmail(dto.getEmail());
        contact.setPhone(dto.getPhone());
        contact.setDniCif(dto.getDniCif());
        contact.setFalla(falla);
        return contact;
    }
}
