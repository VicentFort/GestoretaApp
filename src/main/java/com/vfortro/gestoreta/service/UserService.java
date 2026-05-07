package com.vfortro.gestoreta.service;

import com.vfortro.gestoreta.conversor.FoodNeedConversor;
import com.vfortro.gestoreta.conversor.RequestConversor;
import com.vfortro.gestoreta.conversor.UserConversor;
import com.vfortro.gestoreta.dto.food.FoodNeedCreateDTO;
import com.vfortro.gestoreta.dto.requests.RequestCreateDTO;
import com.vfortro.gestoreta.dto.requests.RequestUpdateDTO;
import com.vfortro.gestoreta.dto.users.UserCreateDTO;
import com.vfortro.gestoreta.dto.users.info.UserInfoDTO;
import com.vfortro.gestoreta.dto.users.UserUpdateDTO;
import com.vfortro.gestoreta.model.*;
import com.vfortro.gestoreta.model.enums.FoodNeedType;
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
    private FoodNeedRepository foodNeedRepository;
    @Autowired
    private RequestRepository requestRepository;

    //CONVERSORS
    @Autowired
    private UserConversor userConversor;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private FoodNeedConversor foodNeedConversor;
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
        if(user.getAdminAccess()!= null) {
            Position createPos = new Position();
            createPos.setAdminAccess(true);
            createPos.setUser(saved);
            createPos.setName("Càrrec genèric");
            createPos.setFalla(saved.getFalla());
            createPos.setAdminAccess(user.getAdminAccess());
            createPos.setArtsAccess(false);
            createPos.setBankAccess(false);
            createPos.setHouseholdAccess(false);
            createPos.setLotteryAccess(false);
            createPos.setOtherAccess(false);
            createPos.setPyrotechnicsAccess(false);
            positionRepository.saveAndFlush(createPos);
        }
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

    @Transactional
    public void editAdminAccess(Long userId, Boolean adminAccess) {
        Position pos = positionRepository.findByUserId(userId);
        if(pos!=null) {
            pos.setAdminAccess(adminAccess);
            positionRepository.saveAndFlush(pos);
        } else {
            Position createPos = new Position();
            User user = userRepository.findUserById(userId);
            createPos.setAdminAccess(true);
            createPos.setUser(user);
            createPos.setName("Càrrec genèric");
            createPos.setFalla(user.getFalla());
            createPos.setAdminAccess(adminAccess);
            createPos.setArtsAccess(false);
            createPos.setBankAccess(false);
            createPos.setHouseholdAccess(false);
            createPos.setLotteryAccess(false);
            createPos.setOtherAccess(false);
            createPos.setPyrotechnicsAccess(false);
            positionRepository.saveAndFlush(createPos);
        }



    }

    @Transactional(readOnly = true)
    public boolean checkAdminAccess(String email) {
        User user = userRepository.findUserByEmail(email);
        if(user.getPositions() == null) return false;
        if(user.getPositions().isEmpty()) return false;
        for(Position position : user.getPositions()) {
            if(position.getAdminAccess()) return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public boolean checkBankAccess(String email) {
        User user = userRepository.findUserByEmail(email);
        if(user.getPositions().isEmpty()) return false;
        for(Position position : user.getPositions()) {
            if(position.getBankAccess()) return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public boolean checkLotteryAccess(String email) {
        User user = userRepository.findUserByEmail(email);
        if(user.getPositions().isEmpty()) return false;
        for(Position position : user.getPositions()) {
            if(position.getLotteryAccess()) return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public boolean checkArtsAccess(String email) {
        User user = userRepository.findUserByEmail(email);
        if(user.getPositions().isEmpty()) return false;
        for(Position position : user.getPositions()) {
            if(position.getArtsAccess()) return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public boolean checkPyrothecnicsAccess(String email) {
        User user = userRepository.findUserByEmail(email);
        if(user.getPositions().isEmpty()) return false;
        for(Position position : user.getPositions()) {
            if(position.getPyrotechnicsAccess()) return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public boolean checkHouseHoldAccess(String email) {
        User user = userRepository.findUserByEmail(email);
        if(user.getPositions().isEmpty()) return false;
        for(Position position : user.getPositions()) {
            if(position.getHouseholdAccess()) return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public boolean checkOtherAccess(String email) {
        User user = userRepository.findUserByEmail(email);
        if(user.getPositions().isEmpty()) return false;
        for(Position position : user.getPositions()) {
            if(position.getOtherAccess()) return true;
        }
        return false;
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
    public User readUserAsEntity(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("No existeix el user amb email: " +email));
        return user;
    }

    @Transactional(readOnly = true)
    public FoodNeedCreateDTO readFoodNeed(Long foodNeedId) {
        FoodNeed need = foodNeedRepository.findById(foodNeedId).orElse(null);
        if(need == null) return null;
        return foodNeedConversor.fromEntity2Dto(need);
    }

    @Transactional
    public UserInfoDTO createFoodNeed(String desc, String email) throws AccessDeniedException {
        User user = userRepository.findUserByEmail(email);
        FoodNeed need = new FoodNeed();
        need.setDescription(FoodNeedType.fromString(desc));
        need.setUser(user);
        foodNeedRepository.saveAndFlush(need);
        return userConversor.fromEntity2InfoDto(userRepository.findUserByEmail(email));
    }

    @Transactional
    public UserInfoDTO deleteFoodNeed(Long needId, String email) throws AccessDeniedException {
        User user = userRepository.findUserByEmail(email);
        FoodNeed need = foodNeedRepository.findFoodNeedById(needId);
        if(!Objects.equals(user.getId(), need.getUser().getId())) throw new AccessDeniedException("Sin permiso.");
        foodNeedRepository.deleteById(need.getId());
        return userConversor.fromEntity2InfoDto(userRepository.findUserByEmail(email));
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
    public void updateRequest(RequestUpdateDTO dto, String email) throws AccessDeniedException {
        if(!Objects.equals(dto.getIdFalla(), readUser(email).getFallaId())) throw new AccessDeniedException("Sin permiso para esta falla.");
        if(!checkAdminAccess(email)) throw new AccessDeniedException("Sin permiso.");
        Request request = requestRepository.findRequestById(dto.getRequestId());
        request.setAproved(dto.getAproved());
        request.setReply(dto.getReply());
        requestRepository.saveAndFlush(request);
    }
}
