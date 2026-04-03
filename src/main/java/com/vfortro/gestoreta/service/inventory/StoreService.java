package com.vfortro.gestoreta.service.inventory;

import com.vfortro.gestoreta.conversor.StoreConversor;
import com.vfortro.gestoreta.dto.inventory.stores.StoreCreateDto;
import com.vfortro.gestoreta.dto.inventory.stores.StoreInfoDto;
import com.vfortro.gestoreta.model.Falla;
import com.vfortro.gestoreta.model.inventory.Store;
import com.vfortro.gestoreta.repository.FallaRepository;
import com.vfortro.gestoreta.repository.inventory.StoreRepository;
import com.vfortro.gestoreta.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private FallaRepository fallaRepository;

    @Autowired
    private StoreConversor storeConversor;

    @Autowired
    private UserService userService;


    public StoreInfoDto createStore(StoreCreateDto newStore, String email) throws AccessDeniedException {
        if(!userService.checkAdminAccess(email)) { throw new AccessDeniedException("Sense permís.");}
        Falla falla = fallaRepository.findFallaById(userService.readUser(email).getFallaId());
        Store toCreate = storeConversor.fromDto2Entity(newStore,falla);
        Store saved =  storeRepository.saveAndFlush(toCreate);
        return storeConversor.fromEntity2Dto(saved);
    }
}
