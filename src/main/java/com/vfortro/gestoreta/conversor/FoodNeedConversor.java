package com.vfortro.gestoreta.conversor;

import com.vfortro.gestoreta.dto.food.FoodNeedCreateDTO;
import com.vfortro.gestoreta.model.FoodNeed;
import com.vfortro.gestoreta.model.enums.FoodNeedType;
import com.vfortro.gestoreta.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FoodNeedConversor {

    @Autowired
    private UserRepository userRepository;

    public FoodNeedCreateDTO fromEntity2Dto(FoodNeed need) {
        FoodNeedCreateDTO dto = new FoodNeedCreateDTO();
        dto.setFoodNeedId(need.getId());
        dto.setDescription(need.getDescription().getValue());
        dto.setUserId(need.getUser().getId());
        return dto;
    }

    public FoodNeed fromDto2Entity(FoodNeedCreateDTO dto) {
        FoodNeed need = new FoodNeed();
        need.setDescription(FoodNeedType.valueOf(dto.getDescription()));

        if(dto.getUserId() == null) throw new NullPointerException("La necesidad debe tener una id de usuario.");
        if(!userRepository.existsById(dto.getUserId())) throw new EntityNotFoundException("El usuario de la necesidad no existe en la base de datos.");
        need.setUser(userRepository.findUserById(dto.getUserId()));

        return need;
    }
}
