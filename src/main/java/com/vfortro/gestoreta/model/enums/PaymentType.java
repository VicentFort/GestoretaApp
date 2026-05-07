package com.vfortro.gestoreta.model.enums;

public enum PaymentType {
    COUPON_SOLD("Venda de tiquet"),
    COUPON_EXCHANGED("Bescanvi de tiquet"),
    FEE_PAYMENT("Pagament de quota"),
    SUBSCRIBER_FEE_PAYMENT("Pagament d.abonats"),
    LOTTERY_PAYMENT("Pagament de loteria"),
    GRANT_PAYMENT("Pagament de subvenció"),
    PRIZE_REWARD_PAYMENT("Pagament de recompensa per premi"),
    MAINTENANCE_EXPENSE("Gasto de manteniment"),
    RENT_EXPENSE("Gasto de lloguer"),
    ADMINISTRATIVE_EXPENSE("Gasto administratiu"),
    LOTTERY_EXPENSE("Gasto de loteria"),
    PYROTECHNICS_EXPENSE("Gasto de pirotècnia"),
    STAFF_EXPENSE("Gasto de personal"),
    ARTS_AND_CRAFTS_EXPENSE("Gasto de material artístic o plàstic"),
    OFFICE_MATERIAL_EXPENSE("Gasto de material d.oficina"),
    SW_HW_EXPENSE("Gasto informàtic o electrònic"),
    DRINK_FOOD_EXPENSE("Gasto de beguda o menjar"),
    SUBSCRIBER_EXPENSE("Gasto d.abonats"),
    DONATION("Donació"),
    OTHER_EXPENSES("Altre gasto"),
    OTHER_PAYMENTS("Altre pagament"),
    EVENT_PAYMENT("Pagament d.event");

    private final String value;

    PaymentType(String value) { this.value = value; }

    @com.fasterxml.jackson.annotation.JsonCreator
    public String getValue() {
        return value;
    }

    public static PaymentType fromValue(String value) {
        for(PaymentType type: PaymentType.values()) {
            if(type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return OTHER_EXPENSES;
    }

}
