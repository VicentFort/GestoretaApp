package com.vfortro.gestoreta.converters;

import com.vfortro.gestoreta.model.enums.ItemCategory;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ItemCategoryConverter implements AttributeConverter<ItemCategory, String> {

    @Override
    public String convertToDatabaseColumn(ItemCategory itemCategory) {
        return (itemCategory == null) ? null : itemCategory.getValue();
    }

    @Override
    public ItemCategory convertToEntityAttribute(String s) {
        return (s == null) ? null : ItemCategory.fromString(s);
    }
}
