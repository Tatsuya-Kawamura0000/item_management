package com.example.itemmanagement.controller;

import com.example.itemmanagement.dto.ReceiptBulkAddRequest;
import com.example.itemmanagement.dto.ReceiptItemDto;
import com.example.itemmanagement.dto.ReceiptReadResponse;
import com.example.itemmanagement.entity.Categories;
import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.security.LoginUser;
import com.example.itemmanagement.service.AddItemService;
import com.example.itemmanagement.service.CategoryService;
import com.example.itemmanagement.service.OpenAiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/receipts")
@RequiredArgsConstructor
public class ReceiptApiController {
    private final OpenAiService openAiService;
    private final CategoryService categoryService;
    private final AddItemService addItemService;

    @PostMapping("/analyze")
    public ResponseEntity<?> analyze(@RequestParam("image") MultipartFile image) {
        if (image.isEmpty() || image.getContentType() == null || !image.getContentType().startsWith("image/")) {
            return ResponseEntity.badRequest().body(Map.of("message", "レシート画像を選択してください。"));
        }
        try {
            List<Categories> categories = categoryService.getAllCategories();
            ReceiptReadResponse response = openAiService.readReceipt(image.getBytes(), image.getContentType(), categories);
            if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
                return ResponseEntity.unprocessableEntity().body(Map.of("message", "レシートから食材を読み取れませんでした。内容を確認して再撮影してください。"));
            }
            Map<String, Integer> categoryIds = categories.stream()
                    .collect(Collectors.toMap(Categories::getName, Categories::getId, (first, ignored) -> first));
            response.getItems().forEach(item -> normalize(item, categoryIds));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("message", "レシートの読み取りに失敗しました。時間をおいて再度お試しください。"));
        }
    }

    @PostMapping("/bulk-add")
    public ResponseEntity<?> bulkAdd(@Valid @RequestBody ReceiptBulkAddRequest request,
                                     @AuthenticationPrincipal LoginUser loginUser) {
        List<Categories> categories = categoryService.getAllCategories();
        Map<Integer, Categories> categoryById = categories.stream()
                .collect(Collectors.toMap(Categories::getId, Function.identity()));
        for (var item : request.getItems()) {
            if (item.getName() == null || item.getName().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("message", "食材名を入力してください。"));
            }
            if (item.getCategoryId() == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "カテゴリーを入力してください。"));
            }
            if (!categoryById.containsKey(item.getCategoryId())) {
                return ResponseEntity.badRequest().body(Map.of("message", "選択されたカテゴリーは利用できません。"));
            }
        }
        List<Items> saved = addItemService.addAll(request.getItems(), loginUser.getId());
        return ResponseEntity.ok(Map.of("count", saved.size()));
    }

    private void normalize(ReceiptItemDto item, Map<String, Integer> categoryIds) {
        if (item.getQuantity() == null || item.getQuantity() < 1) item.setQuantity(1);
        if (item.getUnit() == null || item.getUnit().isBlank()) item.setUnit("-");
        item.setCategoryId(categoryIds.get(item.getCategory() == null ? "" : item.getCategory().trim()));
        // expirationDate: AIが付与した日付を尊重する。無効な形式や欠損の場合は null にして、
        // フロントエンド側で表示（例: 今日を入力例として表示）・ユーザーによる修正を促す。
        if (item.getExpirationDate() == null || item.getExpirationDate().isBlank()) {
            item.setExpirationDate(null);
            return;
        }
        try {
            LocalDate.parse(item.getExpirationDate());
        } catch (Exception e) {
            item.setExpirationDate(null);
        }
    }
}
