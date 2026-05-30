package com.vfortro.gestoreta.service;


import com.vfortro.gestoreta.conversor.payments.CouponConversor;
import com.vfortro.gestoreta.dto.payments.GenericPaymentRequestDTO;
import com.vfortro.gestoreta.dto.payments.coupons.CouponCreateDTO;
import com.vfortro.gestoreta.dto.payments.coupons.CouponEditDTO;
import com.vfortro.gestoreta.dto.payments.info.CouponStockInfoDTO;
import com.vfortro.gestoreta.exceptions.InsufficientStockException;
import com.vfortro.gestoreta.model.User;
import com.vfortro.gestoreta.model.enums.ItemCategory;
import com.vfortro.gestoreta.model.enums.PaymentType;
import com.vfortro.gestoreta.model.inventory.InventoryItem;
import com.vfortro.gestoreta.model.inventory.Stock;
import com.vfortro.gestoreta.model.payments.*;
import com.vfortro.gestoreta.repository.FallaRepository;
import com.vfortro.gestoreta.repository.UserRepository;
import com.vfortro.gestoreta.repository.inventory.InventoryItemRepository;
import com.vfortro.gestoreta.repository.payments.*;
import com.vfortro.gestoreta.service.payments.PaymentHandler;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;

@Service
public class PaymentService {
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private CouponStockRepository stockRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private FallaRepository fallaRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InventoryItemRepository itemRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private QrCodeService qrCodeService;

    @Autowired
    private CouponConversor couponConversor;




    private final List<PaymentHandler> paymentHandlers;

    public PaymentService(List<PaymentHandler> paymentHandlers) {
        this.paymentHandlers = paymentHandlers;
    }


    @Transactional
    public void processPayment(GenericPaymentRequestDTO request, String email) throws AccessDeniedException, EntityNotFoundException, InsufficientStockException, IllegalStateException, IllegalAccessException {

        //1. Validación de permisos de usuario.
        User manager = userService.readUserAsEntity(email);
        if(manager.getFalla()==null) {
            throw new EntityNotFoundException("El usuari no té falla");
        }
        if(!userService.checkManagerAccess(email)) {
            throw new AccessDeniedException("Sense permís!");
        }

        //2. Procesar el pago
        PaymentType pType = PaymentType.fromValue(request.getType());
        PaymentHandler handler = paymentHandlers.stream()
                .filter(h -> h.supports(pType))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No hi ha manjeador de pagament per a aquest tipus"));
        List<Payment> payments = handler.processPayment(request, manager);

        //3. Guardar los logs.
        paymentRepository.saveAll(payments);
    }

    @Transactional
    public void createCoupon(CouponCreateDTO dto, String email) throws AccessDeniedException, IllegalAccessException, EntityNotFoundException {
        User manager = userService.readUserAsEntity(email);
        if(!userService.checkManagerAccess(email)) {
            throw new AccessDeniedException("Sense permís");
        }
        InventoryItem item = itemRepository.findById(dto.getItemId()).orElseThrow(() -> new EntityNotFoundException("No existeix el item del tiquet"));
        if(!Objects.equals(item.getFalla().getId(), manager.getFalla().getId())) throw new IllegalAccessException("La falla del tiquet i del gestor no son la mateiza");
        Coupon toSave = couponConversor.fromDto2Entity(dto);
        if(!(toSave.getItem().getItemCategory() == ItemCategory.DRINKS) && !(toSave.getItem().getItemCategory() == ItemCategory.FOOD)) throw new IllegalStateException("El tiquet ha de ser de menjar o beguda");
        couponRepository.save(toSave);
    }

    @Transactional
    public void editCoupon(CouponEditDTO dto, String email) throws IllegalAccessException, AccessDeniedException, EntityNotFoundException {
        User manager = userService.readUserAsEntity(email);
        if(!userService.checkManagerAccess(email)) throw new AccessDeniedException("Sense permís");
        Coupon coupon = couponRepository.findById(dto.getCouponId()).orElseThrow(() -> new EntityNotFoundException("No s'ha trobat el tiquet"));
        if(!Objects.equals(coupon.getFalla().getId(), manager.getFalla().getId())) throw new IllegalAccessException("La falla del tiquet i del gestor no son la mateixa");
        if(dto.getPrice() != null) coupon.setPrice(dto.getPrice());
        if(dto.getName() != null && !dto.getName().isBlank()) coupon.setName(dto.getName());
        if(dto.getItemId() != null) {
            InventoryItem item = itemRepository.findById(dto.getItemId()).orElseThrow(() -> new EntityNotFoundException("No existeix el item assignat"));
            if(item.getFalla().getId() != manager.getFalla().getId()) throw new IllegalAccessException("La falla del item no conicideix amb la del gestor");
            coupon.setItem(item);
        }
        couponRepository.save(coupon);
    }


    public void getCouponStockOfUser(Long userId, Long stockId, String email) throws IllegalAccessException, AccessDeniedException, EntityNotFoundException {
        if(!userService.checkManagerAccess(email)) throw new AccessDeniedException("Sense permís");

        CouponStock stock = stockRepository.findById(stockId).orElseThrow(() -> new EntityNotFoundException("No existeix el stock amb id: " + stockId));

        if(!Objects.equals(stock.getUser().getId(), userId)) throw new IllegalAccessException("El stock no es d'aquest usuari");

        CouponStockInfoDTO dto = couponConversor.fromEntity2Dto(stock);

    }

    public String generateStockQR(Long couponId, Long stockId, Long amount, String email) throws Exception, EntityNotFoundException, IllegalAccessException,AccessDeniedException {
        User user = userService.readUserAsEntity(email);
        CouponStock stock = stockRepository.findById(stockId).orElseThrow(() -> new EntityNotFoundException("No existeix el stock"));
        if(!Objects.equals(stock.getCoupon().getCouponId(), couponId)) throw new IllegalAccessException("El stock no es del tiquet indicat");
        if(!Objects.equals(stock.getUser().getId(), user.getId())) throw new AccessDeniedException("No es pot accedir als tiquets d'aquest usuari, no es el teu");


        try {
            // 1. Crear la cadena de texto con el formato que leerá el Dispositivo B
            // Ejemplo: app://exchange?couponId=5&amount=1&stockId=12&storeId=99
            String qrContent = String.format(
                    "app://exchange?couponId=%d&stockId=%d&amount=%d",
                    stock.getCoupon().getCouponId(),
                    stock.getStockId(),
                    amount // Inyectamos la cantidad seleccionada por el usuario
            );
            // 2. Generar el QR convertido a String Base64 (Tamaño ideal para móvil: 350x350)
            String qrBase64 = qrCodeService.generateQrCodeBase64(qrContent, 350, 350);

            return qrBase64;
            // 3. Construir la respuesta con el DTO
        } catch (Exception e) {
            System.out.println("ERROR AL GENERAR EL QR DEL TIQUET");
            throw e;
        }

    }
}
