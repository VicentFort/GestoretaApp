package com.vfortro.gestoreta.controller;

import com.vfortro.gestoreta.dto.ApiMessageResponse;
import com.vfortro.gestoreta.dto.payments.CouponExchangeRequestDTO;
import com.vfortro.gestoreta.dto.payments.CouponPurchaseRequestDTO;
import com.vfortro.gestoreta.dto.payments.coupons.CouponCreateDTO;
import com.vfortro.gestoreta.exceptions.InsufficientStockException;
import com.vfortro.gestoreta.service.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/sellCoupons")
    public ResponseEntity<?> sellCoupon(@Valid @RequestBody CouponPurchaseRequestDTO request, Authentication auth) {
        String email = auth.getName();
        try {
            paymentService.processPayment(request,email);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(new ApiMessageResponse(e.getMessage(), false), HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException | InsufficientStockException e) {
            return new ResponseEntity<>(new ApiMessageResponse(e.getMessage(), false), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(new ApiMessageResponse(e.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch(IllegalAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/createCoupon")
    public ResponseEntity<?> createCoupon(@Valid @RequestBody CouponCreateDTO coupon, Authentication auth) {
        String email = auth.getName();
        try {
            paymentService.createCoupon(coupon, email);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(new ApiMessageResponse(e.getMessage(), false),HttpStatus.UNAUTHORIZED);
        } catch(IllegalAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/exchangeCoupon")
    public ResponseEntity<?> exchangeCoupon(@RequestBody CouponExchangeRequestDTO request, Authentication auth) {
        String email = auth.getName();
        try {
            paymentService.processPayment(request, email);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(new ApiMessageResponse(e.getMessage(), false), HttpStatus.UNAUTHORIZED);
        } catch (InsufficientStockException e) {
            return new ResponseEntity<>(new ApiMessageResponse(e.getMessage(), false), HttpStatus.PAYMENT_REQUIRED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ApiMessageResponse(e.getMessage(), false), HttpStatus.NOT_FOUND);
        } catch(IllegalAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }
}
