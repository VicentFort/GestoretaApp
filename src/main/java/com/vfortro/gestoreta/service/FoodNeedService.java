package com.vfortro.gestoreta.service;

import com.vfortro.gestoreta.conversor.FoodNeedConversor;
import com.vfortro.gestoreta.dto.FoodNeedCreateDto;
import com.vfortro.gestoreta.model.FoodNeed;
import com.vfortro.gestoreta.repository.FoodNeedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FoodNeedService {

    @Autowired
    private FoodNeedRepository foodNeedRepository;

    @Autowired
    private FoodNeedConversor foodNeedConversor;

    @Transactional(readOnly = true)
    public FoodNeedCreateDto readFoodNeed(Long foodNeedId) {
        FoodNeed need = foodNeedRepository.findById(foodNeedId).orElse(null);
        if(need == null) return null;
        return foodNeedConversor.fromEntity2Dto(need);
    }

    @Transactional
    public FoodNeedCreateDto createFoodNeed(FoodNeedCreateDto need) {
        FoodNeed createdNeed = foodNeedRepository.saveAndFlush(foodNeedConversor.fromDto2Entity(need));
        return foodNeedConversor.fromEntity2Dto(createdNeed);
    }
}
