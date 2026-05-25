package com.vfortro.gestoreta.service.payments;

import com.vfortro.gestoreta.dto.payments.CouponPurchaseRequestDTO;
import com.vfortro.gestoreta.dto.payments.CouponRequestDTO;
import com.vfortro.gestoreta.dto.payments.GenericPaymentRequestDTO;
import com.vfortro.gestoreta.model.User;
import com.vfortro.gestoreta.model.enums.PaymentType;
import com.vfortro.gestoreta.model.payments.*;
import com.vfortro.gestoreta.repository.payments.CouponRepository;
import com.vfortro.gestoreta.repository.payments.CouponStockRepository;
import com.vfortro.gestoreta.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CouponPaymentHandler implements PaymentHandler {

    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private CouponStockRepository stockRepository;

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(PaymentType type) {
        return type == PaymentType.COUPON_SOLD;
    }

    @Override
    public List<Payment> processPayment(GenericPaymentRequestDTO dto, User manager) throws IllegalStateException {
        System.out.println("INICIANT PROCESSAMENT DE VENDA DE TIQUET");
        CouponPurchaseRequestDTO request = (CouponPurchaseRequestDTO) dto;
        List<Payment> payments = new ArrayList<>();
        //1.1 Obtenemos el usuario que ha hecho la compra.
        User user = userService.readUserAsEntity(request.getUserEmail());

        //3. Procesar tickes (coupons) comprados.
        Double totalSum = 0.0D;
        for(CouponRequestDTO couponDto: request.getCoupons()) {
            //3.0 Encontrar el cupon en la base de datos y crear un detalle de compra de se cupon.
            Coupon coupon = couponRepository.findById(couponDto.getCouponId()).orElseThrow(() -> new EntityNotFoundException("No existeix el ticket amb id: " + couponDto.getCouponId()));

            totalSum += coupon.getPrice()  * couponDto.getAmount();

            //3.1 Preparar el mensaje de log para el Payment y el registro de inventario.
            String paymentMessage = "";
            if(request.getMessage() == null || request.getMessage().isBlank()) {
                paymentMessage = couponDto.getAmount() + " tiquets venuts de: " + coupon.getName() + " amb id: " + coupon.getCouponId() + " per part de: " + manager.getName() + " " + manager.getSurname();
            } else {
                paymentMessage = request.getMessage();
            }

            //3.2 Actualizar el stock de tickets.
            CouponStock stock = stockRepository.findByCouponCouponIdAndUserId(coupon.getCouponId(), user.getId())
                    .orElseGet(() -> {
                        CouponStock newStock = new CouponStock();
                        newStock.setCoupon(coupon);
                        newStock.setUser(user);
                        newStock.setAmount(0L);
                        newStock.setFalla(manager.getFalla());
                        return newStock;
                    });
            stock.setAmount(stock.getAmount() + couponDto.getAmount());
            stockRepository.save(stock);

            //3.3 Generar Payment.
            Payment payment = new Payment();
            payment.setManager(manager);
            payment.setFalla(manager.getFalla());
            payment.setUser(user);
            payment.setItem(coupon.getItem());
            payment.setCouponSold(coupon);
            payment.setPrice(coupon.getPrice()*couponDto.getAmount());
            payment.setDate(LocalDateTime.now());
            payment.setType(PaymentType.COUPON_SOLD);
            payment.setMessage(paymentMessage);
            payments.add(payment);

        }
        //4. Comprobar que el total pagado sea mayor o igual a los precios de los cupones
        if(Math.abs(totalSum - request.getTotalPrice()) > 0.00001) {
            throw new IllegalStateException("El valor de la compra no coincideix a la quantitat abonada.");
        }
        //5. Guardar la compra
        System.out.println("FINALITZAT PROCESSAMENT DE VENDA DE TIQUET");
        return payments;
    }
}
