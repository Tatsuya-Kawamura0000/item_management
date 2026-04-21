package com.example.itemmanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {

    private Integer id;

    private Integer userId;

    private String recipeName;

    private String description;

    private String ingredientsJson;

    private String stepsJson;

    private String sourceItemsJson;

    private Boolean favorite;

    private Integer status;

    private LocalDateTime createdAt;
}
