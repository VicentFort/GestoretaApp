package com.vfortro.gestoreta.conversor;

import com.vfortro.gestoreta.conversor.payments.CouponConversor;
import com.vfortro.gestoreta.dto.assists.AssistDTO;
import com.vfortro.gestoreta.dto.attendants.AttPrefInfoDTO;
import com.vfortro.gestoreta.dto.events.EventInfoDTO;
import com.vfortro.gestoreta.dto.events.EventInfoUserDTO;
import com.vfortro.gestoreta.dto.events.EventTagAdminInfoDTO;
import com.vfortro.gestoreta.dto.fallas.info.FallaAdInfoDTO;
import com.vfortro.gestoreta.dto.fallas.info.FallaUserInfoDTO;
import com.vfortro.gestoreta.dto.payments.info.CouponStockInfoDTO;
import com.vfortro.gestoreta.dto.users.UserCreateDTO;
import com.vfortro.gestoreta.dto.users.info.UserInfoDTO;
import com.vfortro.gestoreta.dto.users.info.UserInfoFallaDTO;
import com.vfortro.gestoreta.dto.users.notifications.NotificationInfoDTO;
import com.vfortro.gestoreta.model.*;
import com.vfortro.gestoreta.model.enums.AccessType;
import com.vfortro.gestoreta.model.enums.FoodNeedType;
import com.vfortro.gestoreta.repository.FallaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    @Autowired
    private UserNotificationCovnersor notificationCovnersor;
    @Autowired
    private AssistConversor assistConversor;
    @Autowired
    private CouponConversor couponConversor;


    private final Map<AccessType, Integer> prios = Map.of(AccessType.EMPTY_CHARGE, 0, AccessType.REPRESENTATIVE, 1, AccessType.MANAGER, 2, AccessType.SUPERUSER, 3);

    public UserCreateDTO fromEntity2Dto(User user) {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setUserId(user.getId());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        if (Objects.nonNull(user.getFalla())) dto.setFallaId(user.getFalla().getId());
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
        List<FallaAdInfoDTO> fallaAds = new ArrayList<>();
        List<EventInfoUserDTO> events = new ArrayList<>();
        List<FoodNeedType> needs = user.getNeeds();
        List<AttPrefInfoDTO> tagNamePrefs = new ArrayList<>();
        List<EventInfoUserDTO> attEvents = new ArrayList<>();
        List<NotificationInfoDTO> nots = new ArrayList<>();
        List<CouponStockInfoDTO> coupons = new ArrayList<>();
        for (Assist a : user.getAssists()) {
            EventInfoUserDTO eInfo = eventConversor.fromEntity2InfoUserDto(a.getEvent());
            eInfo.setAssist(assistConversor.formEntity2Dto(a));
            events.add(eInfo);
        }
        for (AttendantPreference pref : user.getAttendantPreferences()) {
            AttPrefInfoDTO att = new AttPrefInfoDTO();
            att.setPrefId(pref.getId());
            att.setTagName(pref.getEventTag().getName());
            att.setTagId(pref.getEventTag().getId());
            tagNamePrefs.add(att);
        }

        if(user.getFalla() == null) {
            List<Falla> fallas = fallaRepository.findAll();
            fallas.forEach(falla -> {
                fallaAds.add(fromEntity2Ad(falla));
            });
        }

        user.getAttendedEvents().forEach(att -> {
            Event event = att.getEvent();
            EventInfoUserDTO eDto = eventConversor.fromEntity2InfoUserDto(event);
            event.getAssists().forEach(assist -> {
                if(Objects.equals(assist.getUser().getId(), user.getId())) {
                    eDto.setAssist(assistConversor.formEntity2Dto(assist));
                }
            });
            attEvents.add(eDto);
        });


        user.getNotifications().forEach( notification -> {
            nots.add(notificationCovnersor.fromEntity2Dto(notification));
        });

        user.getStocks().forEach(stock -> {
            coupons.add(couponConversor.fromEntity2Dto(stock));
        });
        dto.setNotifications(nots);
        dto.setEvents(events);
        dto.setFoodNeeds(needs);
        dto.setEventTagPrefs(tagNamePrefs);
        dto.setAttEvents(attEvents);
        dto.setCouponStocks(coupons);
        dto.setFallaAds(fallaAds);
        return dto;
    }

    public User fromDto2Entity(UserCreateDTO dto) {
        User user = new User();
        user.setId(dto.getUserId());
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setNickname(dto.getNickname());

        if (dto.getJoinDate() != null) {
            user.setJoinDate(dto.getJoinDate());
        } else {
            user.setJoinDate(LocalDate.now());
        }


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
        dto.setDescription(falla.getDescription());
        dto.setOpenRequests(falla.getOpenRequests());
        if(falla.getShieldUrl() != null) {
            String base64Image = Base64.getEncoder().encodeToString(falla.getShieldUrl());
            dto.setShield("data:image/jpeg;base64," + base64Image);
        }
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

    public FallaAdInfoDTO fromEntity2Ad(Falla falla) {
        FallaAdInfoDTO dto = new FallaAdInfoDTO();
        dto.setId(falla.getId());
        dto.setName(falla.getName());
        dto.setCreationDate(falla.getCreationDate());
        dto.setMemberCount(falla.getUsers().size());
        dto.setDescription(falla.getDescription());
        dto.setOpenRequests(falla.getOpenRequests());
        if(falla.getShieldUrl() != null) {
            String base64Image = Base64.getEncoder().encodeToString(falla.getShieldUrl());
            dto.setShield("data:image/jpeg;base64," + base64Image);
        }
        return dto;
    }
}

