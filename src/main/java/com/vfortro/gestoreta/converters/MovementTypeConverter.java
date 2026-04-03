package com.vfortro.gestoreta.converters;

import com.vfortro.gestoreta.model.enums.MovementType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MovementTypeConverter implements AttributeConverter<MovementType, String> {
    @Override
    public String convertToDatabaseColumn(MovementType movementType) {
        return (movementType == null) ? null : movementType.getValue();
    }

    @Override
    public MovementType convertToEntityAttribute(String s) {
        return (s == null) ? null : MovementType.fromValue(s);
    }
}
