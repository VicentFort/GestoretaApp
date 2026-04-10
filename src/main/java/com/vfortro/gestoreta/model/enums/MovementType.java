package com.vfortro.gestoreta.model.enums;

public enum MovementType {
    INCOMING("Entrada"),
    OUTGOING("Eixida"),
    LOAN("Prèstec");

    private final String value;

    MovementType(String value) {
        this.value = value;
    }
    @com.fasterxml.jackson.annotation.JsonValue
    public String getValue() {
        return value;
    }

    public static MovementType fromValue(String value) {
        for (MovementType type : MovementType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }
}
