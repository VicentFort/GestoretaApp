package com.vfortro.gestoreta.controller;

import com.vfortro.gestoreta.dto.payments.CouponExchangeRequestDTO;
import com.vfortro.gestoreta.dto.payments.CouponPurchaseRequestDTO;
import com.vfortro.gestoreta.dto.payments.EventPaymentRequestDTO;
import com.vfortro.gestoreta.dto.payments.FeePaymentRequestDTO;
import com.vfortro.gestoreta.dto.payments.coupons.CouponCreateDTO;
import com.vfortro.gestoreta.dto.payments.coupons.CouponEditDTO;
import com.vfortro.gestoreta.exceptions.InsufficientStockException;
import com.vfortro.gestoreta.service.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/createCoupon")
    public ResponseEntity<?> createCoupon(@Valid @RequestBody CouponCreateDTO coupon, Authentication auth) {
        String email = auth.getName();
        try {
            paymentService.createCoupon(coupon, email);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.UNAUTHORIZED);
        } catch(IllegalAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/editCoupon")
    public ResponseEntity<?> editCoupon(@Valid @RequestBody CouponEditDTO dto, Authentication auth) {
        String email = auth.getName();
        try {
            paymentService.editCoupon(dto, email);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(IllegalAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch(EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch(AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/sellCoupon")
    public ResponseEntity<?> sellCoupon(@Valid @RequestBody CouponPurchaseRequestDTO request, Authentication auth) {
        String email = auth.getName();
        try {
            paymentService.processPayment(request,email);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException | InsufficientStockException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (InsufficientStockException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.PAYMENT_REQUIRED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch(IllegalAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/feePayment")
    public ResponseEntity<?> feePayment(@RequestBody FeePaymentRequestDTO request, Authentication auth) {
        String email = auth.getName();
        try {
            paymentService.processPayment(request, email);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (InsufficientStockException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.PAYMENT_REQUIRED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch(IllegalAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/eventPayment")
    public ResponseEntity<?> eventPayment(@RequestBody EventPaymentRequestDTO request, Authentication auth) {
        String email = auth.getName();
        try {
            paymentService.processPayment(request, email);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (InsufficientStockException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.PAYMENT_REQUIRED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch(IllegalAccessException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/generateCouponQR")
    public ResponseEntity<?> generateCouponQR(@RequestParam Long couponId, @RequestParam Long stockId, @RequestParam Long amount, Authentication auth) {
        String email = auth.getName();
        try {
            String result = paymentService.generateStockQR(couponId, stockId, amount, email);
            return ResponseEntity.ok(result);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.UNAUTHORIZED);
        } catch(IllegalAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch(EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
