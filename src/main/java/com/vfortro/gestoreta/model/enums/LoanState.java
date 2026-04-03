package com.vfortro.gestoreta.model.enums;

public enum LoanState {
    PENDING("Pendent"),
    RETURNED("Tornat"),
    DELAYED("Atrassat"),
    LOST("Pergut");

    private final String value;
    LoanState(String value) {
        this.value = value;
    }

    @com.fasterxml.jackson.annotation.JsonValue
    public String getValue() {
        return value;
    }

    public static LoanState fromString(String value) {
        for(LoanState loanState : LoanState.values()) {
            if(loanState.value.equalsIgnoreCase(value)) {
                return loanState;
            }
        }
        return PENDING;
    }
}
