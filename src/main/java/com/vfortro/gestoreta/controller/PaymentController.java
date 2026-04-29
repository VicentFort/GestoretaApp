package com.vfortro.gestoreta.controller;

import com.vfortro.gestoreta.dto.ApiMessageResponse;
import com.vfortro.gestoreta.dto.payments.ExchangeRequestDto;
import com.vfortro.gestoreta.dto.payments.PurchaseRequestDto;
import com.vfortro.gestoreta.dto.payments.coupons.CouponCreateDto;
import com.vfortro.gestoreta.exceptions.InsufficientStockException;
import com.vfortro.gestoreta.service.PaymentService;
import jakarta.persistence.EntityNotFoundException;
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
    public ResponseEntity<?> sellCoupon(@RequestBody PurchaseRequestDto request, Authentication auth) {
        String email = auth.getName();
        try {
            paymentService.sellCoupons(request,email);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(new ApiMessageResponse(e.getMessage(), false), HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException | InsufficientStockException e) {
            return new ResponseEntity<>(new ApiMessageResponse(e.getMessage(), false), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(new ApiMessageResponse(e.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/createCoupon")
    public ResponseEntity<?> createCoupon(@RequestBody CouponCreateDto coupon, Authentication auth) {
        String email = auth.getName();
        try {
            paymentService.createCoupon(coupon, email);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(new ApiMessageResponse(e.getMessage(), false),HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/exchangeCoupon")
    public ResponseEntity<?> exchangeCoupon(@RequestBody ExchangeRequestDto request, Authentication auth) {
        String email = auth.getName();
        try {
            paymentService.exchangeCoupon(request, email);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(new ApiMessageResponse(e.getMessage(), false), HttpStatus.UNAUTHORIZED);
        } catch (InsufficientStockException e) {
            return new ResponseEntity<>(new ApiMessageResponse(e.getMessage(), false), HttpStatus.PAYMENT_REQUIRED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ApiMessageResponse(e.getMessage(), false), HttpStatus.NOT_FOUND);
        }
    }
}
