package com.vfortro.gestoreta.dto.inventory.loans;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@Data
public class LoanInfoDTO {
    private Long id;
    private Long amount;
    private LocalDateTime acquisitionDate;
    private LocalDateTime idealReturnDate;
    private LocalDateTime realReturnDate;
    private String state;
    private Long contactId;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private String contactDniCif;
    private Long itemId;
    private String itemName;
    private Boolean hasDelayedNotifications;
}
