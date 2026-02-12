package com.vfortro.gestoreta.conversor;

import com.vfortro.gestoreta.dto.UserCreateDto;
import com.vfortro.gestoreta.model.User;
import com.vfortro.gestoreta.repository.FallaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return user;
    }
}
