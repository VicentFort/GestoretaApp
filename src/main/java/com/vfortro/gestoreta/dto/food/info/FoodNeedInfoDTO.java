package com.vfortro.gestoreta.dto.food.info;

import com.vfortro.gestoreta.model.enums.FoodNeedType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FoodNeedInfoDTO {
    private FoodNeedType foodNeedType;
    private String userName;
    private String userSurname;
    private String eventTitle;
}
