package com.vfortro.gestoreta.conversor;

import com.vfortro.gestoreta.dto.users.notifications.NotificationInfoDTO;
import com.vfortro.gestoreta.model.UserNotification;
import org.springframework.stereotype.Component;

@Component
public class UserNotificationCovnersor {
     public NotificationInfoDTO fromEntity2Dto(UserNotification notification) {
         NotificationInfoDTO dto = new NotificationInfoDTO();
         dto.setId(notification.getNotificationId());
         dto.setMessage(notification.getMessage());
         dto.setType(notification.getType().getValue());
         dto.setRead(notification.getRead());
         dto.setDate(notification.getDate());
         dto.setUserId(notification.getUser().getId());
         dto.setUserName(notification.getUser().getName() + " " + notification.getUser().getSurname());
         if(notification.getFalla() != null) {
             dto.setFallaId(notification.getFalla().getId());
             dto.setFalla(notification.getFalla().getName());
         }
         if(notification.getEvent() != null) {
             dto.setEventId(notification.getEvent().getId());
             dto.setEvent(notification.getEvent().getTitle());
         }

         if (notification.getPayment() != null) {
             dto.setPaymentId(notification.getPayment().getPaymentId());
             dto.setPaymentMessage(notification.getPayment().getMessage());
         }
         if(notification.getCoupon() != null) {
             dto.setCouponId(notification.getCoupon().getCouponId());
             dto.setCoupon(notification.getCoupon().getName());
         }
         return dto;
     }
}
