package com.vfortro.gestoreta.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.vfortro.gestoreta.dto.payments.CouponExchangeRequestDTO;
import com.vfortro.gestoreta.dto.payments.CouponPurchaseRequestDTO;
import com.vfortro.gestoreta.dto.payments.EventPaymentRequestDTO;
import com.vfortro.gestoreta.dto.payments.FeePaymentRequestDTO;
import com.vfortro.gestoreta.dto.payments.coupons.CouponCreateDTO;
import com.vfortro.gestoreta.dto.payments.coupons.CouponEditDTO;
import com.vfortro.gestoreta.exceptions.InsufficientStockException;
import com.vfortro.gestoreta.service.PaymentService;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.nio.file.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock private PaymentService paymentService;
    @Mock private Authentication authentication;

    @InjectMocks
    private PaymentController paymentController;

    private final String userEmail = "tresorer@falla.com";

    @BeforeEach
    void setUp() {
        lenient().when(authentication.getName()).thenReturn(userEmail);
    }

    // ==========================================
    // ENDPOINT: POST /payment/createCoupon
    // ==========================================
    @Test
    void createCoupon_Success_ShouldReturnOk() throws Exception {
        CouponCreateDTO dto = new CouponCreateDTO();

        ResponseEntity<?> response = paymentController.createCoupon(dto, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(paymentService, times(1)).createCoupon(dto, userEmail);
    }

    @Test
    void createCoupon_IllegalAccess_ShouldReturnForbidden() throws Exception {
        CouponCreateDTO dto = new CouponCreateDTO();
        doThrow(new IllegalAccessException("Sense rols de tresoreria")).when(paymentService).createCoupon(dto, userEmail);

        ResponseEntity<?> response = paymentController.createCoupon(dto, authentication);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Sense rols de tresoreria", response.getBody());
    }

    // ==========================================
    // ENDPOINT: PUT /payment/editCoupon
    // ==========================================
    @Test
    void editCoupon_NotFound_ShouldReturnNotFound() throws Exception {
        CouponEditDTO dto = new CouponEditDTO();
        doThrow(new EntityNotFoundException("El cupó no existeix")).when(paymentService).editCoupon(dto, userEmail);

        ResponseEntity<?> response = paymentController.editCoupon(dto, authentication);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("El cupó no existeix", response.getBody());
    }

    @Test
    void editCoupon_Success_ShouldReturnOk() throws Exception {
        CouponEditDTO dto = new CouponEditDTO();

        ResponseEntity<?> response = paymentController.editCoupon(dto, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(paymentService, times(1)).editCoupon(dto, userEmail);
    }

    // ==========================================
    // ENDPOINT: POST /payment/sellCoupon
    // ==========================================
    @Test
    void sellCoupon_InsufficientStock_ShouldReturnNotFound() throws Exception {
        CouponPurchaseRequestDTO request = new CouponPurchaseRequestDTO();
        doThrow(new InsufficientStockException("No queden cupons")).when(paymentService).processPayment(request, userEmail);

        ResponseEntity<?> response = paymentController.sellCoupon(request, authentication);

        // El controlador captura InsufficientStockException devolviendo un 404 (NOT_FOUND)
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No queden cupons", response.getBody());
    }

    @Test
    void sellCoupon_IllegalState_ShouldReturnInternalServerError() throws Exception {
        CouponPurchaseRequestDTO request = new CouponPurchaseRequestDTO();
        doThrow(new IllegalStateException("Compte tancat")).when(paymentService).processPayment(request, userEmail);

        ResponseEntity<?> response = paymentController.sellCoupon(request, authentication);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Compte tancat", response.getBody());
    }

    // ==========================================
    // ENDPOINT: POST /payment/exchangeCoupon
    // ==========================================
    @Test
    void exchangeCoupon_InsufficientStock_ShouldReturnPaymentRequired() throws Exception {
        CouponExchangeRequestDTO request = new CouponExchangeRequestDTO();
        doThrow(new InsufficientStockException("Saldo de punts insuficient")).when(paymentService).processPayment(request, userEmail);

        ResponseEntity<?> response = paymentController.exchangeCoupon(request, authentication);

        // El controlador captura InsufficientStockException en este método como un 402 (PAYMENT_REQUIRED)
        assertEquals(HttpStatus.PAYMENT_REQUIRED, response.getStatusCode());
        assertEquals("Saldo de punts insuficient", response.getBody());
    }

    @Test
    void exchangeCoupon_Success_ShouldReturnOk() throws Exception {
        CouponExchangeRequestDTO request = new CouponExchangeRequestDTO();

        ResponseEntity<?> response = paymentController.exchangeCoupon(request, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(paymentService, times(1)).processPayment(request, userEmail);
    }

    // ==========================================
    // ENDPOINT: POST /payment/feePayment
    // ==========================================
    @Test
    void feePayment_AccessDenied_ShouldReturnUnauthorized() throws Exception {
        FeePaymentRequestDTO request = new FeePaymentRequestDTO();
        doThrow(new AccessDeniedException("No autoritzat")).when(paymentService).processPayment(request, userEmail);

        ResponseEntity<?> response = paymentController.feePayment(request, authentication);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("No autoritzat", response.getBody());
    }

    @Test
    void feePayment_Success_ShouldReturnOk() throws Exception {
        FeePaymentRequestDTO request = new FeePaymentRequestDTO();

        ResponseEntity<?> response = paymentController.feePayment(request, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(paymentService, times(1)).processPayment(request, userEmail);
    }

    // ==========================================
    // ENDPOINT: POST /payment/eventPayment
    // ==========================================
    @Test
    void eventPayment_IllegalStateException_ShouldReturnForbidden() throws Exception {
        EventPaymentRequestDTO request = new EventPaymentRequestDTO();
        doThrow(new IllegalStateException("L'esdeveniment ja ha passat")).when(paymentService).processPayment(request, userEmail);

        ResponseEntity<?> response = paymentController.eventPayment(request, authentication);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("L'esdeveniment ja ha passat", response.getBody());
    }

    @Test
    void eventPayment_Success_ShouldReturnOk() throws Exception {
        EventPaymentRequestDTO request = new EventPaymentRequestDTO();

        ResponseEntity<?> response = paymentController.eventPayment(request, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(paymentService, times(1)).processPayment(request, userEmail);
    }
}