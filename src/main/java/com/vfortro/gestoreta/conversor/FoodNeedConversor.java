package com.vfortro.gestoreta.conversor;

import com.vfortro.gestoreta.dto.FoodNeedCreateDto;
import com.vfortro.gestoreta.model.FoodNeed;
import com.vfortro.gestoreta.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FoodNeedConversor {

    @Autowired
    private UserRepository userRepository;

    public FoodNeedCreateDto fromEntity2Dto(FoodNeed need) {
        FoodNeedCreateDto dto = new FoodNeedCreateDto();
        dto.setFoodNeedId(need.getId());
        dto.setDescription(need.getDescription());
        dto.setUserId(need.getUser().getId());
        return dto;
    }

    public FoodNeed fromDto2Entity(FoodNeedCreateDto dto) {
        FoodNeed need = new FoodNeed();
        need.setDescription(dto.getDescription());

        if(dto.getUserId() == null) throw new NullPointerException("La necesidad debe tener una id de usuario.");
        if(!userRepository.existsById(dto.getUserId())) throw new EntityNotFoundException("El usuario de la necesidad no existe en la base de datos.");
        need.setUser(userRepository.findUserById(dto.getUserId()));

        return need;
    }
}
