package com.vfortro.gestoreta.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum FoodNeedType {

    VEGAN("Vegà"),
    VEGETARIAN("Vegetarià"),
    GLUTEN("Celiaquia"),
    LACTOSE_INTOLERANCE("Intolerància a la Lactosa"),
    FRUCTOSE_INTOLERANCE("Intolerància a la fructosa"),
    HISTAMINE("Histaminia"),
    DRY_FRUIT_ALLERGY("Al·lèrgia als fruits secs"),
    PEANUT_ALLERGY("Al·lèrgia als cacus"),
    SEAFOOD_ALLERGY("Al·lèrgia als mariscs"),
    FISH_ALLERGY("Al·lèrgia al peix"),
    EGG_ALLERGY("Al·lèrgia als ous"),
    SOY_ALLERGY("Al·lèrgia a la soja"),
    KOSHER("Kosher"),
    HALAL("Halal"),
    OTHER("Altra");

    private final String value;

    FoodNeedType(String value) {
        this.value = value;
    }
    @com.fasterxml.jackson.annotation.JsonValue
    public String getValue() {
        return value;
    }

    public static FoodNeedType fromString(String text) {
        for (FoodNeedType b : FoodNeedType.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return OTHER;
    }

}
