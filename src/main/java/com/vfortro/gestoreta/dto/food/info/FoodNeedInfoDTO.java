package com.vfortro.gestoreta.dto.food.info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FoodNeedInfoDTO {
    private String foodNeedType;
    private String userName;
    private String userSurname;
    private String eventTitle;
}
