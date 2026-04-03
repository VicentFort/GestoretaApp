package com.vfortro.gestoreta.service;

import com.vfortro.gestoreta.conversor.FoodNeedConversor;
import com.vfortro.gestoreta.conversor.UserConversor;
import com.vfortro.gestoreta.dto.food.FoodNeedCreateDto;
import com.vfortro.gestoreta.dto.users.UserInfoDto;
import com.vfortro.gestoreta.model.FoodNeed;
import com.vfortro.gestoreta.model.enums.FoodNeedType;
import com.vfortro.gestoreta.model.User;
import com.vfortro.gestoreta.repository.FoodNeedRepository;
import com.vfortro.gestoreta.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.Objects;

@Service
public class FoodNeedService {

    @Autowired
    private FoodNeedRepository foodNeedRepository;

    @Autowired
    private FoodNeedConversor foodNeedConversor;

    @Autowired
    private UserConversor userConversor;
    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public FoodNeedCreateDto readFoodNeed(Long foodNeedId) {
        FoodNeed need = foodNeedRepository.findById(foodNeedId).orElse(null);
        if(need == null) return null;
        return foodNeedConversor.fromEntity2Dto(need);
    }

    @Transactional
    public UserInfoDto createFoodNeed(String desc, String email) throws AccessDeniedException {
        User user = userRepository.findUserByEmail(email);
        FoodNeed need = new FoodNeed();
        need.setDescription(FoodNeedType.fromString(desc));
        need.setUser(user);
        foodNeedRepository.saveAndFlush(need);
        return userConversor.fromEntity2InfoDto(userRepository.findUserByEmail(email));
    }

    public UserInfoDto deleteFoodNeed(Long needId, String email) throws AccessDeniedException {
        User user = userRepository.findUserByEmail(email);
        FoodNeed need = foodNeedRepository.findFoodNeedById(needId);
        if(!Objects.equals(user.getId(), need.getUser().getId())) throw new AccessDeniedException("Sin permiso.");
        foodNeedRepository.deleteById(need.getId());
        return userConversor.fromEntity2InfoDto(userRepository.findUserByEmail(email));
    }
}
