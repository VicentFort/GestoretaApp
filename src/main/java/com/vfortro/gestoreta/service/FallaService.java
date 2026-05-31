package com.vfortro.gestoreta.service;

import com.vfortro.gestoreta.conversor.*;
import com.vfortro.gestoreta.dto.fallas.info.FallaAdminInfoDTO;
import com.vfortro.gestoreta.dto.fallas.FallaCreateDTO;
import com.vfortro.gestoreta.dto.fallas.info.FallaUserInfoDTO;
import com.vfortro.gestoreta.dto.fallas.FallaUpdateDTO;
import com.vfortro.gestoreta.dto.requests.RequestCreateDTO;
import com.vfortro.gestoreta.dto.requests.RequestUpdateDTO;
import com.vfortro.gestoreta.dto.users.UserCreateDTO;
import com.vfortro.gestoreta.model.*;
import com.vfortro.gestoreta.model.enums.AccessType;
import com.vfortro.gestoreta.model.enums.UserNotificationType;
import com.vfortro.gestoreta.repository.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class FallaService {

    //REPOSITORIES
    @Autowired
    private FallaRepository fallaRepository;
    @Autowired
    private EventTagRepository eventTagRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChargeRepository chargeRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private UserNotificationRepository notificationRepository;

    //CONVERSORS
    @Autowired
    private FallaConversor fallaConversor;
    @Autowired
    private RequestConversor requestConversor;

    //SERVICES
    @Autowired
    private UserService userService;



    @Transactional
    public FallaCreateDTO createFalla(FallaCreateDTO falla) {
        Falla saved = fallaRepository.saveAndFlush(fallaConversor.fromDto2Entity(falla));
        return fallaConversor.fromEntity2DTO(saved);
    }

    @Transactional
    public void updateFalla(FallaUpdateDTO newFalla, String email) throws AccessDeniedException, EntityNotFoundException, IllegalAccessException {
        if(!userService.checkManagerAccess(email)) throw new AccessDeniedException("Sin permiso.");
        UserCreateDTO user = userService.readUser(email);
        if(user.getFallaId() == null) throw new NullPointerException("El usuario no tiene falla asignada.");
        Falla updatedFalla = fallaRepository.findFallaById(user.getFallaId());
        if(!Objects.equals(updatedFalla.getId(), userService.readUser(email).getFallaId())) throw new AccessDeniedException("Sin permiso a esta falla.");
        if(newFalla.getName() != null) updatedFalla.setName(newFalla.getName());
        if(newFalla.getCreationDate() != null) updatedFalla.setCreationDate(newFalla.getCreationDate());
        fallaRepository.saveAndFlush(updatedFalla);

    }

    @Transactional(readOnly = true)
    public FallaUserInfoDTO readFalla(Long fallaId) {
        Falla falla = fallaRepository.findById(fallaId).orElse(null);
        if(falla == null) return null;
        FallaUserInfoDTO result = new FallaUserInfoDTO();
        result.setCreationDate(falla.getCreationDate());
        result.setFallaId(fallaId);
        result.setName(falla.getName());
        return result;
    }

    @Transactional
    public void addEventTag(String email, String name) throws AccessDeniedException, EntityNotFoundException, EntityExistsException, IllegalAccessException {
        UserCreateDTO userDto = userService.readUser(email);
        if(userDto.getFallaId()== null) throw new EntityNotFoundException("El usuario no tiene una falla asociada.");
        if(!userService.checkManagerAccess(email)) throw new AccessDeniedException("Sin acceso.");
        Falla falla = fallaRepository.findFallaById(userDto.getFallaId());
        if(eventTagRepository.existsEventTagByNameAndFalla(name, falla)) throw new EntityExistsException("Ya existe una etiqueta: " + name + " para la falla: " + falla.getName());
        EventTag tagSave = new EventTag();
        tagSave.setFalla(falla);
        tagSave.setName(name);
        eventTagRepository.saveAndFlush(tagSave);
    }

    @Transactional(readOnly = true)
    public FallaAdminInfoDTO getFallaInfo(String email) throws AccessDeniedException, IllegalAccessException {
        UserCreateDTO infoDto = userService.readUser(email);
        if(infoDto.getFallaId()==null) throw new EntityNotFoundException("No existe la falla del usuario.");
        if(!userService.checkManagerAccess(email)) throw new AccessDeniedException("Sin permiso.");
        Falla falla = fallaRepository.findFallaById(infoDto.getFallaId());
        return fallaConversor.fromEntity2AdminInfo(falla);

    }

    @Transactional
    public void editAccessType(Long userId, AccessType type, String email) throws AccessDeniedException, IllegalStateException, EntityNotFoundException, IllegalAccessException {

        //Buscamos en la B.D. el usuario y el gestor asociados a la operación.
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("No existeix l'usuari"));
        if(!userService.checkSuperUserAccess(email)) throw new AccessDeniedException("Sense permissos.");
        User manager = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("No existeix el gestor"));
        //Comprobamos que ambos tengan falla.
        if(user.getFalla() == null) {
            throw new IllegalAccessException("L'usuari no està en una falla");
        }

        if(!fallaRepository.existsById(user.getFalla().getId())) {
            throw new EntityNotFoundException("La falla del usuari no existeix en la base de dades");
        }

        //Comprobamos que la falla sea la misma.
        if(!Objects.equals(manager.getFalla(), user.getFalla())) {
            throw new IllegalStateException("La falla del gestor i l'usuari no son la mateixa");
        }
        Falla falla = manager.getFalla();

        //Modificamos el cargo.
        Charge charge = user.getCharges().stream().findFirst().orElse(null);
        if(charge == null) { //Si no existe el cargo.
            charge = new Charge();
            charge.setType(type);
            charge.setUser(user);
            charge.setFalla(falla);
        } else { //Si ya existe el cargo.
            charge.setType(type);
        }

        //Guardamos el cargo.
        chargeRepository.save(charge);
    }

    @Transactional
    public void deleteEventTag(Long tagId, String email) throws AccessDeniedException, IllegalAccessException, EntityNotFoundException {
        if(!userService.checkManagerAccess(email)) throw new AccessDeniedException("Sense permís");
        if(!eventTagRepository.existsById(tagId)) throw new EntityNotFoundException("No existeix la etiqueta");
        eventTagRepository.deleteById(tagId);
    }

    public void updateShield(MultipartFile shieldImage, Long fallaId, String email) throws IllegalAccessException, IOException, EntityNotFoundException, IllegalStateException {
        User manager = userService.readUserAsEntity(email);
        if(!userService.checkManagerAccess(email)) {
            throw new AccessDeniedException("Sense permís.");
        }
        if(!Objects.equals(manager.getFalla().getId(), fallaId)) {
            throw new IllegalAccessException("Sense permís.");
        }

        System.out.println("Shield image length: "+shieldImage.getBytes().length);
        Falla falla = fallaRepository.findById(fallaId).orElseThrow(() -> new EntityNotFoundException("No existeix la falla amb id: " + fallaId));
        falla.setShieldUrl(shieldImage.getBytes());

        fallaRepository.save(falla);
    }

    @Transactional
    public void updateRequest(RequestUpdateDTO request, String email) throws IllegalAccessException, AccessDeniedException, EntityNotFoundException {
        if(!userService.checkSuperUserAccess(email)) throw new AccessDeniedException("Sense permís.");
        User manager = userService.readUserAsEntity(email);
        Falla falla = fallaRepository.findById(request.getIdFalla()).orElseThrow(() -> new EntityNotFoundException("No existeix la falla amb id: " + request.getIdFalla()));
        User user = userService.readUserAsEntity(request.getIdUser());

        Request req = requestRepository.findById(request.getRequestId()).orElseThrow(() -> new EntityNotFoundException("No existeix la sol·licitud amb id: " + request.getRequestId()));

        if(user.getFalla() != null) {
            requestRepository.delete(req);
            throw new IllegalStateException("L'usuari ja te falla");
        }
        if(manager.getFalla().getId() != req.getFalla().getId()) throw new IllegalAccessException("La falla de la sol·licitud no conicideix amb la falla del gestor");

        if(request.getReply().isBlank()) throw new IllegalStateException("La resposta està en blanc");

        if(request.getAproved()) {
            joinFalla(user, falla);
            req.setAproved(true);
            UserNotification notification = new UserNotification();
            notification.setRead(false);
            notification.setUser(user);
            notification.setMessage("La teua sol·licitud a la falla: " + falla.getName() + " ha sigut aprovada amb la resolució: " + req.getReply());
            notification.setType(UserNotificationType.REQUEST_RESOLUTION);
            notification.setDate(LocalDateTime.now());
            notification.setFalla(falla);
            notificationRepository.save(notification);
        } else {
            req.setAproved(false);
        }
        req.setResolutionDate(LocalDateTime.now());
        requestRepository.save(req);
    }
    @Transactional
    public void joinFalla(User user, Falla falla) throws IllegalStateException {
        if(user.getFalla() != null) {
            throw new IllegalStateException("El usuari ja te falla.");
        }
        user.setFalla(falla);
        user.setJoinDate(LocalDate.now());

        User saved = userRepository.save(user);

        Charge charge = new Charge();
        charge.setType(AccessType.EMPTY_CHARGE);
        charge.setFalla(falla);
        charge.setUser(saved);
        charge.setName("Càrrec de la falla: " + falla.getName());
        chargeRepository.save(charge);

    }

    @Transactional
    public void createRequest(RequestCreateDTO request) throws EntityNotFoundException {
        if(!fallaRepository.existsById(request.getIdFalla())) {
            throw new EntityNotFoundException("La falla no existeix");
        }
        User user = userService.readUserAsEntity(request.getIdUser());

        if(user.getFalla() != null) throw new IllegalStateException("L'usuari ja te falla.");
        if(request.getMessage().isBlank()) throw new IllegalStateException("El missatge está en blanc");

        Request toSave = requestConversor.fromDto2Entity(request);
        toSave.setCreationDate(LocalDateTime.now());
        requestRepository.save(toSave);
    }
}
