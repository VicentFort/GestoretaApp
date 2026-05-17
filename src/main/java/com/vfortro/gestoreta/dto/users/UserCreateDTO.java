package com.vfortro.gestoreta.dto.users;

import com.vfortro.gestoreta.model.enums.AccessType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserCreateDTO {
    private Long userId;
    private @NotNull String name;
    private @NotNull String surname;
    private String nickname;
    private Long fallaId;
    private LocalDate joinDate;
    private String urlPfp;
    private @NotNull LocalDate birthday;
    private @NotNull Boolean showBday;
    private @NotNull String email;
    private @NotNull String password;
    private AccessType accessType;

}
