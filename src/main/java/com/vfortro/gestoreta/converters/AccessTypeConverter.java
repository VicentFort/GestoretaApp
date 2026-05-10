package com.vfortro.gestoreta.converters;

import com.vfortro.gestoreta.model.enums.AccessType;
import jakarta.persistence.AttributeConverter;

public class AccessTypeConverter implements AttributeConverter<AccessType, String> {
    @Override
    public String convertToDatabaseColumn(AccessType accessType) {
        return (accessType == null) ? null: accessType.getValue();
    }

    @Override
    public AccessType convertToEntityAttribute(String s) {
        return (s == null) ? null : AccessType.fromString(s);
    }
}
