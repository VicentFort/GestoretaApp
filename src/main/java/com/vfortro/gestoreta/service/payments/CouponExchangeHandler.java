package com.vfortro.gestoreta.service.payments;

import com.vfortro.gestoreta.dto.inventory.stocks.InventoryMovementRequestDTO;
import com.vfortro.gestoreta.dto.payments.CouponExchangeRequestDTO;
import com.vfortro.gestoreta.dto.payments.CouponRequestDTO;
import com.vfortro.gestoreta.dto.payments.GenericPaymentRequestDTO;
import com.vfortro.gestoreta.exceptions.InsufficientStockException;
import com.vfortro.gestoreta.model.User;
import com.vfortro.gestoreta.model.enums.MovementType;
import com.vfortro.gestoreta.model.enums.PaymentType;
import com.vfortro.gestoreta.model.payments.Coupon;
import com.vfortro.gestoreta.model.payments.CouponStock;
import com.vfortro.gestoreta.model.payments.Payment;
import com.vfortro.gestoreta.repository.payments.CouponRepository;
import com.vfortro.gestoreta.repository.payments.CouponStockRepository;
import com.vfortro.gestoreta.service.InventoryService;
import com.vfortro.gestoreta.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CouponExchangeHandler implements PaymentHandler {

    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private CouponStockRepository stockRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private InventoryService inventoryService;

    @Override
    public boolean supports(PaymentType type) {
        return type == PaymentType.COUPON_EXCHANGED;
    }

    @Override
    public List<Payment> processPayment(GenericPaymentRequestDTO dto, User manager) throws AccessDeniedException, InsufficientStockException, IllegalAccessException {
        CouponExchangeRequestDTO request = (CouponExchangeRequestDTO) dto;
        //1.1 Obtenemos el usuario que ha hecho la compra.
        if(Objects.equals(request.getFallaId(), manager.getFalla().getId())) throw new IllegalAccessException("El tiquet no es de la falla");
        CouponStock stock = stockRepository.findById(request.getStockId()).orElseThrow(() -> new EntityNotFoundException("No existeix el stock"));

        User user = stock.getUser();
        //2.0 Encontrar el cupon en la base de datos y crear un detalle de compra de se cupon.
        Coupon coupon = couponRepository.findById(request.getCoupon().getCouponId()).orElseThrow(() -> new EntityNotFoundException("No existeix el ticket amb id: " + request.getCoupon().getCouponId()));

        if(!Objects.equals(stock.getCoupon().getCouponId(), coupon.getCouponId())) throw new IllegalAccessException("El stock no es del tiquet: " + coupon.getName());

        String logMessage = "";
        if(request.getMessage() == null || request.getMessage().isBlank()) {
            logMessage = request.getCoupon().getAmount() + " tickets bescanviats de: " + coupon.getName() + " amb id: " + coupon.getCouponId() + ". Gestionat per: " + manager.getName() + " " + manager.getSurname();
        } else {
            logMessage = request.getMessage();
        }

        //2.1 Gestión del stock de cupones del usuario
        if(stock.getAmount() - request.getCoupon().getAmount() < 0) {
            throw new InsufficientStockException("L'usuari no té prou tickets per a fer el bescanvi");
        }
        stock.setAmount(stock.getAmount() - request.getCoupon().getAmount());
        stockRepository.save(stock);


        //2.2 Generar el movimiento de inventario.
        InventoryMovementRequestDTO movementDto = new InventoryMovementRequestDTO();
        movementDto.setMessage(logMessage);
        movementDto.setItemId(coupon.getItem().getItemId());
        movementDto.setAmount(request.getCoupon().getAmount());
        movementDto.setStoreId(request.getStoreId());
        movementDto.setType(MovementType.OUTGOING);
        inventoryService.processMovement(movementDto, manager.getEmail());

        //2.3 Generar Payment.
        Payment log = new Payment();
        log.setManager(manager);
        log.setFalla(manager.getFalla());
        log.setUser(user);
        log.setCouponExchanged(coupon);
        log.setItem(coupon.getItem());
        log.setPrice(coupon.getPrice());
        log.setDate(LocalDateTime.now());
        log.setType(PaymentType.COUPON_EXCHANGED);
        log.setMessage(logMessage);
        return List.of(log);
    }
}
