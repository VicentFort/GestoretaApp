package com.vfortro.gestoreta.conversor;

import com.vfortro.gestoreta.dto.users.UserCreateDto;
import com.vfortro.gestoreta.dto.users.UserInfoDto;
import com.vfortro.gestoreta.model.Assist;
import com.vfortro.gestoreta.model.Position;
import com.vfortro.gestoreta.model.User;
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

    public UserCreateDto fromEntity2Dto(User user) {
        UserCreateDto dto = new UserCreateDto();
        dto.setUserId(user.getId());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        if(Objects.nonNull(user.getFalla())) dto.setFallaId(user.getFalla().getId());
        dto.setUrlPfp(user.getUrlPfp());
        dto.setBirthday(user.getBirthday());
        dto.setShowBday(user.getShowBday());
        return dto;
    }

    public UserInfoDto fromEntity2InfoDto(User user) {
        UserInfoDto dto = new UserInfoDto();
        dto.setName(user.getName()); dto.setSurname(user.getSurname());
        dto.setBirthday(user.getBirthday()); dto.setShowBday(user.getShowBday());
        dto.setAdminAccess(user.getPositions().stream().anyMatch(Position::getAdminAccess));
        dto.setAssistEventIds(new ArrayList<>());
        dto.setAssistEventTitles(new ArrayList<>());
        for(Assist a : user.getAssists()) {
            dto.getAssistEventIds().add(a.getEvent().getId());
            dto.getAssistEventTitles().add(a.getEvent().getTitle());
        }
        return dto;
    }

    public User fromDto2Entity(UserCreateDto dto) {
        User user = new User();
        user.setId(dto.getUserId());
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());

        if(dto.getFallaId() != null && !fallaRepository.existsById(dto.getFallaId()))
            throw new EntityNotFoundException("La falla asociada al usuario no existe en la base de datos.");
        user.setFalla(fallaRepository.findFallaById(dto.getFallaId()));

        user.setUrlPfp(dto.getUrlPfp());
        user.setBirthday(dto.getBirthday());
        user.setShowBday(dto.getShowBday());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        return user;
    }
}
