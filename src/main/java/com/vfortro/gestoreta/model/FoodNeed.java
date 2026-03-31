package com.vfortro.gestoreta.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "food_needs", uniqueConstraints = {
        @UniqueConstraint(name = "food_needs_users_unique", columnNames = {"user_id", "description"})
})
public class FoodNeed implements Serializable {
    private static final long serialVersionUID = 5378625060144798745L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "need_id", nullable = false)
    private Long id;

    @NotNull
    //@Convert(converter = FoodNeedTypeConverter.class)
    @Column(name = "description", nullable = false, length = Integer.MAX_VALUE, columnDefinition = "food_need_type")
    private FoodNeedType description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


}