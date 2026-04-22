package com.example.itemmanagement.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;


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

    // JSON変換用の ObjectMapper (staticにすることでメモリ効率を上げます)
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 材料をListとして取得する (Thymeleafから ${recipe.ingredientsList} で呼べる)
     */
    public List<String> getIngredientsList() {
        if (this.ingredientsJson == null || this.ingredientsJson.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(this.ingredientsJson, new TypeReference<List<String>>(){});
        } catch (Exception e) {
            // 解析失敗時はログ出力して空リストを返す
            return Collections.emptyList();
        }
    }

    /**
     * 手順をListとして取得する (Thymeleafから ${recipe.stepsList} で呼べる)
     */
    public List<String> getStepsList() {
        if (this.stepsJson == null || this.stepsJson.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(this.stepsJson, new TypeReference<List<String>>(){});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
