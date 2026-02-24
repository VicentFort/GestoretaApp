package com.vfortro.gestoreta.service;

import java.time.LocalDate;

import com.vfortro.gestoreta.conversor.EventConversor;
import com.vfortro.gestoreta.conversor.UserConversor;
import com.vfortro.gestoreta.dto.events.AttendantPreferenceInfoDto;
import com.vfortro.gestoreta.dto.events.EventInfoDto;
import com.vfortro.gestoreta.dto.fallas.FallaInfoDto;
import com.vfortro.gestoreta.dto.users.UserCreateDto;
import com.vfortro.gestoreta.dto.users.UserInfoDto;
import com.vfortro.gestoreta.dto.users.UserUpdateDto;
import com.vfortro.gestoreta.model.*;
import com.vfortro.gestoreta.repository.AttendandPreferenceRepository;
import com.vfortro.gestoreta.repository.EventTagRepository;
import com.vfortro.gestoreta.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserConversor userConversor;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EventTagRepository eventTagRepository;
    @Autowired
    private AttendandPreferenceRepository attendandPreferenceRepository;
    @Autowired
    private EventConversor eventConversor;


    @Transactional
    public UserCreateDto createUser(UserCreateDto user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(userConversor.fromDto2Entity(user));
        return userConversor.fromEntity2Dto(saved);
    }

    @Transactional(readOnly = true)
    public UserCreateDto readUser(String email) {
        User user = (User) userRepository.findByEmail(email).orElse(null);
        if(user == null) return null;
        return userConversor.fromEntity2Dto(user);
    }

    @Transactional(readOnly = true)
    public UserCreateDto readUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) return null;
        return userConversor.fromEntity2Dto(user);
    }

    @Transactional
    public UserInfoDto updateUser(UserUpdateDto newUser, String email) throws AccessDeniedException, NullPointerException {
        if(!userRepository.existsByEmail(email)) throw new NullPointerException("El usuario no existe.");
        if(!Objects.equals(email, userRepository.findUserByEmail(email).getEmail())) throw new AccessDeniedException("Sin permiso.");
        User updatedUser = userRepository.findUserByEmail(email);
        System.out.println(newUser.getName());
        if(newUser.getName() != null) updatedUser.setName(newUser.getName());
        if(newUser.getSurname() != null) updatedUser.setSurname(newUser.getSurname());
        if(newUser.getBirthday() != null) updatedUser.setBirthday(newUser.getBirthday());
        if(newUser.getShowBday() != null) updatedUser.setShowBday(newUser.getShowBday());
        if(newUser.getUrlPfp() != null) updatedUser.setUrlPfp(newUser.getUrlPfp());
        User saved = userRepository.saveAndFlush(updatedUser);
        System.out.println(saved.getName());
        return userConversor.fromEntity2InfoDto(saved);

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
    public UserInfoDto readUserSecure( String email) throws AccessDeniedException {
        UserCreateDto user = readUser(email);
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
    public FallaInfoDto getFallaInfo(String email) {
        User user =userRepository.findUserByEmail(email);
        if(user.getFalla() == null) return new FallaInfoDto(0L, "INEXISTENTE", LocalDate.now());
        FallaInfoDto result = new FallaInfoDto();
        result.setFallaId(user.getFalla().getId());
        result.setName(user.getFalla().getName());
        result.setCreationDate(user.getFalla().getCreationDate());
        return result;
    }

    @Transactional
    public void createAttPreferences(String email, List<Long> tagIds) throws AccessDeniedException {
        User user = userRepository.findUserByEmail(email);
        List<AttendantPreference> prefs = new ArrayList<>();
        for(Long tagId: tagIds) {
            EventTag tag = eventTagRepository.findTagById(tagId);
            if(!Objects.equals(user.getFalla().getId(), tag.getFalla().getId())) throw new AccessDeniedException("Sin acceso a esta falla.");
            AttendantPreference pref = new AttendantPreference();
            pref.setUser(user);
            pref.setEventTag(tag);
            prefs.add(pref);
        }
        attendandPreferenceRepository.saveAllAndFlush(prefs);
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
    public List<AttendantPreferenceInfoDto> getAttPrefs(String email) throws NullPointerException {
        List<AttendantPreferenceInfoDto> prefs = new ArrayList<>();
        User user = userRepository.findUserByEmail(email);
        if(user.getFalla()== null) {
            throw new NullPointerException("El usuario no tiene falla asignada");
        }
        for(AttendantPreference pref : user.getAttendantPreferences()) {
            AttendantPreferenceInfoDto dto = new AttendantPreferenceInfoDto();
            dto.setId(pref.getId());
            dto.setEventTagName(pref.getEventTag().getName());
            dto.setUserName(user.getName());
            dto.setUserSurname(user.getSurname());
            prefs.add(dto);
        }
        return prefs;
    }

    public List<EventInfoDto> getEvents(String email) {
        User user = userRepository.findUserByEmail(email);
        List<EventInfoDto> events = new ArrayList<>();
        for(Assist assist : user.getAssists()) {
            events.add(eventConversor.fromEntity2InfoDto(assist.getEvent()));
        }
        return events;
    }
}
