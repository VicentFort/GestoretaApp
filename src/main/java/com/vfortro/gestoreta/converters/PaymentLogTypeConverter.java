package com.vfortro.gestoreta.converters;

import com.vfortro.gestoreta.model.enums.PaymentType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentLogTypeConverter implements AttributeConverter<PaymentType, String> {
    @Override
    public String convertToDatabaseColumn(PaymentType paymentLogType) {
        return (paymentLogType == null) ? null : paymentLogType.getValue();
    }

    @Override
    public PaymentType convertToEntityAttribute(String s) {
        return (s == null) ? null : PaymentType.fromValue(s);
    }
}
