package com.example.itemmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//JSON形式のAIレスポンスを、ObjectMapperを使用して、ここにマッピング
@Data
@NoArgsConstructor  // 引数なしのコンストラクタを自動生成（JSON変換に必要）
@AllArgsConstructor // 全引数ありのコンストラクタを自動生成
public class RecipeResponse {
    private String recipeName;
    private String description;
    private List<String> ingredients;
    private List<String> steps;
}
