package com.vfortro.gestoreta.model.enums;

public enum NotificationType {
    CONFIRMATION("Confirmació de préstec"),
    REMINDER("Recordatori de préstec"),
    DELAY("Alerta de retràs de préstec");

    private final String value;

    NotificationType(String value) {
        this.value = value;
    }

    @com.fasterxml.jackson.annotation.JsonCreator
    public String getValue() {
        return value;
    }

    public static NotificationType fromValue(String value) {
        for (NotificationType notificationType : NotificationType.values()) {
            if (notificationType.value.equals(value)) {
                return notificationType;
            }
        }
        return CONFIRMATION;
    }

}
