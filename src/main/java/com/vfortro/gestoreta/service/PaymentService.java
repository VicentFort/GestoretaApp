package com.vfortro.gestoreta.service;


import com.vfortro.gestoreta.conversor.payments.CouponConversor;
import com.vfortro.gestoreta.conversor.payments.PurchaseConversor;
import com.vfortro.gestoreta.conversor.payments.PurchaseDetailConversor;
import com.vfortro.gestoreta.dto.inventory.stocks.InventoryMovementDto;
import com.vfortro.gestoreta.dto.payments.CouponRequestDto;
import com.vfortro.gestoreta.dto.payments.ExchangeRequestDto;
import com.vfortro.gestoreta.dto.payments.PurchaseRequestDto;
import com.vfortro.gestoreta.dto.payments.coupons.CouponCreateDto;
import com.vfortro.gestoreta.exceptions.InsufficientStockException;
import com.vfortro.gestoreta.model.Falla;
import com.vfortro.gestoreta.model.User;
import com.vfortro.gestoreta.model.enums.MovementType;
import com.vfortro.gestoreta.model.enums.PaymentLogType;
import com.vfortro.gestoreta.model.inventory.InventoryItem;
import com.vfortro.gestoreta.model.payments.*;
import com.vfortro.gestoreta.repository.FallaRepository;
import com.vfortro.gestoreta.repository.payments.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private CouponStockRepository stockRepository;
    @Autowired
    private PaymentLogRepository logRepository;
    @Autowired
    private PurchaseDetailRepository detailRepository;
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private FallaRepository fallaRepository;

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

    @Transactional
    public void sellCoupons(PurchaseRequestDto request, String email) throws AccessDeniedException, EntityNotFoundException, InsufficientStockException, IllegalStateException {

        //1. Validación de permisos de usuario.
        User manager = userService.readUserAsEntity(email);
        if(manager.getFalla()==null) {
            throw new EntityNotFoundException("El usuari no te falla");
        }
        if(!userService.checkAdminAccess(email)) {
            throw new AccessDeniedException("Sense permís!");
        }
        //1.1 Obtenemos el usuario que ha hecho la compra.
        User user = userService.readUserAsEntity(request.getUserId());

        //2 Creación de la compra (purchase) en la base de datos y el contador de precio.
        Purchase toSave = purchaseConversor.fromDto2Entity(request, manager.getFalla());
        Double totalSum = 0.0D;

        //3. Procesar tickes (coupons) comprados.
        for(CouponRequestDto dto: request.getCoupons()) {
            //3.0 Encontrar el cupon en la base de datos y crear un detalle de compra de se cupon.
            Coupon coupon = couponRepository.findById(dto.getCouponId()).orElseThrow(() -> new EntityNotFoundException("No existeix el ticket amb id: " + dto.getCouponId()));
            PurchaseDetail detail = detailConversor.fromDto2Entity(dto, toSave, coupon);
            PurchaseDetail savedDetail = detailRepository.save(detail);

            totalSum += coupon.getPrice()  * dto.getAmount();


            //3.1 Preparar el mensaje de log para el PaymentLog y el registro de inventario.
            String logMessage = dto.getAmount() + " tiquets venuts de: " + coupon.getName() + " amb id: " + coupon.getCouponId() + " per part de: " + manager.getName() + " " + manager.getSurname();




            //3.2 Actualizar el stock de tickets.
            CouponStock stock = stockRepository.findByCouponCouponIdAndUserId(coupon.getCouponId(), user.getId())
                    .orElseGet(() -> {
                        CouponStock newStock = new CouponStock();
                        newStock.setCoupon(coupon);
                        newStock.setUser(user);
                        newStock.setAmount(0L);
                        return newStock;
                    });
            stock.setAmount(stock.getAmount() + dto.getAmount());
            stockRepository.save(stock);

            //3.3 Generar PaymentLog.
            PaymentLog log = new PaymentLog();
            log.setManager(manager);
            log.setFalla(manager.getFalla());
            log.setUser(user);
            log.setCoupon(coupon);
            log.setPurchase(toSave);
            log.setItem(coupon.getItem());
            log.setPrice(coupon.getPrice());
            log.setDate(LocalDateTime.now());
            log.setType(PaymentLogType.COUPON_SOLD);
            log.setMessage(logMessage);
            logRepository.save(log);
        }
        //4. Comprobar que el total pagado sea mayor o igual a los precios de los cupones
        if(Math.abs(totalSum - request.getTotalPrice()) > 0.00001) {
            throw new IllegalStateException("El valor de la compra no coincideix a la quantitat abonada.");
        }
        //5. Guardar la compra
        Purchase savedPurchase = purchaseRepository.saveAndFlush(toSave);

    }

    @Transactional
    public void createCoupon(CouponCreateDto dto, String email) throws AccessDeniedException {
        if(!userService.checkAdminAccess(email)) {
            throw new AccessDeniedException("Sense permís!");
        }
        Coupon toSave = couponConversor.fromDto2Entity(dto);
        couponRepository.save(toSave);
    }

    @Transactional
    public void exchangeCoupon(ExchangeRequestDto request, String email) throws AccessDeniedException {
        //1. Validación de permisos de usuario.
        User manager = userService.readUserAsEntity(email);
        if(manager.getFalla()==null) {
            throw new EntityNotFoundException("El usuari no te falla");
        }
        if(!userService.checkAdminAccess(email)) {
            throw new AccessDeniedException("Sense permís!");
        }
        //1.1 Obtenemos el usuario que ha hecho la compra.
        User user = userService.readUserAsEntity(request.getUserId());

        for(CouponRequestDto dto: request.getCoupons()) {
            //2.0 Encontrar el cupon en la base de datos y crear un detalle de compra de se cupon.
            Coupon coupon = couponRepository.findById(dto.getCouponId()).orElseThrow(() -> new EntityNotFoundException("No existeix el ticket amb id: " + dto.getCouponId()));

            String logMessage = dto.getAmount() + " tickets bescanviats de: " + coupon.getName() + " amb id: " + coupon.getCouponId() + " .Gestionat per: " + manager.getName() + " " + manager.getSurname();

            //2.1 Gestión del stock de cupones del usuario
            CouponStock stock = stockRepository.findByCouponCouponIdAndUserId(coupon.getCouponId(), user.getId()).orElseThrow(() -> new InsufficientStockException("El usuari no te stock de tiquets"));
            if(stock.getAmount() - dto.getAmount() < 0) {
                throw new InsufficientStockException("L'usuari no té prou tickets per a fer el bescanvi");
            }
            stock.setAmount(stock.getAmount() - dto.getAmount());
            stockRepository.save(stock);


            //2.2 Generar el movimiento de inventario.
            InventoryMovementDto movementDto = new InventoryMovementDto();
            movementDto.setMessage(logMessage);
            movementDto.setItemId(coupon.getItem().getItemId());
            movementDto.setAmount(dto.getAmount());
            movementDto.setStoreId(request.getStoreId());
            movementDto.setType(MovementType.OUTGOING);
            inventoryService.processMovement(movementDto,email);

            //2.3 Generar PaymentLog.
            PaymentLog log = new PaymentLog();
            log.setManager(manager);
            log.setFalla(manager.getFalla());
            log.setUser(user);
            log.setCoupon(coupon);
            //NO HAY COMPRA
            log.setItem(coupon.getItem());
            log.setPrice(coupon.getPrice());
            log.setDate(LocalDateTime.now());
            log.setType(PaymentLogType.COUPON_EXCHANGED);
            log.setMessage(logMessage);
            logRepository.save(log);
        }

    }
}
