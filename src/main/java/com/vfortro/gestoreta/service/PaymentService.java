package com.vfortro.gestoreta.service;


import com.vfortro.gestoreta.conversor.payments.CouponConversor;
import com.vfortro.gestoreta.conversor.payments.PurchaseConversor;
import com.vfortro.gestoreta.conversor.payments.PurchaseDetailConversor;
import com.vfortro.gestoreta.dto.payments.GenericPaymentRequestDTO;
import com.vfortro.gestoreta.dto.payments.coupons.CouponCreateDTO;
import com.vfortro.gestoreta.exceptions.InsufficientStockException;
import com.vfortro.gestoreta.model.User;
import com.vfortro.gestoreta.model.payments.*;
import com.vfortro.gestoreta.repository.FallaRepository;
import com.vfortro.gestoreta.repository.UserRepository;
import com.vfortro.gestoreta.repository.payments.*;
import com.vfortro.gestoreta.service.payments.PaymentHandler;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
public class PaymentService {
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private CouponStockRepository stockRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PurchaseDetailRepository detailRepository;
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private FallaRepository fallaRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private InventoryService inventoryService;


    @Autowired
    private PurchaseConversor purchaseConversor;
    @Autowired
    private PurchaseDetailConversor detailConversor;
    @Autowired
    private CouponConversor couponConversor;


    private final List<PaymentHandler> paymentHandlers;

    public PaymentService(List<PaymentHandler> paymentHandlers) {
        this.paymentHandlers = paymentHandlers;
    }


    @Transactional
    public void processPayment(GenericPaymentRequestDTO request, String email) throws AccessDeniedException, EntityNotFoundException, InsufficientStockException, IllegalStateException {

        //1. Validación de permisos de usuario.
        User manager = userService.readUserAsEntity(email);
        if(manager.getFalla()==null) {
            throw new EntityNotFoundException("El usuari no té falla");
        }
        if(!userService.checkAdminAccess(email)) {
            throw new AccessDeniedException("Sense permís!");
        }

        //2. Procesar el pago
        PaymentHandler handler = paymentHandlers.stream()
                .filter(h -> h.supports(request.getType()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No hi ha manjeador de pagament per a aquest tipus"));
        List<Payment> payments = handler.processPayment(request, manager);

        //3. Guardar los logs.
        paymentRepository.saveAll(payments);
    }

    @Transactional
    public void createCoupon(CouponCreateDTO dto, String email) throws AccessDeniedException {
        if(!userService.checkAdminAccess(email)) {
            throw new AccessDeniedException("Sense permís!");
        }
        Coupon toSave = couponConversor.fromDto2Entity(dto);
        couponRepository.save(toSave);
    }


}
