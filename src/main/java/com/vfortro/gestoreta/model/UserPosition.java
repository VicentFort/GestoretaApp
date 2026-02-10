package com.vfortro.gestoreta.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_positions")
public class UserPosition {
    @EmbeddedId
    private UserPositionId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User users;

    @MapsId("positionId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Position positions;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User users1;


}