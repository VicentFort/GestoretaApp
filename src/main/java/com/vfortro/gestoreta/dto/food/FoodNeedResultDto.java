package com.vfortro.gestoreta.dto.food;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FoodNeedResultDto {
    private String foodNeedDesc;
    private String userName;
    private String userSurname;
    private String eventTitle;
}
