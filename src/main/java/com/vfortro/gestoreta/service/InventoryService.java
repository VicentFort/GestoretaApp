package com.vfortro.gestoreta.service;

import com.vfortro.gestoreta.conversor.InventoryItemConversor;
import com.vfortro.gestoreta.conversor.StoreConversor;
import com.vfortro.gestoreta.dto.inventory.items.InventoryItemCreateDto;
import com.vfortro.gestoreta.dto.inventory.items.InventoryItemInfoDto;
import com.vfortro.gestoreta.dto.inventory.items.InventoryItemUpdateDto;
import com.vfortro.gestoreta.dto.inventory.loans.ReturnLoanDto;
import com.vfortro.gestoreta.dto.inventory.stocks.InventoryMovementDto;
import com.vfortro.gestoreta.dto.inventory.stocks.InventoryMovementResultDto;
import com.vfortro.gestoreta.dto.inventory.stores.StoreCreateDto;
import com.vfortro.gestoreta.dto.inventory.stores.StoreInfoDto;
import com.vfortro.gestoreta.dto.inventory.stores.StoreUpdateDto;
import com.vfortro.gestoreta.dto.users.UserCreateDto;
import com.vfortro.gestoreta.exceptions.InsufficientStockException;
import com.vfortro.gestoreta.model.Falla;
import com.vfortro.gestoreta.model.enums.LoanState;
import com.vfortro.gestoreta.model.enums.MovementType;
import com.vfortro.gestoreta.model.inventory.*;
import com.vfortro.gestoreta.repository.FallaRepository;
import com.vfortro.gestoreta.repository.inventory.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class InventoryService {

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private InventoryMovementRepository movementRepository;

    @Autowired
    private LoanContactRepository contactRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private FallaRepository fallaRepository;

    @Autowired
    private StoreConversor storeConversor;
    @Autowired
    private InventoryItemConversor inventoryItemConversor;
    @Autowired
    private UserService userService;


    @Transactional
    public StoreInfoDto createStore(StoreCreateDto newStore, String email) throws AccessDeniedException {
        if(!userService.checkAdminAccess(email)) { throw new AccessDeniedException("Sense permís.");}
        Falla falla = fallaRepository.findFallaById(userService.readUser(email).getFallaId());
        Store toCreate = storeConversor.fromDto2Entity(newStore,falla);
        Store saved =  storeRepository.saveAndFlush(toCreate);
        return storeConversor.fromEntity2Dto(saved);
    }

    @Transactional
    public InventoryItemInfoDto createItem(InventoryItemCreateDto newItem, String email) throws AccessDeniedException {
        if(!userService.checkAdminAccess(email)) { throw new AccessDeniedException("Sense permís");}
        Falla falla = fallaRepository.findFallaById(userService.readUser(email).getFallaId());
        InventoryItem toSave = inventoryItemConversor.fromDto2Entity(newItem, falla);
        InventoryItem saved = inventoryItemRepository.saveAndFlush(toSave);
        return inventoryItemConversor.fromEntity2Dto(saved);
    }

    @Transactional
    public InventoryMovementResultDto processMovement(InventoryMovementDto dto, String email) throws AccessDeniedException, InsufficientStockException {
        if(!userService.checkAdminAccess(email)) { throw new AccessDeniedException("Sense permís"); }
        UserCreateDto user = userService.readUser(email);
        Stock stock = stockRepository.findByStoreStoreIdAndInventoryItemItemId(dto.getStoreId(), dto.getItemId())
                .orElseGet(() -> {
                    if (dto.getType() == MovementType.INCOMING) {
                        Stock newStock = new Stock();
                        newStock.setStore(storeRepository.findById(dto.getStoreId()).orElseThrow(() -> new EntityNotFoundException("No existeix el magaztem")));
                        newStock.setInventoryItem(inventoryItemRepository.findById(dto.getItemId()).orElseThrow(() -> new EntityNotFoundException("No existeix el magaztem")));
                        newStock.setAmount(0L);
                        return newStock;
                    }
                    throw new InsufficientStockException("No hi ha registre de stock per a este item!");
                });
        if(dto.getType() == MovementType.OUTGOING|| dto.getType() == MovementType.LOAN) {
            if(stock.getAmount() < dto.getAmount()) {
                throw new InsufficientStockException("Stock insuficient!");
            }
            stock.setAmount(stock.getAmount() - dto.getAmount());
        } else {
            stock.setAmount(stock.getAmount() + dto.getAmount());
        }
        Loan newLoan = null;
        if(dto.getType()==MovementType.LOAN) {
            newLoan = registerLoan(dto, stock.getInventoryItem());
        }

        InventoryMovement mov = new InventoryMovement();
        mov.setItem(stock.getInventoryItem());
        mov.setStore(stock.getStore());
        mov.setAmount(dto.getAmount());
        mov.setType(dto.getType());
        mov.setMessage(dto.getMessage());
        mov.setCreatedBy(user.getUserId()+" " +user.getSurname());
        mov.setLoan(newLoan);
        mov.setFalla(stock.getStore().getFalla());
        mov.setDate(LocalDateTime.now());

        movementRepository.saveAndFlush(mov);
        stockRepository.saveAndFlush(stock);
        InventoryMovementResultDto resultDto = new InventoryMovementResultDto();
        resultDto.setIncomingAmount(dto.getAmount());
        resultDto.setFinalAmount(stock.getAmount());
        resultDto.setItemId(stock.getInventoryItem().getItemId());
        resultDto.setItemName(stock.getInventoryItem().getName());
        resultDto.setStoreId(stock.getStore().getStoreId());
        resultDto.setStoreName(stock.getStore().getName());
        resultDto.setMessage(resultDto.toString());
        return resultDto;
    }

    @Transactional
    public Loan registerLoan(InventoryMovementDto dto, InventoryItem item) throws EntityNotFoundException {
        Loan loan = new Loan();
        loan.setAmount(dto.getAmount());
        loan.setAcquisitionDate(dto.getAdquisitionDate());
        loan.setIdealReturnDate(dto.getIdealReturnDate());
        loan.setState(LoanState.PENDING);
        loan.setFalla(item.getFalla());
        loan.setItem(item);
        LoanContact contact = contactRepository.findById(dto.getContactId()).orElseThrow(() -> new EntityNotFoundException("No existeix el contacte"));
        loan.setContact(contact);
        return loanRepository.save(loan);

    }

    @Transactional
    public void returnLoan(ReturnLoanDto dto, String email) throws AccessDeniedException {
        if(!userService.checkAdminAccess(email)) {
            throw new AccessDeniedException("Sense permís!");
        }
        UserCreateDto user = userService.readUser(email);

        Loan loan = loanRepository.findById(dto.getLoanId()).orElseThrow(() -> new EntityNotFoundException("Préstec no trobat"));

        if(loan.getState()== LoanState.RETURNED) {
            throw new IllegalStateException("Este préstec ja ha sigut retornat");
        }

        Stock stock = stockRepository.findByStoreStoreIdAndInventoryItemItemId(dto.getStoreId(),loan.getItem().getItemId())
                .orElseGet( () -> {
                    Stock newStock = new Stock();
                    newStock.setStore(storeRepository.getReferenceById(dto.getStoreId()));
                    newStock.setInventoryItem(loan.getItem());
                    newStock.setAmount(0L);
                    return newStock;
                });
        stock.setAmount(stock.getAmount() + dto.getAmount());
        stockRepository.saveAndFlush(stock);
        InventoryMovement mov = new InventoryMovement();
        mov.setItem(loan.getItem());
        mov.setStore(stock.getStore());
        mov.setAmount(dto.getAmount());
        mov.setType(MovementType.INCOMING);
        mov.setMessage("Retorn del préstec: " + dto.getMessage());
        mov.setCreatedBy(user.getUserId()+" " +user.getSurname());
        mov.setLoan(loan);
        movementRepository.saveAndFlush(mov);

        loan.setRealReturnDate(LocalDateTime.now());
        loan.setState(LoanState.RETURNED);
        loanRepository.save(loan);
    }

    @Transactional
    public void deleteStore(Long storeId, String email) throws AccessDeniedException {
        if(!userService.checkAdminAccess(email)) {throw new AccessDeniedException("Sense permís!");}
        if(!storeRepository.existsById(storeId)) {throw new  EntityNotFoundException("No existeix el magatzem");}
        storeRepository.deleteById(storeId);
    }

    @Transactional
    public void updateStore(StoreUpdateDto updatedStore, String email) throws AccessDeniedException, EntityNotFoundException {
        if(!userService.checkAdminAccess(email)) {throw new AccessDeniedException("Sense permís!");}
        Store storeToUpdate = storeRepository.findById(updatedStore.getStoreId()).orElseThrow(() -> new EntityNotFoundException("No existeix el magatzem"));

        if(updatedStore.getName() != null) storeToUpdate.setName(updatedStore.getName());
        if(updatedStore.getLocation() != null) storeToUpdate.setLocation(updatedStore.getLocation());

        storeRepository.saveAndFlush(storeToUpdate);

    }

    @Transactional
    public void deleteItem(Long itemId, String email) throws AccessDeniedException, EntityNotFoundException {
        if(!userService.checkAdminAccess(email)) {throw new AccessDeniedException("Sense permís!");}
        if(!inventoryItemRepository.existsById(itemId)) {throw new EntityNotFoundException("No existeix el item");}
        inventoryItemRepository.deleteById(itemId);
    }


    public void updateItem(InventoryItemUpdateDto updatedItem, String email) throws AccessDeniedException, EntityNotFoundException {
        if(!userService.checkAdminAccess(email)) { throw new AccessDeniedException("Sense permís!");}
        InventoryItem itemToUpdate = inventoryItemRepository.findById(updatedItem.getItemId()).orElseThrow(() -> new EntityNotFoundException("No existeix el item"));

        if(updatedItem.getName() != null) itemToUpdate.setName(updatedItem.getName());
        if(updatedItem.getDescription() != null) itemToUpdate.setDescription(updatedItem.getDescription());
        if(updatedItem.getItemCategory() != null) itemToUpdate.setItemCategory(updatedItem.getItemCategory());

        inventoryItemRepository.saveAndFlush(itemToUpdate);

    }
}
