package com.vfortro.gestoreta.model.enums;

public enum ItemCategory {
    PYROTECHNICS("Pirotècnia"),
    FOOD("Menjar"),
    OFFICE("Oficina"),
    ARTS_AND_CRAFTS("Arts plàstiques"),
    DRINKS("Beguda"),
    INFRASTRUCTURE("Infraestructra"),
    ELECTRONICS("Electrònica / Informàtica"),
    OTHERS("Altres");

    private final String value;

    ItemCategory(String value) {
        this.value = value;
    }

    @com.fasterxml.jackson.annotation.JsonValue
    public String getValue() {
        return value;
    }

    public static ItemCategory fromString(String value) {
        for(ItemCategory itemCategory : ItemCategory.values()) {
            if(itemCategory.value.equalsIgnoreCase(value)) {
                return itemCategory;
            }
        }
        return OTHERS;
    }
}
