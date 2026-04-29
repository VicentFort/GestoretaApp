package com.vfortro.gestoreta.converters;

import com.vfortro.gestoreta.model.enums.PaymentLogType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentLogTypeConverter implements AttributeConverter<PaymentLogType, String> {
    @Override
    public String convertToDatabaseColumn(PaymentLogType paymentLogType) {
        return (paymentLogType == null) ? null : paymentLogType.getValue();
    }

    @Override
    public PaymentLogType convertToEntityAttribute(String s) {
        return (s == null) ? null : PaymentLogType.fromValue(s);
    }
}
