package com.example.itemmanagement.service;

import com.example.itemmanagement.dto.RecipeResponse;
import com.example.itemmanagement.entity.Items;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    @Value("${OPENAI_API_KEY}")
    private String apiKey;

    private final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ItemDeadlineService itemDeadlineService;



    //items:食材一覧(name)、genreParam:ジャンル、prioritizeExpiring:期限間近食材優、lowCalorie:低カロリー、asyMode:手軽
    public RecipeResponse getRecipeSuggestion(List<Items> items, String genreParam,
                                              boolean prioritizeExpiring, boolean lowCalorie, boolean easyMode,boolean isSelectionMode) {


        // 1. 最新の期限状態をセット
        itemDeadlineService.applyDeadlineMessage(items);

        // 2. 食材一覧リストを作成
        String ingredients = items.stream()
                .map(Items::getName)
                .collect(Collectors.joining("、"));

        // 3. 追加条件の組み立て
        StringBuilder options = new StringBuilder();


        // --- ここがポイント：モードに応じたメイン指示の追加 ---
        if (isSelectionMode) {
            options.append("- 【重要】ユーザーが選択した食材です。メインで使用してください。\n");
        }

        // 期限間近食材を抽出して,使用するように指示
        if (prioritizeExpiring) {
            List<String> urgentItems = items.stream()
                    .filter(Items::isExpiringSoon) // trueに修正したフラグを使用
                    .map(Items::getName)
                    .toList();

            if (!urgentItems.isEmpty()) {
                options.append(String.format("- 期限が近いこの食材を優先的に使用希望: [%s]\n",
                        String.join("、", urgentItems)));
            }
        }

        if (lowCalorie) options.append("- 低カロリー\n");
        if (easyMode) options.append("- 15分以内で作れる、簡単な工程\n");



        // 2. プロンプトの構築（テキストブロックで見やすく）
        String prompt = String.format("""
            以下の【食材リスト】から、レシピを1つ提案してください。ジャンルは「%s」です。
            
            【追加条件】
            %s
            
            【出力ルール】
            - 必ずJSON形式のみで返却すること。余計な解説文は一切不要。
            - フォーマット: {"recipeName":"","description":"","ingredients":[],"steps":[]}
            
            【食材リスト】
            %s
            """,
                genreParam,
                !options.isEmpty() ? options.toString() : "- 特になし",
                ingredients
        );

        try {
            // リクエストヘッダー
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            // リクエストボディ
            Map<String, Object> body = new HashMap<>();
            body.put("model", "gpt-4o-mini");

            //messages:AIへの指示と依頼内容
            //role(system):AIへの設定(背景)指示、role(user):userのcontent(今回の指示)をAIが読み取り、結果を返す　ここはOpenAI APIの仕様。
            body.put("messages", List.of(
                    Map.of("role", "system", "content", "あなたは料理のプロです。必ずJSONのみで返してください。"),
                    Map.of("role", "user", "content", prompt)
            ));

            //ヘッダーとボディはセットで送信する必要がある
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            // restTemplateを使用してAPI実行
            ResponseEntity<String> response = restTemplate.postForEntity(
                    OPENAI_URL,
                    request,
                    //レスポンスタイプに文字列を指定　→　OpenAIから届いたJSONを、加工せずにテキストとしてまるごと Stringに入れてくれる
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
