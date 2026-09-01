package com.example.itemmanagement.service;

import com.example.itemmanagement.entity.Recipe;
import com.example.itemmanagement.entity.RecipeIngredient;
import com.example.itemmanagement.entity.RecipeStep;
import com.example.itemmanagement.form.RecipeCreateForm;
import com.example.itemmanagement.mapper.RecipeCategoryMapper;
import com.example.itemmanagement.mapper.RecipeIngredientMapper;
import com.example.itemmanagement.mapper.RecipeMapper;
import com.example.itemmanagement.mapper.RecipeStepMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeMapper recipeMapper;

    @Mock
    private RecipeCategoryMapper recipeCategoryMapper;

    @Mock
    private RecipeIngredientMapper recipeIngredientMapper;

    @Mock
    private RecipeStepMapper recipeStepMapper;

    @InjectMocks
    private RecipeService sut;

    private Integer userId;

    @BeforeEach
    void setUp() {
        userId = 1;
    }

    @Test
    void createRecipe_正常にEntityと材料および手順がMapperへ渡されて保存されること() {
        // Arrange
        RecipeCreateForm form = new RecipeCreateForm();
        form.setRecipeName("肉じゃが");
        form.setServings(2);
        form.setCategoryId(2);
        form.setMemo("甘めの味付けがコツ");
        form.setSourceType("USER");
        form.setSourceUrl("https://example.com");

        List<RecipeCreateForm.IngredientForm> ingredients = new ArrayList<>();
        ingredients.add(new RecipeCreateForm.IngredientForm("牛肉", new BigDecimal("200.00"), "g", true, 1));
        ingredients.add(new RecipeCreateForm.IngredientForm("じゃがいも", new BigDecimal("2"), "個", true, 2));
        form.setIngredients(ingredients);

        List<RecipeCreateForm.StepForm> steps = new ArrayList<>();
        steps.add(new RecipeCreateForm.StepForm("材料を切る"));
        steps.add(new RecipeCreateForm.StepForm("牛肉を炒めて煮る"));
        form.setSteps(steps);

        doAnswer(invocation -> {
            Recipe r = invocation.getArgument(0);
            r.setId(100);
            return null;
        }).when(recipeMapper).insertRecipe(any(Recipe.class));

        // Act
        Integer createdId = sut.createRecipe(userId, form);

        // Assert
        assertEquals(100, createdId);

        // Recipe本体の検証
        ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);
        verify(recipeMapper, times(1)).insertRecipe(recipeCaptor.capture());
        Recipe capturedRecipe = recipeCaptor.getValue();
        assertEquals(userId, capturedRecipe.getUserId());
        assertEquals("肉じゃが", capturedRecipe.getRecipeName());
        assertEquals(2, capturedRecipe.getServings());
        assertEquals(2, capturedRecipe.getCategoryId());
        assertEquals("甘めの味付けがコツ", capturedRecipe.getMemo());
        assertEquals("USER", capturedRecipe.getSourceType());
        assertEquals("https://example.com", capturedRecipe.getSourceUrl());
        assertFalse(capturedRecipe.getIsFavorite());

        // 材料の検証
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<RecipeIngredient>> ingredientCaptor = ArgumentCaptor.forClass(List.class);
        verify(recipeIngredientMapper, times(1)).batchInsert(ingredientCaptor.capture());
        List<RecipeIngredient> capturedIngredients = ingredientCaptor.getValue();
        assertEquals(2, capturedIngredients.size());
        assertEquals("牛肉", capturedIngredients.get(0).getIngredientName());
        assertEquals(new BigDecimal("200.00"), capturedIngredients.get(0).getQuantity());
        assertEquals("g", capturedIngredients.get(0).getUnit());
        assertTrue(capturedIngredients.get(0).getIsMain());
        assertEquals(1, capturedIngredients.get(0).getDisplayOrder());
        assertEquals(100, capturedIngredients.get(0).getRecipeId());

        assertEquals("じゃがいも", capturedIngredients.get(1).getIngredientName());
        assertEquals(2, capturedIngredients.get(1).getDisplayOrder());

        // 手順の検証
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<RecipeStep>> stepCaptor = ArgumentCaptor.forClass(List.class);
        verify(recipeStepMapper, times(1)).batchInsert(stepCaptor.capture());
        List<RecipeStep> capturedSteps = stepCaptor.getValue();
        assertEquals(2, capturedSteps.size());
        assertEquals(1, capturedSteps.get(0).getStepNo());
        assertEquals("材料を切る", capturedSteps.get(0).getInstruction());
        assertEquals(100, capturedSteps.get(0).getRecipeId());
        assertEquals(2, capturedSteps.get(1).getStepNo());
        assertEquals("牛肉を炒めて煮る", capturedSteps.get(1).getInstruction());
    }

    @Test
    void createRecipe_材料や手順が空の場合はbatchInsertが呼ばれないこと() {
        // Arrange
        RecipeCreateForm form = new RecipeCreateForm();
        form.setRecipeName("シンプル料理");
        form.setServings(1);

        doAnswer(invocation -> {
            Recipe r = invocation.getArgument(0);
            r.setId(101);
            return null;
        }).when(recipeMapper).insertRecipe(any(Recipe.class));

        // Act
        Integer createdId = sut.createRecipe(userId, form);

        // Assert
        assertEquals(101, createdId);
        verify(recipeMapper, times(1)).insertRecipe(any(Recipe.class));
        verify(recipeIngredientMapper, never()).batchInsert(any());
        verify(recipeStepMapper, never()).batchInsert(any());
    }

    @Test
    void getRecipeDetail_材料と手順を含んだRecipeが取得できること() {
        // Arrange
        Integer recipeId = 50;
        Recipe recipe = new Recipe();
        recipe.setId(recipeId);
        recipe.setRecipeName("カレー");

        when(recipeMapper.findById(recipeId)).thenReturn(recipe);

        List<RecipeIngredient> ingredients = List.of(new RecipeIngredient(1, recipeId, "豚肉", new BigDecimal("150"), "g", true, 1, 1));
        List<RecipeStep> steps = List.of(new RecipeStep(1, recipeId, 1, "炒める"));
        when(recipeIngredientMapper.selectByRecipeId(recipeId)).thenReturn(ingredients);
        when(recipeStepMapper.selectByRecipeId(recipeId)).thenReturn(steps);

        // Act
        Recipe actual = sut.getRecipeDetail(recipeId);

        // Assert
        assertNotNull(actual);
        assertEquals("カレー", actual.getRecipeName());
        assertEquals(1, actual.getIngredients().size());
        assertEquals(1, actual.getSteps().size());
    }

    @Test
    void getAllRecipesWithDetails_全レシピに材料と手順が紐づいて返ること() {
        // Arrange
        Recipe recipe1 = new Recipe();
        recipe1.setId(10);
        Recipe recipe2 = new Recipe();
        recipe2.setId(20);

        when(recipeMapper.selectByUserId(userId)).thenReturn(List.of(recipe1, recipe2));
        when(recipeIngredientMapper.selectByRecipeId(10)).thenReturn(List.of(new RecipeIngredient()));
        when(recipeStepMapper.selectByRecipeId(10)).thenReturn(List.of(new RecipeStep()));
        when(recipeIngredientMapper.selectByRecipeId(20)).thenReturn(List.of());
        when(recipeStepMapper.selectByRecipeId(20)).thenReturn(List.of());

        // Act
        List<Recipe> actual = sut.getAllRecipesWithDetails(userId);

        // Assert
        assertEquals(2, actual.size());
        assertEquals(1, actual.get(0).getIngredients().size());
        assertEquals(1, actual.get(0).getSteps().size());
        assertEquals(0, actual.get(1).getIngredients().size());
    }

    @Test
    void updateRecipe_正常に既存材料手順が削除され再登録されること() {
        // Arrange
        Integer recipeId = 15;
        Recipe existing = new Recipe();
        existing.setId(recipeId);
        existing.setUserId(userId);

        when(recipeMapper.findById(recipeId)).thenReturn(existing);

        RecipeCreateForm form = new RecipeCreateForm();
        form.setRecipeName("更新後レシピ");
        form.setServings(4);
        form.setCategoryId(1);
        form.setIngredients(List.of(new RecipeCreateForm.IngredientForm("豚肉", new BigDecimal("300"), "g", true, 1)));
        form.setSteps(List.of(new RecipeCreateForm.StepForm("炒める")));

        // Act
        sut.updateRecipe(userId, recipeId, form);

        // Assert
        verify(recipeMapper, times(1)).updateRecipe(any(Recipe.class));
        verify(recipeIngredientMapper, times(1)).deleteByRecipeId(recipeId);
        verify(recipeIngredientMapper, times(1)).batchInsert(anyList());
        verify(recipeStepMapper, times(1)).deleteByRecipeId(recipeId);
        verify(recipeStepMapper, times(1)).batchInsert(anyList());
    }

    @Test
    void deleteRecipe_材料手順本体が削除されること() {
        // Arrange
        Integer recipeId = 15;
        Recipe existing = new Recipe();
        existing.setId(recipeId);
        existing.setUserId(userId);

        when(recipeMapper.findById(recipeId)).thenReturn(existing);

        // Act
        sut.deleteRecipe(userId, recipeId);

        // Assert
        verify(recipeIngredientMapper, times(1)).deleteByRecipeId(recipeId);
        verify(recipeStepMapper, times(1)).deleteByRecipeId(recipeId);
        verify(recipeMapper, times(1)).deleteByIdAndUserId(recipeId, userId);
    }
}
