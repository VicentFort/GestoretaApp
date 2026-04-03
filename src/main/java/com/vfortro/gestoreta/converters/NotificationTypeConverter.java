package com.vfortro.gestoreta.converters;

import com.vfortro.gestoreta.model.enums.NotificationType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class NotificationTypeConverter implements AttributeConverter<NotificationType, String> {

    @Override
    public String convertToDatabaseColumn(NotificationType notificationType) {
        return (notificationType == null) ? null : notificationType.getValue();
    }

    @Override
    public NotificationType convertToEntityAttribute(String s) {
        return (s == null) ? null : NotificationType.fromValue(s);
    }
}
