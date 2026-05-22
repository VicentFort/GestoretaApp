package com.vfortro.gestoreta.converters;

import com.vfortro.gestoreta.model.enums.FoodNeedType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Converter(autoApply = true)
public class FoodNeedTypeArrayConverter implements AttributeConverter<List<FoodNeedType>, String[]> {

    @Override
    public String[] convertToDatabaseColumn(List<FoodNeedType> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return new String[1];
        }
        String[] result = attribute.stream()
                .map(FoodNeedType::getValue)
                .toArray(String[]::new);
        // Extrae el string interno ("Vegà", "Histaminia", etc.)
        return result;
    }

    @Override
    public List<FoodNeedType> convertToEntityAttribute(String[] dbData) {
        if (dbData == null || dbData.length == 0) {
            return Collections.emptyList();
        }
        // Usa tu método fromString para devolver el Enum correcto desde el texto de la BD
        return Arrays.stream(dbData)
                .map(FoodNeedType::fromString)
                .collect(Collectors.toList());
    }
}