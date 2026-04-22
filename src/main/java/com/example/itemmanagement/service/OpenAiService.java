package com.example.itemmanagement.service;

import com.example.itemmanagement.dto.RecipeResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    @Value("${OPENAI_API_KEY}")
    private String apiKey;

    private final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RecipeResponse getRecipeSuggestion(List<String> items) {

        String ingredients = String.join("、", items);

        String prompt = String.format(
                "以下の食材を使って、晩ごはんのレシピを1つ提案してください。" +
                        "出力は必ずJSON形式のみで返してください。" +
                        "JSONフォーマット: {\"recipeName\":\"\",\"description\":\"\",\"ingredients\":[],\"steps\":[]}\n" +
                        "食材リスト: %s", ingredients
        );

        try {
            // リクエストヘッダー
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            // リクエストボディ
            Map<String, Object> body = new HashMap<>();
            body.put("model", "gpt-4o-mini");

            body.put("messages", List.of(
                    Map.of("role", "system", "content", "あなたは料理のプロです。必ずJSONのみで返してください。"),
                    Map.of("role", "user", "content", prompt)
            ));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            // API実行
            ResponseEntity<String> response = restTemplate.postForEntity(
                    OPENAI_URL,
                    request,
                    String.class
            );

            // レスポンス解析
            JsonNode root = objectMapper.readTree(response.getBody());
            String content = root.path("choices").get(0)
                    .path("message")
                    .path("content")
                    .asText();

            // ```json 除去
            String cleanedJson = content.replaceAll("```json|```", "").trim();

            return objectMapper.readValue(cleanedJson, RecipeResponse.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
