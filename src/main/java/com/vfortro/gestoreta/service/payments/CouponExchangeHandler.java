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
    public List<Payment> processPayment(GenericPaymentRequestDTO dto, User manager) throws AccessDeniedException, InsufficientStockException {
        CouponExchangeRequestDTO request = (CouponExchangeRequestDTO) dto;
        List<Payment> logs = new ArrayList<>();
        //1.1 Obtenemos el usuario que ha hecho la compra.
        User user = userService.readUserAsEntity(request.getUserEmail());

        for(CouponRequestDTO couponDTO: request.getCoupons()) {
            //2.0 Encontrar el cupon en la base de datos y crear un detalle de compra de se cupon.
            Coupon coupon = couponRepository.findById(couponDTO.getCouponId()).orElseThrow(() -> new EntityNotFoundException("No existeix el ticket amb id: " + couponDTO.getCouponId()));

            String logMessage = "";
            if(request.getMessage() == null || request.getMessage().isBlank()) {
                logMessage = couponDTO.getAmount() + " tickets bescanviats de: " + coupon.getName() + " amb id: " + coupon.getCouponId() + ". Gestionat per: " + manager.getName() + " " + manager.getSurname();
            } else {
                logMessage = request.getMessage();
            }

            //2.1 Gestión del stock de cupones del usuario
            CouponStock stock = stockRepository.findByCouponCouponIdAndUserId(coupon.getCouponId(), user.getId()).orElseThrow(() -> new InsufficientStockException("El usuari no te stock de tiquets"));
            if(stock.getAmount() - couponDTO.getAmount() < 0) {
                throw new InsufficientStockException("L'usuari no té prou tickets per a fer el bescanvi");
            }
            stock.setAmount(stock.getAmount() - couponDTO.getAmount());
            stockRepository.save(stock);


            //2.2 Generar el movimiento de inventario.
            InventoryMovementRequestDTO movementDto = new InventoryMovementRequestDTO();
            movementDto.setMessage(logMessage);
            movementDto.setItemId(coupon.getItem().getItemId());
            movementDto.setAmount(couponDTO.getAmount());
            movementDto.setStoreId(request.getStoreId());
            movementDto.setType(MovementType.OUTGOING);
            inventoryService.processMovement(movementDto, manager);

            //2.3 Generar Payment.
            Payment log = new Payment();
            log.setManager(manager);
            log.setFalla(manager.getFalla());
            log.setUser(user);
            log.setCouponExchanged(coupon);
            //NO HAY COMPRA
            log.setItem(coupon.getItem());
            log.setPrice(coupon.getPrice());
            log.setDate(LocalDateTime.now());
            log.setType(PaymentType.COUPON_EXCHANGED);
            log.setMessage(logMessage);
            logs.add(log);
        }
        return logs;
    }
}
