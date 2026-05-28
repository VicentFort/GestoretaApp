package com.vfortro.gestoreta.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.vfortro.gestoreta.conversor.inventory.*;
import com.vfortro.gestoreta.dto.inventory.items.*;
import com.vfortro.gestoreta.dto.inventory.loans.*;
import com.vfortro.gestoreta.dto.inventory.loans.contacts.*;
import com.vfortro.gestoreta.dto.inventory.stocks.*;
import com.vfortro.gestoreta.dto.inventory.stores.*;
import com.vfortro.gestoreta.dto.users.UserCreateDTO;
import com.vfortro.gestoreta.exceptions.InsufficientStockException;
import com.vfortro.gestoreta.model.Falla;
import com.vfortro.gestoreta.model.enums.ItemCategory;
import com.vfortro.gestoreta.model.enums.LoanState;
import com.vfortro.gestoreta.model.enums.MovementType;
import com.vfortro.gestoreta.model.enums.NotificationType;
import com.vfortro.gestoreta.model.inventory.*;
import com.vfortro.gestoreta.repository.FallaRepository;
import com.vfortro.gestoreta.repository.inventory.*;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock private InventoryItemRepository inventoryItemRepository;
    @Mock private StoreRepository storeRepository;
    @Mock private StockRepository stockRepository;
    @Mock private InventoryMovementRepository movementRepository;
    @Mock private LoanContactRepository contactRepository;
    @Mock private LoanRepository loanRepository;
    @Mock private FallaRepository fallaRepository;
    @Mock private LoanNotificationRepository notificationRepository;

    @Mock private StoreConversor storeConversor;
    @Mock private InventoryItemConversor inventoryItemConversor;
    @Mock private LoanContactConversor contactConversor;
    @Mock private InventoryMovementConversor movementConversor;
    @Mock private LoanConversor loanConversor;
    @Mock private UserService userService;

    @InjectMocks
    private InventoryService inventoryService;

    private final String email = "vicent@falla.com";
    private Falla sampleFalla;
    private UserCreateDTO sampleUserDto;
    private Store sampleStore;
    private InventoryItem sampleItem;
    private Stock sampleStock;
    private LoanContact sampleContact;
    private Loan sampleLoan;
    private UserCreateDTO mockUser;
    @BeforeEach
    void setUp() {
        sampleFalla = new Falla();
        sampleFalla.setId(1L);

        sampleUserDto = new UserCreateDTO();
        sampleUserDto.setFallaId(1L);
        sampleUserDto.setName("Vicent");
        sampleUserDto.setSurname("Fortro");

        sampleStore = new Store();
        sampleStore.setStoreId(10L);
        sampleStore.setFalla(sampleFalla);

        sampleItem = new InventoryItem();
        sampleItem.setItemId(20L);
        sampleItem.setFalla(sampleFalla);

        sampleStock = new Stock();
        sampleStock.setStockId(30L);
        sampleStock.setStore(sampleStore);
        sampleStock.setInventoryItem(sampleItem);
        sampleStock.setAmount(100L); // Stock inicial de 100 unidades

        sampleContact = new LoanContact();
        sampleContact.setContactId(40L);

        sampleLoan = new Loan();
        sampleLoan.setLoanId(50L);
        sampleLoan.setItem(sampleItem);
        sampleLoan.setState(LoanState.PENDING);

        mockUser = new UserCreateDTO();
        mockUser.setName("Pepe");
        mockUser.setSurname("Pérez");
    }

    // ==========================================
    // SECCIÓN: MAGACENES (STORES)
    // ==========================================
    @Test
    void createStore_NoManagerAccess_ShouldThrowAccessDeniedException() throws IllegalAccessException {
        when(userService.checkManagerAccess(email)).thenReturn(false);
        assertThrows(AccessDeniedException.class, () -> inventoryService.createStore(new StoreCreateDTO(), email));
    }

    @Test
    void createStore_Success_ShouldReturnDto() throws Exception {
        StoreCreateDTO createDto = new StoreCreateDTO();
        StoreInfoDTO infoDto = new StoreInfoDTO();

        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(userService.readUser(email)).thenReturn(sampleUserDto);
        when(fallaRepository.findFallaById(1L)).thenReturn(sampleFalla);
        when(storeConversor.fromDto2Entity(createDto, sampleFalla)).thenReturn(sampleStore);
        when(storeRepository.saveAndFlush(sampleStore)).thenReturn(sampleStore);
        when(storeConversor.fromEntity2Dto(sampleStore)).thenReturn(infoDto);

        assertNotNull(inventoryService.createStore(createDto, email));
    }

    @Test
    void deleteStore_NotFound_ShouldThrowEntityNotFoundException() throws IllegalAccessException {
        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(storeRepository.existsById(10L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> inventoryService.deleteStore(10L, email));
    }

    @Test
    void updateStore_Success_ShouldModifyFieldsAndSave() throws Exception {
        StoreUpdateDTO updateDto = new StoreUpdateDTO();
        updateDto.setStoreId(10L);
        updateDto.setName("Magatzem Nou");

        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(storeRepository.findById(10L)).thenReturn(Optional.of(sampleStore));

        inventoryService.updateStore(updateDto, email);

        assertEquals("Magatzem Nou", sampleStore.getName());
        verify(storeRepository, times(1)).saveAndFlush(sampleStore);
    }

    // ==========================================
    // SECCIÓN: ARTÍCULOS (ITEMS)
    // ==========================================
    @Test
    void createItem_Success_ShouldReturnDto() throws Exception {
        InventoryItemCreateDTO createDto = new InventoryItemCreateDTO();
        InventoryItemInfoDTO infoDto = new InventoryItemInfoDTO();

        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(userService.readUser(email)).thenReturn(sampleUserDto);
        when(fallaRepository.findFallaById(1L)).thenReturn(sampleFalla);
        when(inventoryItemConversor.fromDto2Entity(createDto, sampleFalla)).thenReturn(sampleItem);
        when(inventoryItemRepository.saveAndFlush(sampleItem)).thenReturn(sampleItem);
        when(inventoryItemConversor.fromEntity2Dto(sampleItem)).thenReturn(infoDto);

        assertNotNull(inventoryService.createItem(createDto, email));
    }

    // ==========================================
    // SECCIÓN: MOVIMIENTOS DE INVENTARIO
    // ==========================================
    @Test
    void processMovement_NoStockRecordForOutgoing_ShouldThrowInsufficientStockException() throws IllegalAccessException {
        InventoryMovementRequestDTO dto = new InventoryMovementRequestDTO();
        dto.setStoreId(10L);
        dto.setItemId(20L);
        dto.setType(MovementType.OUTGOING); // Intento sacar sin que exista registro

        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(userService.readUser(email)).thenReturn(sampleUserDto);
        when(stockRepository.findByStoreStoreIdAndInventoryItemItemId(10L, 20L)).thenReturn(Optional.empty());

        assertThrows(InsufficientStockException.class, () -> inventoryService.processMovement(dto, email));
    }

    @Test
    void processMovement_InsufficientAmount_ShouldThrowInsufficientStockException() throws IllegalAccessException {
        InventoryMovementRequestDTO dto = new InventoryMovementRequestDTO();
        dto.setStoreId(10L);
        dto.setItemId(20L);
        dto.setType(MovementType.OUTGOING);
        dto.setAmount(150L); // Tenemos 100 en sampleStock

        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(userService.readUser(email)).thenReturn(sampleUserDto);
        when(stockRepository.findByStoreStoreIdAndInventoryItemItemId(10L, 20L)).thenReturn(Optional.of(sampleStock));

        assertThrows(InsufficientStockException.class, () -> inventoryService.processMovement(dto, email));
    }

    @Test
    void processMovement_SuccessOutgoing_ShouldReduceStockAndSave() throws Exception {
        InventoryMovementRequestDTO dto = new InventoryMovementRequestDTO();
        dto.setStoreId(10L);
        dto.setItemId(20L);
        dto.setType(MovementType.OUTGOING);
        dto.setAmount(40L); // Quedarán 60
        dto.setMessage("Eixida de carpes");

        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(userService.readUser(email)).thenReturn(sampleUserDto);
        when(stockRepository.findByStoreStoreIdAndInventoryItemItemId(10L, 20L)).thenReturn(Optional.of(sampleStock));
        when(movementRepository.saveAndFlush(any(InventoryMovement.class))).thenReturn(new InventoryMovement());

        inventoryService.processMovement(dto, email);

        assertEquals(60L, sampleStock.getAmount());
        verify(stockRepository, times(1)).saveAndFlush(sampleStock);
        verify(stockRepository, never()).deleteById(any());
    }

    @Test
    void processMovement_StockReachesZero_ShouldDeleteStockRecord() throws Exception {
        InventoryMovementRequestDTO dto = new InventoryMovementRequestDTO();
        dto.setStoreId(10L);
        dto.setItemId(20L);
        dto.setType(MovementType.OUTGOING);
        dto.setAmount(100L); // Vaciamos por completo el stock

        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(userService.readUser(email)).thenReturn(sampleUserDto);
        when(stockRepository.findByStoreStoreIdAndInventoryItemItemId(10L, 20L)).thenReturn(Optional.of(sampleStock));
        when(movementRepository.saveAndFlush(any(InventoryMovement.class))).thenReturn(new InventoryMovement());

        inventoryService.processMovement(dto, email);

        assertEquals(0L, sampleStock.getAmount());
        verify(stockRepository, times(1)).deleteById(sampleStock.getStockId());
        verify(stockRepository, never()).saveAndFlush(sampleStock);
    }

    @Test
    void processMovement_IncomingWithNoPriorStock_ShouldCreateNewStockRecord() throws Exception {
        InventoryMovementRequestDTO dto = new InventoryMovementRequestDTO();
        dto.setStoreId(10L);
        dto.setItemId(20L);
        dto.setType(MovementType.INCOMING);
        dto.setAmount(15L);

        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(userService.readUser(email)).thenReturn(sampleUserDto);
        // Retornamos vacío para activar el bloque orElseGet
        when(stockRepository.findByStoreStoreIdAndInventoryItemItemId(10L, 20L)).thenReturn(Optional.empty());
        when(storeRepository.findById(10L)).thenReturn(Optional.of(sampleStore));
        when(inventoryItemRepository.findById(20L)).thenReturn(Optional.of(sampleItem));
        when(movementRepository.saveAndFlush(any(InventoryMovement.class))).thenReturn(new InventoryMovement());

        inventoryService.processMovement(dto, email);

        // Verificamos que se guarda un Stock con monto 15 debido al incremento del incoming
        verify(stockRepository, times(1)).saveAndFlush(argThat(stock -> stock.getAmount() == 15L));
    }

    @Test
    void processMovement_LoanType_ShouldRegisterLoanAndNotification() throws Exception {
        InventoryMovementRequestDTO dto = new InventoryMovementRequestDTO();
        dto.setStoreId(10L);
        dto.setItemId(20L);
        dto.setType(MovementType.LOAN);
        dto.setAmount(5L);
        dto.setContactId(40L);

        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(userService.readUser(email)).thenReturn(sampleUserDto);
        when(stockRepository.findByStoreStoreIdAndInventoryItemItemId(10L, 20L)).thenReturn(Optional.of(sampleStock));
        when(contactRepository.findById(40L)).thenReturn(Optional.of(sampleContact));
        when(loanRepository.save(any(Loan.class))).thenReturn(sampleLoan);
        when(movementRepository.saveAndFlush(any(InventoryMovement.class))).thenReturn(new InventoryMovement());

        inventoryService.processMovement(dto, email);

        // Se reduce el stock en 5 unidades
        assertEquals(95L, sampleStock.getAmount());
        // Se comprueba que se registra el préstamo y la correspondiente alerta de confirmación
        verify(loanRepository, times(1)).save(any(Loan.class));
        verify(notificationRepository, times(1)).save(argThat(notification -> notification.getType() == NotificationType.CONFIRMATION));
    }

    // ==========================================
    // SECCIÓN: RETORNO DE PRÉSTAMOS (LOANS)
    // ==========================================
    @Test
    void returnLoan_AlreadyReturned_ShouldThrowIllegalStateException() throws IllegalAccessException {
        ReturnLoanDTO dto = new ReturnLoanDTO();
        dto.setLoanId(50L);
        sampleLoan.setState(LoanState.RETURNED); // Estado ya retornado

        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(userService.readUser(email)).thenReturn(sampleUserDto);
        when(loanRepository.findById(50L)).thenReturn(Optional.of(sampleLoan));

        assertThrows(IllegalStateException.class, () -> inventoryService.returnLoan(dto, email));
    }

    @Test
    void returnLoan_Success_ShouldRestoreStockAndMarkAsReturned() throws Exception {
        ReturnLoanDTO dto = new ReturnLoanDTO();
        dto.setLoanId(50L);
        dto.setStoreId(10L);
        dto.setAmount(5L);
        dto.setMessage("Tornat a temps");

        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(userService.readUser(email)).thenReturn(sampleUserDto);
        when(loanRepository.findById(50L)).thenReturn(Optional.of(sampleLoan));
        // Simulamos que el almacén ya tiene un registro de ese stock
        when(stockRepository.findByStoreStoreIdAndInventoryItemItemId(10L, 20L)).thenReturn(Optional.of(sampleStock));
        when(loanRepository.save(sampleLoan)).thenReturn(sampleLoan);

        inventoryService.returnLoan(dto, email);

        // Tenía 100 de stock + 5 devueltos = 105
        assertEquals(105L, sampleStock.getAmount());
        assertEquals(LoanState.RETURNED, sampleLoan.getState());
        assertNotNull(sampleLoan.getRealReturnDate());

        verify(stockRepository, times(1)).saveAndFlush(sampleStock);
        verify(movementRepository, times(1)).saveAndFlush(argThat(movement ->
                movement.getType() == MovementType.INCOMING &&
                        movement.getMessage().contains("Retorn del préstec")
        ));
    }

    // ==========================================
    // SECCIÓN: CONTACTOS DE PRÉSTAMO (CONTACTS)
    // ==========================================
    @Test
    void updateContact_NotFound_ShouldThrowEntityNotFoundException() throws IllegalAccessException {
        LoanContactUpdateDTO updateDto = new LoanContactUpdateDTO();
        updateDto.setId(999L);

        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(contactRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> inventoryService.updateContact(updateDto, email));
    }

    @Test
    void updateContact_Success_ShouldUpdateFieldsAndSave() throws Exception {
        LoanContactUpdateDTO updateDto = new LoanContactUpdateDTO();
        updateDto.setId(40L);
        updateDto.setName("Pepe");
        updateDto.setPhone("666777888");

        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(contactRepository.findById(40L)).thenReturn(Optional.of(sampleContact));

        inventoryService.updateContact(updateDto, email);

        assertEquals("Pepe", sampleContact.getName());
        assertEquals("666777888", sampleContact.getPhone());
        verify(contactRepository, times(1)).saveAndFlush(sampleContact);
    }

    @Test
    void processMovement_NoManagerAccess_ShouldThrowAccessDeniedException() throws IllegalAccessException {
        InventoryMovementRequestDTO dto = new InventoryMovementRequestDTO();
        when(userService.checkManagerAccess(email)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () ->
                inventoryService.processMovement(dto, email)
        );

        verifyNoInteractions(stockRepository, movementRepository);
    }

    // ==========================================
    // CAMINO 2: Crear stock nuevo (INCOMING) si no existe registro previo
    // ==========================================
    @Test
    void processMovement_IncomingNewStock_ShouldCreateAndSave() throws Exception {
        // Arrange
        InventoryMovementRequestDTO dto = new InventoryMovementRequestDTO();
        dto.setStoreId(1L);
        dto.setItemId(2L);
        dto.setAmount(10L);
        dto.setType(MovementType.INCOMING);

        Store mockStore = new Store();
        InventoryItem mockItem = new InventoryItem();

        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(userService.readUser(email)).thenReturn(mockUser);
        when(stockRepository.findByStoreStoreIdAndInventoryItemItemId(1L, 2L)).thenReturn(Optional.empty());
        when(storeRepository.findById(1L)).thenReturn(Optional.of(mockStore));
        when(inventoryItemRepository.findById(2L)).thenReturn(Optional.of(mockItem));

        InventoryMovement mockMovement = new InventoryMovement();
        when(movementRepository.saveAndFlush(any(InventoryMovement.class))).thenReturn(mockMovement);

        InventoryMovementInfoDTO expectedInfoDto = new InventoryMovementInfoDTO();
        // Ajusta este stubbing según el nombre real del método de tu conversor
        lenient().when((movementConversor).fromEntity2Dto(mockMovement)).thenReturn(expectedInfoDto);

        // Act
        InventoryMovementInfoDTO result = inventoryService.processMovement(dto, email);

        // Assert
        assertNotNull(result);
        verify(stockRepository, times(1)).saveAndFlush(any(Stock.class));
    }

    // ==========================================
    // CAMINO 3: Excepción si no hay registro de stock y se intenta sacar (OUTGOING)
    // ==========================================
    @Test
    void processMovement_OutgoingNoStockRegister_ShouldThrowInsufficientStockException() throws IllegalAccessException {
        InventoryMovementRequestDTO dto = new InventoryMovementRequestDTO();
        dto.setStoreId(1L);
        dto.setItemId(2L);
        dto.setType(MovementType.OUTGOING);

        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(stockRepository.findByStoreStoreIdAndInventoryItemItemId(1L, 2L)).thenReturn(Optional.empty());

        assertThrows(InsufficientStockException.class, () ->
                inventoryService.processMovement(dto, email)
        );
    }

    // ==========================================
    // CAMINO 4: Excepción si hay registro pero la cantidad es insuficiente
    // ==========================================
    @Test
    void processMovement_OutgoingInsufficientStock_ShouldThrowInsufficientStockException() throws IllegalAccessException {
        InventoryMovementRequestDTO dto = new InventoryMovementRequestDTO();
        dto.setStoreId(1L);
        dto.setItemId(2L);
        dto.setAmount(50L); // Pide 50
        dto.setType(MovementType.OUTGOING);

        Stock existingStock = new Stock();
        existingStock.setAmount(20L); // Solo hay 20

        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(stockRepository.findByStoreStoreIdAndInventoryItemItemId(1L, 2L)).thenReturn(Optional.of(existingStock));

        assertThrows(InsufficientStockException.class, () ->
                inventoryService.processMovement(dto, email)
        );
    }




    // ==========================================
    // CAMINO 5: Éxito OUTGOING restando stock y borrando fila si llega a 0
    // ==========================================
    @Test
    void processMovement_OutgoingStockReachesZero_ShouldDeleteStockId() throws Exception {
        InventoryMovementRequestDTO dto = new InventoryMovementRequestDTO();
        dto.setStoreId(1L);
        dto.setItemId(2L);
        dto.setAmount(10L);
        dto.setType(MovementType.OUTGOING);

        Stock existingStock = new Stock();
        existingStock.setStockId(99L);
        existingStock.setAmount(10L); // Quedará a 0
        existingStock.setStore(new Store());

        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(userService.readUser(email)).thenReturn(mockUser);
        when(stockRepository.findByStoreStoreIdAndInventoryItemItemId(1L, 2L)).thenReturn(Optional.of(existingStock));

        InventoryMovement mockMovement = new InventoryMovement();
        when(movementRepository.saveAndFlush(any(InventoryMovement.class))).thenReturn(mockMovement);

        // Act
        inventoryService.processMovement(dto, email);

        // Assert
        assertEquals(0L, existingStock.getAmount());
        verify(stockRepository, times(1)).deleteById(99L); // Al llegar a 0 entra en el bloque else
        verify(stockRepository, never()).saveAndFlush(existingStock);
    }

    @Test
    void updateItem_NoManagerAccess_ShouldThrowAccessDeniedException() throws IllegalAccessException {
        InventoryItemUpdateDTO updateDto = new InventoryItemUpdateDTO();
        when(userService.checkManagerAccess(email)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () ->
                inventoryService.updateItem(updateDto, email)
        );

        verifyNoInteractions(inventoryItemRepository);
    }
    @Test
    void updateItem_ItemNotFound_ShouldThrowEntityNotFoundException() throws IllegalAccessException {
        InventoryItemUpdateDTO updateDto = new InventoryItemUpdateDTO();
        updateDto.setItemId(1L);

        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                inventoryService.updateItem(updateDto, email)
        );
    }

    @Test
    void updateItem_Success_ShouldUpdateAllFieldsAndSave() throws Exception {
        // Arrange
        InventoryItemUpdateDTO updateDto = new InventoryItemUpdateDTO();
        updateDto.setItemId(1L);
        updateDto.setName("Nuevo Nombre");
        updateDto.setDescription("Nueva Descripcion");
        updateDto.setItemCategory(ItemCategory.FOOD);
        updateDto.setEnabled(true);

        InventoryItem existingItem = new InventoryItem();
        existingItem.setName("Nombre Viejo");
        existingItem.setDescription("Descripcion Vieja");
        existingItem.setItemCategory(ItemCategory.DRINKS);
        existingItem.setEnabled(false);

        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.of(existingItem));

        // Act
        inventoryService.updateItem(updateDto, email);

        // Assert
        assertEquals("Nuevo Nombre", existingItem.getName());
        assertEquals("Nueva Descripcion", existingItem.getDescription());
        assertEquals(ItemCategory.FOOD, existingItem.getItemCategory());
        assertTrue(existingItem.getEnabled());

        verify(inventoryItemRepository, times(1)).saveAndFlush(existingItem);
    }

    // =========================================================================
    // TESTS PARA: createContact(LoanContactCreateDTO, String)
    // =========================================================================

    @Test
    void createContact_NoManagerAccess_ShouldThrowAccessDeniedException() throws IllegalAccessException {
        LoanContactCreateDTO createDto = new LoanContactCreateDTO();
        when(userService.checkManagerAccess(email)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () ->
                inventoryService.createContact(createDto, email)
        );

        verifyNoInteractions(fallaRepository, contactRepository);
    }

    @Test
    void createContact_Success_ShouldReturnLoanContactInfo() throws Exception {
        // Arrange
        LoanContactCreateDTO createDto = new LoanContactCreateDTO();

        UserCreateDTO mockUserDto = new UserCreateDTO();
        mockUserDto.setFallaId(5L);

        Falla mockFalla = new Falla();
        LoanContact mockContactToCreate = new LoanContact();
        LoanContact mockSavedContact = new LoanContact();
        LoanContactInfoDTO expectedResponse = new LoanContactInfoDTO();

        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(userService.readUser(email)).thenReturn(mockUserDto);
        when(fallaRepository.findFallaById(5L)).thenReturn(mockFalla);

        // Asegúrate de cambiar contactConversor en la declaración de tus @Mocks si se llama distinto
        when(contactConversor.fromDto2Entity(createDto, mockFalla)).thenReturn(mockContactToCreate);
        when(contactRepository.saveAndFlush(mockContactToCreate)).thenReturn(mockSavedContact);
        when(contactConversor.fromEntity2Dto(mockSavedContact)).thenReturn(expectedResponse);

        // Act
        LoanContactInfoDTO result = inventoryService.createContact(createDto, email);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(contactRepository, times(1)).saveAndFlush(mockContactToCreate);
    }

    // =========================================================================
    // TESTS PARA LAS DOS RAMAS DE: returnLoan(ReturnLoanDTO, String)
    // =========================================================================

    // RAMA 1: Excepción por falta de permisos al inicio del método
    @Test
    void returnLoan_NoManagerAccess_ShouldThrowAccessDeniedException() throws IllegalAccessException {
        ReturnLoanDTO returnDto = new ReturnLoanDTO();
        when(userService.checkManagerAccess(email)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () ->
                inventoryService.returnLoan(returnDto, email)
        );

        // Verificamos que el método se frena de inmediato y no consulta repositorios
        verifyNoInteractions(loanRepository, stockRepository, movementRepository);
    }

    // RAMA 2: El Stock no existe previamente en la BD y entra en el .orElseGet()
    @Test
    void returnLoan_StockDoesNotExist_ShouldExecuteOrElseGetAndCreateNewStock() throws Exception {
        // Arrange
        ReturnLoanDTO returnDto = new ReturnLoanDTO();
        returnDto.setLoanId(10L);
        returnDto.setStoreId(1L);
        returnDto.setAmount(5L);
        returnDto.setMessage("Todo correcto");

        UserCreateDTO mockUser = new UserCreateDTO();
        mockUser.setName("Juan");
        mockUser.setSurname("Gómez");

        InventoryItem mockItem = new InventoryItem();
        mockItem.setItemId(2L);

        Loan mockLoan = new Loan();
        mockLoan.setLoanId(10L);
        mockLoan.setState(LoanState.PENDING); // No devuelto para evitar el IllegalStateException
        mockLoan.setItem(mockItem);

        Falla mockFalla = new Falla();
        Store mockStoreFromReference = new Store();
        mockStoreFromReference.setFalla(mockFalla);

        LoanInfoDTO expectedResponse = new LoanInfoDTO();

        // Stubs de acceso y usuario
        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(userService.readUser(email)).thenReturn(mockUser);
        when(loanRepository.findById(10L)).thenReturn(Optional.of(mockLoan));

        // CRÍTICO: Forzamos el .orElseGet haciendo que devuelva Optional.empty()
        when(stockRepository.findByStoreStoreIdAndInventoryItemItemId(1L, 2L)).thenReturn(Optional.empty());

        // Mockeamos la llamada interna que hace el .orElseGet()
        when(storeRepository.getReferenceById(1L)).thenReturn(mockStoreFromReference);

        // Mocks obligatorios de guardado y conversión final
        when(movementRepository.saveAndFlush(any(InventoryMovement.class))).thenReturn(new InventoryMovement());
        when(loanRepository.save(any(Loan.class))).thenReturn(mockLoan);
        when(loanConversor.fromEntity2Dto(any(Loan.class))).thenReturn(expectedResponse);

        // Act
        LoanInfoDTO result = inventoryService.returnLoan(returnDto, email);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResponse, result);

        // Verificamos que el flujo creó un Stock desde 0L y le sumó los 5 del DTO
        verify(stockRepository, times(1)).saveAndFlush(argThat(stock ->
                stock.getAmount() == 5L &&
                        stock.getInventoryItem() == mockItem &&
                        stock.getStore() == mockStoreFromReference
        ));
    }
}


