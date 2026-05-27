package com.vfortro.gestoreta.converters;

import com.vfortro.gestoreta.model.enums.UserNotificationType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserNotificationTypeConverter implements AttributeConverter<UserNotificationType, String> {
    @Override
    public String convertToDatabaseColumn(UserNotificationType userNotificationType) {
        return (userNotificationType == null) ? null : userNotificationType.getValue();
    }

    @Override
    public UserNotificationType convertToEntityAttribute(String s) {
        return (s == null) ?  null :UserNotificationType.fromValue(s);
    }
}
