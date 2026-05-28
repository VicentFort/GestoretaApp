package com.vfortro.gestoreta.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.vfortro.gestoreta.conversor.payments.CouponConversor;
import com.vfortro.gestoreta.dto.payments.*;
import com.vfortro.gestoreta.dto.payments.coupons.CouponCreateDTO;
import com.vfortro.gestoreta.dto.payments.coupons.CouponEditDTO;
import com.vfortro.gestoreta.model.Falla;
import com.vfortro.gestoreta.model.User;
import com.vfortro.gestoreta.model.enums.ItemCategory;
import com.vfortro.gestoreta.model.enums.PaymentType;
import com.vfortro.gestoreta.model.inventory.InventoryItem;
import com.vfortro.gestoreta.model.payments.Coupon;
import com.vfortro.gestoreta.model.payments.Payment;
import com.vfortro.gestoreta.repository.inventory.InventoryItemRepository;
import com.vfortro.gestoreta.repository.payments.CouponRepository;
import com.vfortro.gestoreta.repository.payments.PaymentRepository;
import com.vfortro.gestoreta.service.UserService;
import com.vfortro.gestoreta.service.payments.*;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock private CouponRepository couponRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private InventoryItemRepository itemRepository;
    @Mock private UserService userService;
    @Mock private CouponConversor couponConversor;

    @Mock private CouponExchangeHandler couponExchangeHandler;
    @Mock private CouponPaymentHandler couponPaymentHandler;
    @Mock private EventPaymentHandler eventPaymentHandler;
    @Mock private FeePaymentHandler feePaymentHandler;

    private PaymentService paymentService;

    private final String email = "manager@gestoreta.com";
    private User sampleManager;
    private Falla sampleFalla;
    private InventoryItem sampleItem;
    private Coupon sampleCoupon;

    @BeforeEach
    void setUp() {
        List<PaymentHandler> handlers = new ArrayList<>();
        handlers.add(couponExchangeHandler);
        handlers.add(couponPaymentHandler);
        handlers.add(eventPaymentHandler);
        handlers.add(feePaymentHandler);

        paymentService = new PaymentService(handlers);

        java.lang.reflect.Field[] fields = PaymentService.class.getDeclaredFields();
        try {
            for (java.lang.reflect.Field field : fields) {
                field.setAccessible(true);
                if (field.getType().equals(CouponRepository.class)) field.set(paymentService, couponRepository);
                if (field.getType().equals(PaymentRepository.class)) field.set(paymentService, paymentRepository);
                if (field.getType().equals(InventoryItemRepository.class)) field.set(paymentService, itemRepository);
                if (field.getType().equals(UserService.class)) field.set(paymentService, userService);
                if (field.getType().equals(CouponConversor.class)) field.set(paymentService, couponConversor);
            }
        } catch (IllegalAccessException e) {
            fail("No se pudieron inicializar los campos por reflexión.");
        }

        sampleFalla = new Falla();
        sampleFalla.setId(1L);

        sampleManager = new User();
        sampleManager.setId(10L);
        sampleManager.setEmail(email);
        sampleManager.setFalla(sampleFalla);

        sampleItem = new InventoryItem();
        sampleItem.setItemId(20L);
        sampleItem.setFalla(sampleFalla);
        sampleItem.setItemCategory(ItemCategory.FOOD);

        sampleCoupon = new Coupon();
        sampleCoupon.setCouponId(30L);
        sampleCoupon.setFalla(sampleFalla);
        sampleCoupon.setItem(sampleItem);
    }

    // ==========================================
    // METODO: processPayment (Estrategia y Handlers)
    // ==========================================
    @Test
    void processPayment_UserHasNoFalla_ShouldThrowEntityNotFoundException() {
        sampleManager.setFalla(null);
        when(userService.readUserAsEntity(email)).thenReturn(sampleManager);

        // CORRECCIÓN: Usamos una implementación concreta no abstracta para lanzar el test
        FeePaymentRequestDTO request = new FeePaymentRequestDTO();

        assertThrows(EntityNotFoundException.class, () -> paymentService.processPayment(request, email));
    }

    @Test
    void processPayment_NoHandlerSupportsPaymentType_ShouldThrowIllegalStateException() throws IllegalAccessException {
        // CORRECCIÓN: Instanciamos un hijo concreto que se corresponda con el tipo asignado
        CouponPurchaseRequestDTO request = new CouponPurchaseRequestDTO();
        request.setType("Venda de tiquet"); // PaymentType.COUPON_SOLD

        when(userService.readUserAsEntity(email)).thenReturn(sampleManager);
        when(userService.checkManagerAccess(email)).thenReturn(true);

        when(couponExchangeHandler.supports(PaymentType.COUPON_SOLD)).thenReturn(false);
        when(couponPaymentHandler.supports(PaymentType.COUPON_SOLD)).thenReturn(false);
        when(eventPaymentHandler.supports(PaymentType.COUPON_SOLD)).thenReturn(false);
        when(feePaymentHandler.supports(PaymentType.COUPON_SOLD)).thenReturn(false);

        assertThrows(IllegalStateException.class, () -> paymentService.processPayment(request, email));
    }

    @Test
    void processPayment_RoutingToCouponPaymentHandler_Success() throws Exception {
        // CORRECCIÓN: Instanciamos el DTO concreto que espera recibir el CouponPaymentHandler
        CouponPurchaseRequestDTO request = new CouponPurchaseRequestDTO();
        request.setType("Venda de tiquet");

        List<Payment> expectedPayments = List.of(new Payment());

        when(userService.readUserAsEntity(email)).thenReturn(sampleManager);
        when(userService.checkManagerAccess(email)).thenReturn(true);

        when(couponExchangeHandler.supports(PaymentType.COUPON_SOLD)).thenReturn(false);
        when(couponPaymentHandler.supports(PaymentType.COUPON_SOLD)).thenReturn(true);

        when(couponPaymentHandler.processPayment(request, sampleManager)).thenReturn(expectedPayments);

        paymentService.processPayment(request, email);

        verify(paymentRepository, times(1)).saveAll(expectedPayments);
    }

    @Test
    void processPayment_RoutingToFeePaymentHandler_Success() throws Exception {
        // CORRECCIÓN: Instanciamos el DTO concreto que espera recibir el FeePaymentHandler
        FeePaymentRequestDTO request = new FeePaymentRequestDTO();
        request.setType("Pagament de quota");

        List<Payment> expectedPayments = List.of(new Payment());

        when(userService.readUserAsEntity(email)).thenReturn(sampleManager);
        when(userService.checkManagerAccess(email)).thenReturn(true);

        when(couponExchangeHandler.supports(PaymentType.FEE_PAYMENT)).thenReturn(false);
        when(couponPaymentHandler.supports(PaymentType.FEE_PAYMENT)).thenReturn(false);
        when(eventPaymentHandler.supports(PaymentType.FEE_PAYMENT)).thenReturn(false);
        when(feePaymentHandler.supports(PaymentType.FEE_PAYMENT)).thenReturn(true);

        when(feePaymentHandler.processPayment(request, sampleManager)).thenReturn(expectedPayments);

        paymentService.processPayment(request, email);

        verify(paymentRepository, times(1)).saveAll(expectedPayments);
    }

    // ==========================================
    // METODO: createCoupon
    // ==========================================
    @Test
    void createCoupon_FallaIdMismatch_ShouldThrowIllegalAccessException() throws IllegalAccessException {
        CouponCreateDTO dto = new CouponCreateDTO();
        dto.setItemId(20L);

        Falla otraFalla = new Falla();
        otraFalla.setId(99L);
        sampleItem.setFalla(otraFalla);

        when(userService.readUserAsEntity(email)).thenReturn(sampleManager);
        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(itemRepository.findById(20L)).thenReturn(Optional.of(sampleItem));

        assertThrows(IllegalAccessException.class, () -> paymentService.createCoupon(dto, email));
    }

    @Test
    void createCoupon_InvalidCategory_ShouldThrowIllegalStateException() throws IllegalAccessException {
        CouponCreateDTO dto = new CouponCreateDTO();
        dto.setItemId(20L);

        sampleItem.setItemCategory(ItemCategory.PYROTECHNICS);

        when(userService.readUserAsEntity(email)).thenReturn(sampleManager);
        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(itemRepository.findById(20L)).thenReturn(Optional.of(sampleItem));
        when(couponConversor.fromDto2Entity(dto)).thenReturn(sampleCoupon);

        assertThrows(IllegalStateException.class, () -> paymentService.createCoupon(dto, email));
    }

    @Test
    void createCoupon_Success_ShouldSave() throws Exception {
        CouponCreateDTO dto = new CouponCreateDTO();
        dto.setItemId(20L);
        sampleItem.setItemCategory(ItemCategory.FOOD);

        when(userService.readUserAsEntity(email)).thenReturn(sampleManager);
        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(itemRepository.findById(20L)).thenReturn(Optional.of(sampleItem));
        when(couponConversor.fromDto2Entity(dto)).thenReturn(sampleCoupon);

        paymentService.createCoupon(dto, email);

        verify(couponRepository, times(1)).save(sampleCoupon);
    }

    // ==========================================
    // METODO: editCoupon
    // ==========================================
    @Test
    void editCoupon_CouponNotFound_ShouldThrowEntityNotFoundException() throws IllegalAccessException {
        CouponEditDTO dto = new CouponEditDTO();
        dto.setCouponId(30L);

        when(userService.readUserAsEntity(email)).thenReturn(sampleManager);
        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(couponRepository.findById(30L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> paymentService.editCoupon(dto, email));
    }

    @Test
    void editCoupon_SuccessWithNewItem_ShouldVerifyAndSave() throws Exception {
        CouponEditDTO dto = new CouponEditDTO();
        dto.setCouponId(30L);
        dto.setItemId(500L);
        dto.setName("Tiquet Nou");

        InventoryItem nuevoItem = new InventoryItem();
        nuevoItem.setItemId(500L);
        nuevoItem.setFalla(sampleFalla);

        when(userService.readUserAsEntity(email)).thenReturn(sampleManager);
        when(userService.checkManagerAccess(email)).thenReturn(true);
        when(couponRepository.findById(30L)).thenReturn(Optional.of(sampleCoupon));
        when(itemRepository.findById(500L)).thenReturn(Optional.of(nuevoItem));

        paymentService.editCoupon(dto, email);

        assertEquals("Tiquet Nou", sampleCoupon.getName());
        assertEquals(nuevoItem, sampleCoupon.getItem());
        verify(couponRepository, times(1)).save(sampleCoupon);
    }
}