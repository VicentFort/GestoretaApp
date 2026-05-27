package com.vfortro.gestoreta.model.enums;

public enum UserNotificationType {
    EVENT_PAYMENT_REMINDER("Recordatori de pagament d.esdeveniment"),
    EVENT_PAYMENT_CONFIRMATION("Pagament d.esdeveniment realitzat"),
    COUPON_SELL_CONFIRMATION("Venda de tiquet realitzada"),
    EVENT_ATTENDANT_PETITION("Sol·licitud d.encarregat d.esdeveniment"),
    FEE_PAYMENT_CONFIRMATION("Pagament cobrat"),
    REQUEST_RESOLUTION("Resolució de sol·licitud de unió"),
    OTHER("Altra");

    private final String value;

    UserNotificationType(String value) { this.value = value;}

    @com.fasterxml.jackson.annotation.JsonCreator
    public String getValue() {
        return value;
    }

    public static UserNotificationType fromValue(String value) {
        for(UserNotificationType nType : UserNotificationType.values()) {
            if(nType.value.equalsIgnoreCase(value)) {
                return nType;
            }
        }
        return OTHER;
    }


}
