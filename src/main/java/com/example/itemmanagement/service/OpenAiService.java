package com.example.itemmanagement.service;

import com.example.itemmanagement.dto.RecipeResponse;
import com.example.itemmanagement.dto.ReceiptReadResponse;
import com.example.itemmanagement.entity.Categories;
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
import java.util.Base64;
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
    public RecipeResponse getRecipeSuggestion(List<Items> items, String genreParam, boolean prioritizeExpiring, boolean lowCalorie, boolean easyMode, boolean isSelectionMode) {


        // 1. 最新の期限状態をセット
        itemDeadlineService.applyDeadlineMessage(items);

        // 2. 食材一覧リストを作成
        String ingredients = items.stream().map(Items::getName).collect(Collectors.joining("、"));

        // 3. 追加条件の組み立て
        StringBuilder options = new StringBuilder();


        // --- ここがポイント：モードに応じたメイン指示の追加 ---
        if (isSelectionMode) {
            options.append("- 【重要】ユーザーが選択した食材です。メインで使用してください。\n");
        }

        // 期限間近食材を抽出して,使用するように指示
        if (prioritizeExpiring) {
            List<String> urgentItems = items.stream().filter(Items::isExpiringSoon) // trueに修正したフラグを使用
                    .map(Items::getName).toList();

            if (!urgentItems.isEmpty()) {
                options.append(String.format("- 期限が近いこの食材を優先的に使用希望: [%s]\n", String.join("、", urgentItems)));
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
                """, genreParam, !options.isEmpty() ? options.toString() : "- 特になし", ingredients);

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
            body.put("messages", List.of(Map.of("role", "system", "content", "あなたは料理のプロです。必ずJSONのみで返してください。"), Map.of("role", "user", "content", prompt)));

            //ヘッダーとボディはセットで送信する必要がある
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            // restTemplateを使用してAPI実行
            ResponseEntity<String> response = restTemplate.postForEntity(OPENAI_URL, request,
                    //レスポンスタイプに文字列を指定　→　OpenAIから届いたJSONを、加工せずにテキストとしてまるごと Stringに入れてくれる
                    String.class);

            // レスポンス解析
            JsonNode root = objectMapper.readTree(response.getBody());
            String content = root.path("choices").get(0).path("message").path("content").asText();

            // ```json 除去
            String cleanedJson = content.replaceAll("```json|```", "").trim();

            return objectMapper.readValue(cleanedJson, RecipeResponse.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** レシート画像を、登録前に編集しやすいJSONへ変換する。 */
    public ReceiptReadResponse readReceipt(byte[] image, String mediaType, List<Categories> categories) throws Exception {
        String categoryNames = categories.stream().map(Categories::getName).collect(Collectors.joining("、"));
        String prompt = String.format("""
                添付した日本の購入レシートから、食品・飲料だけを抽出してください。必ずJSONのみで返してください。
                今日の日付は %s です。
                注意: レシートに賞味期限・消費期限が記載されていることは前提としません。多くの場合、レシートには期限情報がありません。
                - 各食材について、食材名をもとに一般的な保存期間や賞味・消費期限の傾向を考慮して、購入日を基準に妥当な expirationDate を推測してください。
                - レシート上から購入日が明確に読み取れる場合は、その購入日を基準日に使って期限を推定してください。
                - 購入日が読み取れない場合のみ、現時点（今日）を基準日に使って推測してください。
                - 可能な限り具体的な日付（yyyy-MM-dd）で返してください。どうしても推測できない場合は空文字にせず、食材名から合理的に推定した日付を返してください（最善の推測を行ってください）。
                quantity が不明なら1、unit が不明なら「-」にしてください。
                category は次の候補の完全一致だけを使い、判断できない場合は空文字にしてください: %s
                出力形式: {"items":[{"name":"","quantity":1,"unit":"-","category":"","expirationDate":"yyyy-MM-dd"}]}
                """, java.time.LocalDate.now(), categoryNames);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        Map<String, Object> imageUrl = Map.of("url", "data:" + mediaType + ";base64," + Base64.getEncoder().encodeToString(image));
        Map<String, Object> message = Map.of("role", "user", "content", List.of(
                Map.of("type", "text", "text", prompt),
                Map.of("type", "image_url", "image_url", imageUrl)));
        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4o-mini");
        body.put("response_format", Map.of("type", "json_object"));
        body.put("messages", List.of(Map.of("role", "system", "content", "You extract receipt items and return valid JSON only."), message));
        ResponseEntity<String> response = restTemplate.postForEntity(OPENAI_URL,
                new HttpEntity<>(body, headers), String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        String content = root.path("choices").path(0).path("message").path("content").asText();
        if (content.isBlank()) throw new IllegalStateException("OpenAI response is empty");
        return objectMapper.readValue(content.replaceAll("```json|```", "").trim(), ReceiptReadResponse.class);
    }
}
