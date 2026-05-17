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
import com.vfortro.gestoreta.repository.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    //CONVERSORS
    @Autowired
    private UserConversor userConversor;
    @Autowired
    private RequestConversor requestConversor;

    //OTHER
    @Autowired
    private PasswordEncoder passwordEncoder;




    @Transactional
    public UserCreateDTO createUser(UserCreateDTO user) {
        if(userRepository.existsByEmail(user.getEmail()) || user.getEmail().isEmpty()) throw new EntityExistsException("Ja existeix un usuari amb email: " + user.getEmail());
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
        if(!userRepository.existsByEmail(email)) throw new NullPointerException("El usuario no existe.");
        if(!Objects.equals(email, userRepository.findUserByEmail(email).getEmail())) throw new AccessDeniedException("Sin permiso.");
        User updatedUser = userRepository.findUserByEmail(email);
        if(newUser.getName() != null) updatedUser.setName(newUser.getName());
        if(newUser.getSurname() != null) updatedUser.setSurname(newUser.getSurname());
        if(newUser.getBirthday() != null) updatedUser.setBirthday(newUser.getBirthday());
        if(newUser.getShowBday() != null) updatedUser.setShowBday(newUser.getShowBday());
        if(newUser.getUrlPfp() != null) updatedUser.setUrlPfp(newUser.getUrlPfp());
        if(newUser.getNickname() != null) updatedUser.setNickname(newUser.getNickname());
        User saved = userRepository.saveAndFlush(updatedUser);
        return userConversor.fromEntity2InfoDto(saved);

    }

    @Transactional(readOnly = true)
    public UserInfoDTO readUserSecure(String email) throws AccessDeniedException {
        UserCreateDTO user = readUser(email);
        if(user == null) return null;
        if(!Objects.equals(user.getUserId(), readUser(email).getUserId())) throw new AccessDeniedException("Sin permiso.");
        User userEnt = userRepository.findUserByEmail(email);
        List<Long> eventIds = new ArrayList<>(); List<String> eventTitles = new ArrayList<>();
        for(Assist assist : userEnt.getAssists()) {
            eventIds.add(assist.getEvent().getId());
            eventTitles.add(assist.getEvent().getTitle());
        }
        return userConversor.fromEntity2InfoDto(userEnt);
    }

    @Transactional(readOnly = true)
    public User readUserAsEntity(Long userId) {
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
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("No existeix el user amb email: " +email));
        return user;
    }

    @Transactional
    public void createFoodNeed(String desc, String email) throws AccessDeniedException, EntityNotFoundException, IllegalStateException {
        User user = readUserAsEntity(email);
        if(user.getNeeds().contains(desc)) throw new IllegalStateException("Ja existeix la preferència alimentària");
        user.getNeeds().add(desc);
        User saved = userRepository.save(user);
    }

    @Transactional
    public UserInfoDTO deleteFoodNeed(String needType, String email) throws AccessDeniedException, EntityNotFoundException {
        User user = readUserAsEntity(email);
        if(!user.getNeeds().contains(needType)) throw new AccessDeniedException("L'usuari no té aquesta necessitat alimentària");
        user.getNeeds().remove(needType);
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
        if(!Objects.equals(dto.getIdFalla(), readUser(email).getFallaId())) throw new AccessDeniedException("Sin permiso para esta falla.");
        if(!checkManagerAccess(email)) throw new AccessDeniedException("Sin permiso.");
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
}
