package com.vfortro.gestoreta.conversor;

import com.vfortro.gestoreta.conversor.inventory.InventoryItemConversor;
import com.vfortro.gestoreta.conversor.inventory.InventoryMovementConversor;
import com.vfortro.gestoreta.conversor.inventory.LoanContactConversor;
import com.vfortro.gestoreta.conversor.inventory.StoreConversor;
import com.vfortro.gestoreta.dto.attendants.AttPrefAdminInfoDTO;
import com.vfortro.gestoreta.dto.events.EventInfoDTO;
import com.vfortro.gestoreta.dto.events.EventTagAdminInfoDTO;
import com.vfortro.gestoreta.dto.fallas.info.FallaAdminInfoDTO;
import com.vfortro.gestoreta.dto.fallas.FallaCreateDTO;
import com.vfortro.gestoreta.dto.inventory.items.InventoryItemInfoDTO;
import com.vfortro.gestoreta.dto.inventory.loans.contacts.LoanContactInfoDTO;
import com.vfortro.gestoreta.dto.inventory.stocks.InventoryMovementInfoDTO;
import com.vfortro.gestoreta.dto.inventory.stores.StoreInfoDTO;
import com.vfortro.gestoreta.dto.requests.RequestInfoDTO;
import com.vfortro.gestoreta.dto.users.info.UserInfoFallaDTO;
import com.vfortro.gestoreta.model.*;
import com.vfortro.gestoreta.model.inventory.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FallaConversor {
    private final EventConversor eventConversor;
    private final UserConversor userConversor;
    private final EventTagConversor eventTagConversor;
    private final RequestConversor requestConversor;
    @Autowired
    private StoreConversor storeConversor;

    @Autowired
    private LoanContactConversor loanContactConversor;

    @Autowired
    private InventoryItemConversor inventoryItemConversor;

    @Autowired
    private InventoryMovementConversor inventoryMovementConversor;


    public FallaConversor(EventConversor eventConversor, UserConversor userConversor, EventTagConversor eventTagConversor, RequestConversor requestConversor) {
        this.eventConversor = eventConversor;
        this.userConversor = userConversor;
        this.eventTagConversor = eventTagConversor;
        this.requestConversor = requestConversor;
    }


    public Falla fromDto2Entity(FallaCreateDTO dto) {
        Falla falla = new Falla();
        falla.setId(dto.getFallaId());
        falla.setName(dto.getName());
        falla.setCreationDate(dto.getCreationDate());
        falla.setShieldUrl(dto.getShieldUrl());
        return falla;
    }
    public FallaCreateDTO fromEntity2DTO(Falla falla) {
        FallaCreateDTO dto = new FallaCreateDTO();
        dto.setFallaId(falla.getId());
        dto.setName(falla.getName());
        dto.setCreationDate(falla.getCreationDate());
        dto.setShieldUrl(falla.getShieldUrl());
        return dto;
    }



    public FallaAdminInfoDTO fromEntity2AdminInfo(Falla falla) {
        List<EventTagAdminInfoDTO> tags = new ArrayList<>();
        List<UserInfoFallaDTO> users = new ArrayList<>();
        List<EventInfoDTO> events = new ArrayList<>();
        List<RequestInfoDTO> requests = new ArrayList<>();
        List<AttPrefAdminInfoDTO> prefs = new ArrayList<>();
        List<StoreInfoDTO> stores = new ArrayList<>();
        List<InventoryItemInfoDTO> inventoryItems = new ArrayList<>();
        List<LoanContactInfoDTO> contacts = new ArrayList<>();
        List<InventoryMovementInfoDTO> inventoryMovements = new ArrayList<>();
        FallaAdminInfoDTO dto = new FallaAdminInfoDTO();
        dto.setName(falla.getName());
        dto.setFallaId(falla.getId());

        for(Event event: falla.getEvents()) {
            events.add(eventConversor.fromEntity2InfoDto(event));
        }
        dto.setEvents(events);

        for(User user : falla.getUsers()) {
            users.add(userConversor.fromEntity2InfoFallaDto(user));
        }
        dto.setUsers(users);

        for(EventTag tag : falla.getEventTags()) {
            tags.add(eventTagConversor.fromEntity2Dto(tag));
        }
        dto.setTags(tags);

        for(Request req : falla.getRequests()) {
            requests.add(requestConversor.fromEntity2InfoDto(req));
        }
        dto.setRequests(requests);

        for(Store store : falla.getStores()) {
            stores.add(storeConversor.fromEntity2Dto(store));
        }
        dto.setStores(stores);

        for(InventoryItem item: falla.getItems()) {
            inventoryItems.add(inventoryItemConversor.fromEntity2Dto(item));
        }
        dto.setInventoryItems(inventoryItems);

        for(LoanContact contact : falla.getContacts()) {
            contacts.add(loanContactConversor.fromEntity2Dto(contact));
        }
        dto.setContacts(contacts);

        for(InventoryMovement movement : falla.getMovements()) {
            inventoryMovements.add(inventoryMovementConversor.fromEntity2Dto(movement));
        }
        dto.setInventoryMovements(inventoryMovements);
        return dto;

    }
}
