package com.vfortro.gestoreta.model.enums;

public enum AccessType {
    REPRESENTATIVE("Representatiu"),
    MANAGER("Gestor"),
    SUPERUSER("Superusuari");

    private final String value;

    AccessType(String value) {
        this.value = value;
    }
    @com.fasterxml.jackson.annotation.JsonValue
    public String getValue() {
        return value;
    }

    public static AccessType fromString(String text) {
        for (AccessType b : AccessType.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return REPRESENTATIVE;
    }

}
