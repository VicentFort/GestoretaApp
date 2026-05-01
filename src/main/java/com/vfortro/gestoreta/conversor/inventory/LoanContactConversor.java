package com.vfortro.gestoreta.conversor.inventory;

import com.vfortro.gestoreta.dto.inventory.loans.contacts.LoanContactCreateDTO;
import com.vfortro.gestoreta.dto.inventory.loans.contacts.LoanContactInfoDTO;
import com.vfortro.gestoreta.model.Falla;
import com.vfortro.gestoreta.model.inventory.LoanContact;
import org.springframework.stereotype.Service;

@Service
public class LoanContactConversor {
    public LoanContactInfoDTO fromEntity2Dto(LoanContact contact) {
        LoanContactInfoDTO dto = new LoanContactInfoDTO();
        dto.setId(contact.getContactId());
        dto.setName(contact.getName());
        dto.setEmail(contact.getEmail());
        dto.setPhone(contact.getPhone());
        dto.setDniCif(contact.getDniCif());
        return dto;
    }

    public LoanContact fromDto2Entity(LoanContactCreateDTO dto, Falla falla) {
        LoanContact contact = new LoanContact();
        contact.setName(dto.getName());
        contact.setEmail(dto.getEmail());
        contact.setPhone(dto.getPhone());
        contact.setDniCif(dto.getDniCif());
        contact.setFalla(falla);
        return contact;
    }
}
