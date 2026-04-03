package com.vfortro.gestoreta.converters;

import com.vfortro.gestoreta.model.enums.LoanState;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LoanStateConverter implements AttributeConverter<LoanState, String> {
    @Override
    public String convertToDatabaseColumn(LoanState loanState) {
        return (loanState == null) ? null : loanState.getValue();
    }

    @Override
    public LoanState convertToEntityAttribute(String s) {
        return (s == null) ? null : LoanState.fromString(s);
    }
}
