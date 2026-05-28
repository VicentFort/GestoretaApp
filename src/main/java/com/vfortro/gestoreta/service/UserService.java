package com.vfortro.gestoreta.service;

import com.vfortro.gestoreta.conversor.RequestConversor;
import com.vfortro.gestoreta.conversor.UserConversor;
import com.vfortro.gestoreta.dto.requests.RequestCreateDTO;
import com.vfortro.gestoreta.dto.requests.RequestUpdateDTO;
import com.vfortro.gestoreta.dto.users.UserCreateDTO;
import com.vfortro.gestoreta.dto.users.info.UserInfoDTO;
import com.vfortro.gestoreta.dto.users.UserUpdateDTO;
import com.vfortro.gestoreta.model.*;
import com.vfortro.gestoreta.model.enums.AccessType;
import com.vfortro.gestoreta.model.enums.FoodNeedType;
import com.vfortro.gestoreta.model.enums.UserNotificationType;
import com.vfortro.gestoreta.repository.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class UserService {

    //REPOSITORIES
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventTagRepository eventTagRepository;
    @Autowired
    private AttendandPreferenceRepository attendandPreferenceRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private ChargeRepository chargeRepository;
    @Autowired
    private FallaRepository fallaRepository;
    @Autowired
    private UserNotificationRepository notificationRepository;

    //CONVERSORS
    @Autowired
    private UserConversor userConversor;
    @Autowired
    private RequestConversor requestConversor;

    //OTHER
    @Autowired
    private PasswordEncoder passwordEncoder;




    @Transactional
    public UserCreateDTO createUser(UserCreateDTO user) throws EntityExistsException, IllegalArgumentException {
        if(userRepository.existsByEmail(user.getEmail()) || user.getEmail().isEmpty()) throw new EntityExistsException("Ja existeix un usuari amb email: " + user.getEmail());
        LocalDateTime eighteenYearsAgoToday = LocalDateTime.now().minusYears(18);
        if(user.getBirthday().isAfter(eighteenYearsAgoToday.toLocalDate())) { throw new IllegalArgumentException("L'usuari ha de ser major d'edat");}
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User toSave = userConversor.fromDto2Entity(user);
        toSave.setCreationDate(LocalDateTime.now());
        User saved = userRepository.saveAndFlush(toSave);
        Charge charge = new Charge();
        charge.setUser(saved);
        charge.setName("Càrrec de creació");
        charge.setFalla(saved.getFalla());
        if(user.getAccessType() != null) {
            charge.setType(user.getAccessType());
        } else {
            charge.setType(AccessType.EMPTY_CHARGE);
        }
        chargeRepository.saveAndFlush(charge);
        return userConversor.fromEntity2Dto(saved);
    }

    @Transactional(readOnly = true)
    public UserCreateDTO readUser(String email) throws EntityNotFoundException{
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("L'usuari amb email: " + email + " no existeix."));
        return userConversor.fromEntity2Dto(user);
    }

    @Transactional(readOnly = true)
    public UserCreateDTO readUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) return null;
        return userConversor.fromEntity2Dto(user);
    }

    @Transactional
    public UserInfoDTO updateUser(UserUpdateDTO newUser, String email) throws AccessDeniedException, NullPointerException {
        if(!userRepository.existsByEmail(email)) throw new NullPointerException("El usuari no existeix.");
        if(!Objects.equals(email, userRepository.findUserByEmail(email).getEmail())) throw new AccessDeniedException("Sesne permís.");
        User updatedUser = userRepository.findUserByEmail(email);
        if(newUser.getName() != null) updatedUser.setName(newUser.getName());
        if(newUser.getSurname() != null) updatedUser.setSurname(newUser.getSurname());
        if(newUser.getBirthday() != null) updatedUser.setBirthday(newUser.getBirthday());
        if(newUser.getShowBday() != null) updatedUser.setShowBday(newUser.getShowBday());
        if(newUser.getNickname() != null) updatedUser.setNickname(newUser.getNickname());
        User saved = userRepository.saveAndFlush(updatedUser);
        return userConversor.fromEntity2InfoDto(saved);

    }

    @Transactional(readOnly = true)
    public UserInfoDTO readUserSecure(String email) throws AccessDeniedException {
        User user = readUserAsEntity(email);
        if(user == null) return null;
        if(!Objects.equals(user.getId(), readUser(email).getUserId())) throw new AccessDeniedException("Sense permís");
        return userConversor.fromEntity2InfoDto(user);
    }

    @Transactional(readOnly = true)
    public User readUserAsEntity(Long userId) throws EntityNotFoundException{
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("No existeix el usuari amb id: " + userId));
    }
    @Transactional
    public void createAttPreferences(String email, Long tagId) throws AccessDeniedException {
        User user = userRepository.findUserByEmail(email);
            EventTag tag = eventTagRepository.findTagById(tagId);
            if(!Objects.equals(user.getFalla().getId(), tag.getFalla().getId())) throw new AccessDeniedException("Sin acceso a esta falla.");
            if(tag.getAttendantPreferences().stream().anyMatch(attendantPreference -> {
            return Objects.equals(attendantPreference.getUser().getId(), readUser(email).getUserId()) && Objects.equals(attendantPreference.getEventTag().getId(), tagId);
            })) throw new EntityExistsException("L'usuari ja té preferència per esta etiqueta");
            AttendantPreference pref = new AttendantPreference();
            pref.setUser(user);
            pref.setEventTag(tag);
            attendandPreferenceRepository.saveAndFlush(pref);
    }

    @Transactional
    public void removeAttPrefs(String email, List<Long> prefIds) throws AccessDeniedException {
        User user = userRepository.findUserByEmail(email);
        List<AttendantPreference> prefs = attendandPreferenceRepository.getAllByUser(user);
        for(AttendantPreference pref : prefs) {
            if(!Objects.equals(pref.getUser().getFalla().getId(), user.getFalla().getId())) {
                throw new AccessDeniedException("Sin acceso a esta falla");
            }
        }
        attendandPreferenceRepository.deleteAllById(prefIds);
    }

    @Transactional(readOnly = true)
    public User readUserAsEntity(String email) throws EntityNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("No existeix el usuari amb email: " +email));
        return user;
    }

    @Transactional
    public void createFoodNeed(String needType, String email) throws AccessDeniedException, EntityNotFoundException, IllegalStateException {
        System.out.println("Need type: " + needType);
        User user = readUserAsEntity(email);
        FoodNeedType need = FoodNeedType.fromString(needType);
        if(user.getNeeds().contains(need)) throw new IllegalStateException("Ja existeix la preferència alimentària");
        user.getNeeds().add(need);
        User saved = userRepository.save(user);
    }

    @Transactional
    public UserInfoDTO deleteFoodNeed(String needType, String email) throws AccessDeniedException, EntityNotFoundException {
        FoodNeedType need = FoodNeedType.fromString(needType);
        User user = readUserAsEntity(email);
        if(!user.getNeeds().contains(need)) throw new AccessDeniedException("L'usuari no té aquesta necessitat alimentària");
        user.getNeeds().remove(need);
        User saved = userRepository.saveAndFlush(user);
        return userConversor.fromEntity2InfoDto(saved);
    }

    @Transactional(readOnly = true)
    public RequestUpdateDTO readRequest(Long requestId) {
        Request req = requestRepository.findById(requestId).orElse(null);
        if(req == null) return null;
        return requestConversor.fromEntity2Dto(req);
    }

    @Transactional
    public RequestUpdateDTO createRequest(RequestCreateDTO dto, String email) throws NullPointerException, EntityNotFoundException, IllegalAccessException, AccessDeniedException {
        if(!Objects.equals(dto.getIdUser(), readUser(email).getUserId())) throw new AccessDeniedException("Sin permiso.");
        Request toSave = requestConversor.fromDto2Entity(dto);
        if(Objects.nonNull(toSave.getUser().getFalla()))
            throw new IllegalAccessException("El usuario con id:" + dto.getIdUser() + " ya está en una falla.");
        Request saved = requestRepository.saveAndFlush(toSave);
        return requestConversor.fromEntity2Dto(saved);
    }

    @Transactional
    public void updateRequest(RequestUpdateDTO dto, String email) throws AccessDeniedException, IllegalAccessException {
        if(!Objects.equals(dto.getIdFalla(), readUser(email).getFallaId())) throw new AccessDeniedException("Sense permís");
        if(!checkManagerAccess(email)) throw new AccessDeniedException("Sense permís.");
        Request request = requestRepository.findRequestById(dto.getRequestId());
        request.setAproved(dto.getAproved());
        request.setReply(dto.getReply());
        requestRepository.saveAndFlush(request);
    }

    @Transactional(readOnly = true)
    public Boolean checkManagerAccess(String email) throws IllegalAccessException, EntityNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow( () -> new EntityNotFoundException("No existeix el gestor amb email: " + email));
        if(user.getFalla() == null) {
            throw new IllegalAccessException("El gestor no te falla");
        }
        if(!fallaRepository.existsById(user.getFalla().getId())) {
            throw new EntityExistsException("La falla del gestor no existeix a la base de dades");
        }
        if(user.getCharges() == null) return false;
        if(user.getCharges().isEmpty()) return false;
        for(Charge charge : user.getCharges()) {
            if(charge.getType() == AccessType.MANAGER || charge.getType() == AccessType.SUPERUSER) return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public Boolean checkSuperUserAccess(String email) throws IllegalAccessException, EntityNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow( () -> new EntityNotFoundException("No existeix el superusuari amb email: " + email));
        if(user.getFalla() == null) {
            throw new IllegalAccessException("El superusuari no te falla");
        }
        if(!fallaRepository.existsById(user.getFalla().getId())) {
            throw new EntityExistsException("La falla del superusuari no existeix a la base de dades");
        }
        if(user.getCharges() == null) return false;
        if(user.getCharges().isEmpty()) return false;
        for(Charge charge : user.getCharges()) {
            if(charge.getType() == AccessType.SUPERUSER) return true;
        }
        return false;
    }

    @Transactional
    public void changePfp(MultipartFile pfpImage, String email) throws IOException {
        User user = readUserAsEntity(email);
        user.setPfpContent(pfpImage.getBytes());
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public ByteArrayResource downloadPfp(String email) throws NullPointerException, EntityNotFoundException {
        User user = readUserAsEntity(email);
        if(user.getPfpContent() == null) throw new NullPointerException("L'usuari no te imatge de perfil");
        return new ByteArrayResource(user.getPfpContent());
    }

    @Transactional
    public void readNotification(Long notificationId, String email) throws EntityNotFoundException, AccessDeniedException, IllegalStateException {
        User user = readUserAsEntity(email);
        if(user.getNotifications().stream().noneMatch(n -> Objects.equals(n.getNotificationId(), notificationId))) {
            throw new AccessDeniedException("Sense permís");
        }
        UserNotification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new EntityNotFoundException("No existeix la notificació"));
        if(notification.getRead()) throw new IllegalStateException("La notificació ja s'ha llegit anteriorment");

        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
