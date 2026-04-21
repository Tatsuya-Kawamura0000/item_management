/*package com.example.itemmanagement.service;

import com.example.itemmanagement.dto.RecipeResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${GEMINI_API_KEY}")
    private String apiKey;

    //private final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";
    //private final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=";

    //private final String GEMINI_URL ="https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=";

    //private final String GEMINI_URL ="https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=";

    //private final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-2.0-flash:generateContent?key=";

    //private final String GEMINI_URL ="https://generativelanguage.googleapis.com/v1/models/gemini-2.0-flash-lite:generateContent?key=";


    private final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=";


    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    //アイテム情報(items)を渡して、Geminiにレシピを提案してもらうメソッド
    public RecipeResponse getRecipeSuggestion(List<String> items) {

        String url = GEMINI_URL + apiKey;

        // 1. プロンプト（AIへの指示文）を作成
        String ingredients = String.join("、", items);
        String prompt = String.format(

                "以下の食材を使って、晩ごはんのレシピを1つ提案してください。出力は必ず以下のJSON形式のみで返してください。\n" +
                        "JSONフォーマット: {\"recipeName\":\"\",\"description\":\"\",\"ingredients\":[],\"steps\":[]}\n" +
                        "食材リスト: %s", ingredients

        );

        // 2. Gemini APIが受け取れるリクエスト形式（JSON）を組み立て
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(
                Map.of("parts", List.of(
                        Map.of("text", prompt)  //ここに上記のプロンプトを配置
                ))
        ));

        try {
            // 3. APIに送信
            String responseStr = restTemplate.postForObject(url, requestBody, String.class);

            // 4. パース（AIの返答からJSON部分を抜き出してJavaオブジェクトに変換）
            JsonNode root = objectMapper.readTree(responseStr);
            String aiResponseText = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();                         //text の部分を取得

            // AIが ```json ... ``` のように返してきた場合の掃除
            String cleanedJson = aiResponseText.replaceAll("```json|```", "").trim();

            return objectMapper.readValue(cleanedJson, RecipeResponse.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null; // 本来はエラーハンドリングをしっかり行いますが、まずはシンプルに
        }
    }
}
*/