package com.vfortro.gestoreta.conversor;

import com.vfortro.gestoreta.dto.attendants.AttPrefInfoDTO;
import com.vfortro.gestoreta.dto.events.EventInfoUserDTO;
import com.vfortro.gestoreta.dto.events.EventTagAdminInfoDTO;
import com.vfortro.gestoreta.dto.fallas.info.FallaUserInfoDTO;
import com.vfortro.gestoreta.dto.users.UserCreateDTO;
import com.vfortro.gestoreta.dto.users.info.UserInfoDTO;
import com.vfortro.gestoreta.dto.users.info.UserInfoFallaDTO;
import com.vfortro.gestoreta.model.*;
import com.vfortro.gestoreta.model.enums.AccessType;
import com.vfortro.gestoreta.model.enums.FoodNeedType;
import com.vfortro.gestoreta.repository.FallaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserConversor {

    @Autowired
    private FallaRepository fallaRepository;
    @Autowired
    private EventConversor eventConversor;
    @Autowired
    private EventTagConversor eventTagConversor;

    private final Map<AccessType, Integer> prios = Map.of(AccessType.EMPTY_CHARGE, 0, AccessType.REPRESENTATIVE, 1, AccessType.MANAGER, 2, AccessType.SUPERUSER, 3);

    public UserCreateDTO fromEntity2Dto(User user) {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setUserId(user.getId());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        if (Objects.nonNull(user.getFalla())) dto.setFallaId(user.getFalla().getId());
        dto.setUrlPfp(user.getUrlPfp());
        dto.setBirthday(user.getBirthday());
        dto.setShowBday(user.getShowBday());
        dto.setNickname(user.getNickname());
        return dto;
    }

    public UserInfoFallaDTO fromEntity2InfoFallaDto(User user) {
        UserInfoFallaDTO dto = new UserInfoFallaDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setFullName(user.getName() + " " + user.getSurname());
        dto.setBirthday(user.getBirthday());
        dto.setShowBday(user.getShowBday());
        dto.setJoinDate(user.getJoinDate());
        dto.setNickname(user.getNickname());

        dto.setAccessType(filterChargeTypes(user));
        List<FoodNeedType> needs = user.getNeeds();
        List<AttPrefInfoDTO> prefs = new ArrayList<>();

        dto.setFoodNeeds(needs);
        for(AttendantPreference pref: user.getAttendantPreferences()) {
            AttPrefInfoDTO prefDto = new AttPrefInfoDTO();
            prefDto.setPrefId(pref.getId());
            prefDto.setTagName(pref.getEventTag().getName());
            prefDto.setTagId(pref.getEventTag().getId());
            prefs.add(prefDto);

        }
        dto.setPrefs(prefs);
        return dto;
    }

    public UserInfoDTO fromEntity2InfoDto(User user) {
        UserInfoDTO dto = new UserInfoDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setBirthday(user.getBirthday());
        dto.setShowBday(user.getShowBday());
        dto.setJoinDate(user.getJoinDate());
        dto.setNickname(user.getNickname());
        dto.setAccessType(filterChargeTypes(user));
        if (user.getFalla() != null) dto.setFallaInfo(fromEntity2UserInfo(user.getFalla()));
        List<EventInfoUserDTO> events = new ArrayList<>();
        List<FoodNeedType> needs = user.getNeeds();
        List<AttPrefInfoDTO> tagNamePrefs = new ArrayList<>();
        List<EventInfoUserDTO> attEvents = new ArrayList<>();
        for (Assist a : user.getAssists()) {
            events.add(eventConversor.fromEntity2InfoUserDto(a.getEvent()));
        }
        for (AttendantPreference pref : user.getAttendantPreferences()) {
            AttPrefInfoDTO att = new AttPrefInfoDTO();
            att.setPrefId(pref.getId());
            att.setTagName(pref.getEventTag().getName());
            att.setTagId(pref.getEventTag().getId());
            tagNamePrefs.add(att);
        }
        for(Attendant att : user.getAttendedEvents()) {
            attEvents.add(eventConversor.fromEntity2InfoUserDto(att.getEvent()));
        }
        dto.setEvents(events);
        dto.setFoodNeeds(needs);
        dto.setEventTagPrefs(tagNamePrefs);
        dto.setAttEvents(attEvents);
        return dto;
    }

    public User fromDto2Entity(UserCreateDTO dto) {
        User user = new User();
        user.setId(dto.getUserId());
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setNickname(dto.getNickname());
        if (dto.getFallaId() != null && !fallaRepository.existsById(dto.getFallaId()))
            throw new EntityNotFoundException("La falla asociada al usuario no existe en la base de datos.");
        user.setFalla(fallaRepository.findFallaById(dto.getFallaId()));

        if (dto.getJoinDate() != null) {
            user.setJoinDate(dto.getJoinDate());
        } else {
            user.setJoinDate(LocalDate.now());
        }

        user.setUrlPfp(dto.getUrlPfp());
        user.setBirthday(dto.getBirthday());
        user.setShowBday(dto.getShowBday());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        return user;
    }

    public FallaUserInfoDTO fromEntity2UserInfo(Falla falla) {
        FallaUserInfoDTO dto = new FallaUserInfoDTO();
        dto.setFallaId(falla.getId());
        dto.setCreationDate(falla.getCreationDate());
        dto.setName(falla.getName());
        List<EventInfoUserDTO> events = new ArrayList<>();
        List<EventTagAdminInfoDTO> tags = new ArrayList<>();
        for (Event e : falla.getEvents()) {
            events.add(eventConversor.fromEntity2InfoUserDto(e));
        }
        for (EventTag t : falla.getEventTags()) {
            tags.add(eventTagConversor.fromEntity2Dto(t));
        }
        dto.setEvents(events); dto.setTags(tags);

        return dto;
    }

    private AccessType filterChargeTypes (User user) {
        AccessType accessPivot = AccessType.EMPTY_CHARGE;
        for(Charge c : user.getCharges()) {
            if(c != null && c.getType() != null) {
                if(c.getType() == AccessType.SUPERUSER) {
                    accessPivot = AccessType.SUPERUSER;
                    break;
                }
                if(prios.containsKey(c.getType())) {
                    if(prios.get(c.getType()) > prios.get(accessPivot)) {
                        accessPivot = c.getType();
                    }
                }
            }
        }
        return accessPivot;
    }
}

