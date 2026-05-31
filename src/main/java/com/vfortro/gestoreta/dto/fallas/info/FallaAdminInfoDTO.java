package com.vfortro.gestoreta.dto.fallas.info;

import com.vfortro.gestoreta.dto.attendants.AttPrefAdminInfoDTO;
import com.vfortro.gestoreta.dto.events.EventInfoDTO;
import com.vfortro.gestoreta.dto.events.EventTagAdminInfoDTO;
import com.vfortro.gestoreta.dto.inventory.items.InventoryItemInfoDTO;
import com.vfortro.gestoreta.dto.inventory.loans.contacts.LoanContactInfoDTO;
import com.vfortro.gestoreta.dto.inventory.stocks.InventoryMovementInfoDTO;
import com.vfortro.gestoreta.dto.inventory.stores.StoreInfoDTO;
import com.vfortro.gestoreta.dto.payments.info.CouponFallaInfoDTO;
import com.vfortro.gestoreta.dto.payments.info.PaymentInfoDTO;
import com.vfortro.gestoreta.dto.requests.RequestInfoDTO;
import com.vfortro.gestoreta.dto.users.info.UserInfoFallaDTO;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Data
public class FallaAdminInfoDTO {
    private Long fallaId;
    private String name;
    private String shield;
    private List<EventTagAdminInfoDTO> tags;
    private List<UserInfoFallaDTO> users;
    private List<EventInfoDTO> events;
    private List<RequestInfoDTO> requests;
    private List<AttPrefAdminInfoDTO> attendants;
    private List<StoreInfoDTO> stores;
    private List<InventoryItemInfoDTO> inventoryItems;
    private List<LoanContactInfoDTO> contacts;
    private List<InventoryMovementInfoDTO> inventoryMovements;
    private List<PaymentInfoDTO> payments;
    private List<CouponFallaInfoDTO> coupons;

}
