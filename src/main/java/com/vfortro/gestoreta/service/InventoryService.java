package com.vfortro.gestoreta.service;

import com.vfortro.gestoreta.conversor.inventory.*;
import com.vfortro.gestoreta.dto.inventory.items.InventoryItemCreateDTO;
import com.vfortro.gestoreta.dto.inventory.items.InventoryItemInfoDTO;
import com.vfortro.gestoreta.dto.inventory.items.InventoryItemUpdateDTO;
import com.vfortro.gestoreta.dto.inventory.loans.LoanInfoDTO;
import com.vfortro.gestoreta.dto.inventory.loans.ReturnLoanDTO;
import com.vfortro.gestoreta.dto.inventory.loans.contacts.LoanContactCreateDTO;
import com.vfortro.gestoreta.dto.inventory.loans.contacts.LoanContactInfoDTO;
import com.vfortro.gestoreta.dto.inventory.loans.contacts.LoanContactUpdateDTO;
import com.vfortro.gestoreta.dto.inventory.stocks.InventoryMovementRequestDTO;
import com.vfortro.gestoreta.dto.inventory.stocks.InventoryMovementInfoDTO;
import com.vfortro.gestoreta.dto.inventory.stores.StoreCreateDTO;
import com.vfortro.gestoreta.dto.inventory.stores.StoreInfoDTO;
import com.vfortro.gestoreta.dto.inventory.stores.StoreUpdateDTO;
import com.vfortro.gestoreta.dto.users.UserCreateDTO;
import com.vfortro.gestoreta.exceptions.InsufficientStockException;
import com.vfortro.gestoreta.model.Falla;
import com.vfortro.gestoreta.model.User;
import com.vfortro.gestoreta.model.enums.LoanState;
import com.vfortro.gestoreta.model.enums.MovementType;
import com.vfortro.gestoreta.model.enums.NotificationType;
import com.vfortro.gestoreta.model.inventory.*;
import com.vfortro.gestoreta.repository.FallaRepository;
import com.vfortro.gestoreta.repository.inventory.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

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

    @Autowired
    private LoanContactConversor contactConversor;

    @Autowired
    private InventoryMovementConversor movementConversor;

    @Autowired
    private LoanConversor loanConversor;

    @Autowired
    private LoanNotificationRepository notificationRepository;



    @Transactional
    public StoreInfoDTO createStore(StoreCreateDTO newStore, String email) throws AccessDeniedException {
        if(!userService.checkAdminAccess(email)) { throw new AccessDeniedException("Sense permís.");}
        Falla falla = fallaRepository.findFallaById(userService.readUser(email).getFallaId());
        Store toCreate = storeConversor.fromDto2Entity(newStore,falla);
        Store saved =  storeRepository.saveAndFlush(toCreate);
        return storeConversor.fromEntity2Dto(saved);
    }

    @Transactional
    public InventoryItemInfoDTO createItem(InventoryItemCreateDTO newItem, String email) throws AccessDeniedException {
        if(!userService.checkAdminAccess(email)) { throw new AccessDeniedException("Sense permís");}
        Falla falla = fallaRepository.findFallaById(userService.readUser(email).getFallaId());
        InventoryItem toSave = inventoryItemConversor.fromDto2Entity(newItem, falla);
        InventoryItem saved = inventoryItemRepository.saveAndFlush(toSave);
        return inventoryItemConversor.fromEntity2Dto(saved);
    }

    @Transactional
    public InventoryMovementInfoDTO processMovement(InventoryMovementRequestDTO dto, String email) throws AccessDeniedException, InsufficientStockException {
        if(!userService.checkAdminAccess(email)) { throw new AccessDeniedException("Sense permís"); }
        UserCreateDTO user = userService.readUser(email);
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
        mov.setCreatedBy(user.getName()+" " +user.getSurname());
        mov.setLoan(newLoan);
        mov.setFalla(stock.getStore().getFalla());
        mov.setDate(LocalDateTime.now());

        InventoryMovement saved = movementRepository.saveAndFlush(mov);
        if(stock.getAmount()>0L) {
            stockRepository.saveAndFlush(stock);
        } else {
            stockRepository.deleteById(stock.getStockId());
        }

        InventoryMovementInfoDTO infoDto = movementConversor.fromEntity2Dto(saved);
        return infoDto;
    }

    @Transactional
    public void processMovement(InventoryMovementRequestDTO dto, User manager) throws AccessDeniedException, InsufficientStockException {
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
        mov.setCreatedBy(manager.getName()+ " " + manager.getSurname());
        mov.setLoan(newLoan);
        mov.setFalla(stock.getStore().getFalla());
        mov.setDate(LocalDateTime.now());

        InventoryMovement saved = movementRepository.saveAndFlush(mov);
        if(stock.getAmount()>0L) {
            stockRepository.saveAndFlush(stock);
        } else {
            stockRepository.deleteById(stock.getStockId());
        }

        InventoryMovementInfoDTO infoDto = movementConversor.fromEntity2Dto(saved);
    }

    @Transactional
    public Loan registerLoan(InventoryMovementRequestDTO dto, InventoryItem item) throws EntityNotFoundException {
        Loan loan = new Loan();
        loan.setAmount(dto.getAmount());
        loan.setAcquisitionDate(dto.getAdquisitionDate());
        loan.setIdealReturnDate(dto.getIdealReturnDate());
        loan.setState(LoanState.PENDING);
        loan.setFalla(item.getFalla());
        loan.setItem(item);
        LoanContact contact = contactRepository.findById(dto.getContactId()).orElseThrow(() -> new EntityNotFoundException("No existeix el contacte"));
        loan.setContact(contact);
        Loan saved = loanRepository.save(loan);
        registerLoanNotification(saved, NotificationType.CONFIRMATION);
        return saved;

    }

    @Transactional
    public LoanNotification registerLoanNotification(Loan loan, NotificationType type) throws EntityNotFoundException {
        LoanNotification notification = new LoanNotification();
        notification.setLoan(loan);
        notification.setContact(loan.getContact());
        notification.setDate(LocalDateTime.now());
        notification.setType(type);
        notification.setSuccessful(true);
        LoanNotification saved = notificationRepository.save(notification);
        return saved;
    }

    @Transactional
    public LoanInfoDTO returnLoan(ReturnLoanDTO dto, String email) throws AccessDeniedException, IllegalStateException {
        if(!userService.checkAdminAccess(email)) {
            throw new AccessDeniedException("Sense permís!");
        }
        UserCreateDTO user = userService.readUser(email);

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
        mov.setFalla(stock.getStore().getFalla());
        mov.setDate(LocalDateTime.now());
        mov.setType(MovementType.INCOMING);
        mov.setMessage("Retorn del prèstec: " + dto.getMessage());
        mov.setCreatedBy(user.getName()+" " +user.getSurname());
        mov.setLoan(loan);
        movementRepository.saveAndFlush(mov);

        loan.setRealReturnDate(LocalDateTime.now());
        loan.setState(LoanState.RETURNED);
        Loan saved = loanRepository.save(loan);

        return loanConversor.fromEntity2Dto(saved);
    }

    @Transactional
    public void deleteStore(Long storeId, String email) throws AccessDeniedException {
        if(!userService.checkAdminAccess(email)) {throw new AccessDeniedException("Sense permís!");}
        if(!storeRepository.existsById(storeId)) {throw new  EntityNotFoundException("No existeix el magatzem");}
        storeRepository.deleteById(storeId);
    }

    @Transactional
    public void updateStore(StoreUpdateDTO updatedStore, String email) throws AccessDeniedException, EntityNotFoundException {
        if(!userService.checkAdminAccess(email)) {throw new AccessDeniedException("Sense permís!");}
        Store storeToUpdate = storeRepository.findById(updatedStore.getStoreId()).orElseThrow(() -> new EntityNotFoundException("No existeix el magatzem"));

        if(updatedStore.getName() != null) storeToUpdate.setName(updatedStore.getName());
        if(updatedStore.getLocation() != null) storeToUpdate.setLocation(updatedStore.getLocation());
        if(updatedStore.getEnabled() != null) storeToUpdate.setEnabled(updatedStore.getEnabled());


        storeRepository.saveAndFlush(storeToUpdate);

    }

    @Transactional
    public void deleteItem(Long itemId, String email) throws AccessDeniedException, EntityNotFoundException {
        if(!userService.checkAdminAccess(email)) {throw new AccessDeniedException("Sense permís!");}
        if(!inventoryItemRepository.existsById(itemId)) {throw new EntityNotFoundException("No existeix el item");}
        inventoryItemRepository.deleteById(itemId);
    }


    public void updateItem(InventoryItemUpdateDTO updatedItem, String email) throws AccessDeniedException, EntityNotFoundException {
        if(!userService.checkAdminAccess(email)) { throw new AccessDeniedException("Sense permís!");}
        InventoryItem itemToUpdate = inventoryItemRepository.findById(updatedItem.getItemId()).orElseThrow(() -> new EntityNotFoundException("No existeix el item"));

        if(updatedItem.getName() != null) itemToUpdate.setName(updatedItem.getName());
        if(updatedItem.getDescription() != null) itemToUpdate.setDescription(updatedItem.getDescription());
        if(updatedItem.getItemCategory() != null) itemToUpdate.setItemCategory(updatedItem.getItemCategory());
        if(updatedItem.getEnabled() != null) itemToUpdate.setEnabled(updatedItem.getEnabled());

        inventoryItemRepository.saveAndFlush(itemToUpdate);

    }

    @Transactional
    public LoanContactInfoDTO createContact(LoanContactCreateDTO contact, String email) throws AccessDeniedException {
        if(!userService.checkAdminAccess(email)) { throw new AccessDeniedException("Sense permís");}
        Falla falla = fallaRepository.findFallaById(userService.readUser(email).getFallaId());
        LoanContact toCreate = contactConversor.fromDto2Entity(contact, falla);
        LoanContact saved = contactRepository.saveAndFlush(toCreate);
        return contactConversor.fromEntity2Dto(saved);
    }

    @Transactional
    public void updateContact(LoanContactUpdateDTO contact, String email) throws AccessDeniedException, EntityNotFoundException {
        if(!userService.checkAdminAccess(email)) { throw new AccessDeniedException("Sense permís"); }
        LoanContact toUpdate = contactRepository.findById(contact.getId()).orElseThrow(() -> new EntityNotFoundException("No existeix el contacte"));

        if(contact.getName() != null) toUpdate.setName(contact.getName());
        if(contact.getEmail() != null) toUpdate.setEmail(contact.getEmail());
        if(contact.getPhone() != null) toUpdate.setPhone(contact.getPhone());
        if(contact.getDniCif() != null) toUpdate.setDniCif(contact.getDniCif());

        contactRepository.saveAndFlush(toUpdate);
    }
}
