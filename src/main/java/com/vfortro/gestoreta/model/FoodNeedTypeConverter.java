package com.vfortro.gestoreta.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FoodNeedTypeConverter implements AttributeConverter<FoodNeedType, String> {

    @Override
    public String convertToDatabaseColumn(FoodNeedType foodNeedType) {
        return (foodNeedType == null) ? null : foodNeedType.getValue();
    }

    @Override
    public FoodNeedType convertToEntityAttribute(String s) {
        return (s == null) ? null : FoodNeedType.fromString(s);
    }
}
