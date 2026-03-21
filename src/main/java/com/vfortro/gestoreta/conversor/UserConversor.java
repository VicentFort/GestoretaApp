package com.vfortro.gestoreta.conversor;

import com.vfortro.gestoreta.dto.assists.AttPrefInfoDto;
import com.vfortro.gestoreta.dto.events.EventInfoDto;
import com.vfortro.gestoreta.dto.events.EventInfoUserDto;
import com.vfortro.gestoreta.dto.events.EventTagInfoDto;
import com.vfortro.gestoreta.dto.fallas.FallaUserInfoDto;
import com.vfortro.gestoreta.dto.food.FoodNeedCreateDto;
import com.vfortro.gestoreta.dto.users.UserCreateDto;
import com.vfortro.gestoreta.dto.users.UserInfoDto;
import com.vfortro.gestoreta.dto.users.UserInfoFallaDto;
import com.vfortro.gestoreta.model.*;
import com.vfortro.gestoreta.repository.FallaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserConversor {

    @Autowired
    private FallaRepository fallaRepository;
    @Autowired
    private EventConversor eventConversor;
    @Autowired
    private FoodNeedConversor foodNeedConversor;
    @Autowired
    private EventTagConversor eventTagConversor;


    public UserCreateDto fromEntity2Dto(User user) {
        UserCreateDto dto = new UserCreateDto();
        dto.setUserId(user.getId());
        dto.setCreationDate(user.getCreationDate());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        if (Objects.nonNull(user.getFalla())) dto.setFallaId(user.getFalla().getId());
        dto.setUrlPfp(user.getUrlPfp());
        dto.setBirthday(user.getBirthday());
        dto.setShowBday(user.getShowBday());
        dto.setNickname(user.getNickname());
        return dto;
    }

    public UserInfoFallaDto fromEntity2InfoFallaDto(User user) {
        UserInfoFallaDto dto = new UserInfoFallaDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setBirthday(user.getBirthday());
        dto.setShowBday(user.getShowBday());
        dto.setJoinDate(user.getJoinDate());
        dto.setNickname(user.getNickname());
        dto.setAdminAccess(user.getPositions().stream().anyMatch(Position::getAdminAccess));
        List<FoodNeedCreateDto> needs = new ArrayList<>();
        List<AttPrefInfoDto> prefs = new ArrayList<>();
        for (FoodNeed need : user.getFoodNeeds()) {
            FoodNeedCreateDto dtoN = new FoodNeedCreateDto();
            if (need.getUser().getId() != null) dtoN.setUserId(need.getUser().getId());
            if (need.getId() != null) dtoN.setFoodNeedId(need.getId());
            if (need.getDescription() != null) dtoN.setDescription(need.getDescription());
            needs.add(dtoN);
        }
        dto.setFoodNeeds(needs);
        for(AttendantPreference pref: user.getAttendantPreferences()) {
            AttPrefInfoDto prefDto = new AttPrefInfoDto();
            prefDto.setPrefId(pref.getId());
            prefDto.setTagName(pref.getEventTag().getName());
            prefDto.setTagId(pref.getEventTag().getId());
            prefs.add(prefDto);

        }
        dto.setPrefs(prefs);
        return dto;
    }

    public UserInfoDto fromEntity2InfoDto(User user) {
        UserInfoDto dto = new UserInfoDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setBirthday(user.getBirthday());
        dto.setShowBday(user.getShowBday());
        dto.setJoinDate(user.getJoinDate());
        dto.setNickname(user.getNickname());
        dto.setAdminAccess(user.getPositions().stream().anyMatch(Position::getAdminAccess));
        if (user.getFalla() != null) dto.setFallaInfo(fromEntity2UserInfo(user.getFalla()));
        List<EventInfoUserDto> events = new ArrayList<>();
        List<FoodNeedCreateDto> needs = new ArrayList<>();
        List<AttPrefInfoDto> tagNamePrefs = new ArrayList<>();
        List<EventInfoUserDto> attEvents = new ArrayList<>();
        for (Assist a : user.getAssists()) {
            events.add(eventConversor.fromEntity2InfoUserDto(a.getEvent()));
        }
        for (FoodNeed need : user.getFoodNeeds()) {
            needs.add(foodNeedConversor.fromEntity2Dto(need));
        }
        for (AttendantPreference pref : user.getAttendantPreferences()) {
            AttPrefInfoDto att = new AttPrefInfoDto();
            att.setPrefId(pref.getId());
            att.setTagName(pref.getEventTag().getName());
            att.setTagId(pref.getEventTag().getId());
            tagNamePrefs.add(att);
        }
        for(Attendant att : user.getAttendants()) {
            attEvents.add(eventConversor.fromEntity2InfoUserDto(att.getEvent()));
        }
        dto.setEvents(events);
        dto.setFoodNeeds(needs);
        dto.setEventTagPrefs(tagNamePrefs);
        dto.setAttEvents(attEvents);
        return dto;
    }

    public User fromDto2Entity(UserCreateDto dto) {
        User user = new User();
        user.setId(dto.getUserId());
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setNickname(dto.getNickname());
        if (dto.getFallaId() != null && !fallaRepository.existsById(dto.getFallaId()))
            throw new EntityNotFoundException("La falla asociada al usuario no existe en la base de datos.");
        user.setFalla(fallaRepository.findFallaById(dto.getFallaId()));

        if (dto.getJoinDate() != null)
            user.setJoinDate(dto.getJoinDate());

        user.setUrlPfp(dto.getUrlPfp());
        user.setBirthday(dto.getBirthday());
        user.setShowBday(dto.getShowBday());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        return user;
    }

    public FallaUserInfoDto fromEntity2UserInfo(Falla falla) {
        FallaUserInfoDto dto = new FallaUserInfoDto();
        dto.setFallaId(falla.getId());
        dto.setCreationDate(falla.getCreationDate());
        dto.setName(falla.getName());
        List<EventInfoUserDto> events = new ArrayList<>();
        List<EventTagInfoDto> tags = new ArrayList<>();
        for (Event e : falla.getEvents()) {
            events.add(eventConversor.fromEntity2InfoUserDto(e));
        }
        for (EventTag t : falla.getEventTags()) {
            tags.add(eventTagConversor.fromEntity2Dto(t));
        }
        dto.setEvents(events); dto.setTags(tags);

        return dto;
    }
}
