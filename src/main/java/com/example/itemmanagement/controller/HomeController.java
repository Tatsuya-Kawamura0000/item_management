package com.example.itemmanagement.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.itemmanagement.entity.Categories;
import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.entity.ShoppingListItem;
import com.example.itemmanagement.form.AddItemForm;
import com.example.itemmanagement.mapper.ShoppingListMapper;
import com.example.itemmanagement.service.AddItemService;
import com.example.itemmanagement.service.AddToShoppingListService;
import com.example.itemmanagement.service.GetAllCategoriesService;
import com.example.itemmanagement.service.GetAllItemsService;
import com.example.itemmanagement.service.GetFilterItemsService;
import com.example.itemmanagement.service.StopItemService;
import com.example.itemmanagement.service.UpdateItemService;

@Controller
@RequestMapping("/users")
public class HomeController {

	@Autowired
	private GetAllItemsService getAllItemsService;
	
	@Autowired
	private GetAllCategoriesService getAllCategoriesService;
	
	@Autowired
	private  AddItemService addItemService;
	
	@Autowired
	private  StopItemService stopItemService;
	
	@Autowired
	private  UpdateItemService updateItemService;
	
	@Autowired
	private ShoppingListMapper shoppingListMapper;
	
	@Autowired
	private  AddToShoppingListService addToShoppingListService;

	@Autowired
	private GetFilterItemsService getFilterItemsService;
	
	


	
	@GetMapping
	public String index(Model model) {

	    List<Items> items = getAllItemsService.getAllItems();

	    for (Items item : items) {
	        if (item.getDeadline() != null) {
	            long days = java.time.temporal.ChronoUnit.DAYS.between(
	                    java.time.LocalDate.now(), item.getDeadline());

	            if (days < 0) {
	                item.setMessage("期限切れです、、");
	            } else if (days <= 3) {
	                item.setMessage("気を付けて！");
	            } else {
	                item.setMessage("");
	            }
	        } else {
	            item.setMessage("");
	        }
	    }
	    
	    // ✅ カテゴリー一覧も追加
	    List<Categories> categories = getAllCategoriesService.getAllCategories();
	    model.addAttribute("categories", categories);

	    model.addAttribute("items", items);

	    return "home";
	}

	@GetMapping("/add")									//食材登録画面をリクエストされた時
	public String add(Model model) {

		List<Categories> categories = getAllCategoriesService.getAllCategories();
		
		model.addAttribute("categories", categories);

		model.addAttribute("form", new AddItemForm());

		return "add";									

	}

	
	@PostMapping											
	public String create(@Validated @ModelAttribute("form") AddItemForm form, BindingResult result, Model model,
			 RedirectAttributes redirectAttributes) {

		List<Categories> categories = getAllCategoriesService.getAllCategories();


			if (result.hasErrors()) {							//バリデーションでエラーを捕まえたとき

				model.addAttribute("categoryId", categories);
				
				model.addAttribute("form", form);

				return "add";							//ユーザ登録画面を返す

			}

		
		addItemService.add(form);					// 食材追加処理実行
		
		// ✅ 一時的なメッセージを追加
	    redirectAttributes.addFlashAttribute("successMessage", "食材を登録しました！");

		return "redirect:/users";

		
	}
	
	@PostMapping("/stop/{id}")										//使い切ったボタンを押した食品のIDを受け取る
	public String stop(@PathVariable("id") int id, Model model,RedirectAttributes redirectAttributes) {
 		
		
	    // 🍀 IDで1件取得（お気に入りフラグ確認のため）
	    Items item = getAllItemsService.getItemById(id);
	    
	    // ✅ お気に入り登録されているかチェック
	    if (item.isFavorite()) {
	        // ダイアログを出すためのフラグをセット
	        redirectAttributes.addFlashAttribute("confirmAddToList", true);
	        redirectAttributes.addFlashAttribute("targetItemId", id);
	    }
	    
	    stopItemService.stopItem(id);								//食品の論理削除（）メソッド呼び出し。status=1→0 に。
		
		redirectAttributes.addFlashAttribute("successMessage", "食材を使い切りました！");
		
		 return "redirect:/users"; 
		
	}
	
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable("id") int id, Model model) {

		// IDで食材情報を1件取得
		Items item = getAllItemsService.getItemById(id);

		// カテゴリー一覧を取得
		List<Categories> categories = getAllCategoriesService.getAllCategories();

		model.addAttribute("item", item);
		model.addAttribute("categories", categories);

		return "edit";
	}


	@PostMapping("/update/{id}")
	public String update(@PathVariable("id") int id, @ModelAttribute Items item) {

		updateItemService.updateItem(id, item);

		return "redirect:/users";
	}
	
	@GetMapping("/shoppingList")									//買い物リスト画面をリクエストされた時
	public String shoppingList(Model model, HttpServletRequest request) {
	
	    // CSRFトークンを取得してモデルに追加
	    CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
	    model.addAttribute("_csrf", csrfToken);
		
		List<ShoppingListItem> listItems = shoppingListMapper.findAll();  // ← まず Mapper を作る
	    model.addAttribute("listItems", listItems);
	    
	 // ← ここでカテゴリ一覧も追加
	    List<Categories> categories = getAllCategoriesService.getAllCategories();
	    model.addAttribute("categories", categories);

		return "shoppingList";									

	}
	
	@PostMapping("/favorite/{id}")
	public String toggleFavorite(@PathVariable("id") int id,@RequestParam(required = false) Integer category,
	        @RequestParam(required = false) Boolean expiringSoon) {

	    // IDでアイテム取得
	    Items item = getAllItemsService.getItemById(id);

	    // favoriteを反転
	    item.setFavorite(!item.isFavorite());

	    // 更新
	    updateItemService.updateFavorite(item);
	    
	    // ✅ フィルター条件がある場合は、その条件付きでリダイレクト
	    if (category != null || expiringSoon != null) {
	        StringBuilder url = new StringBuilder("redirect:/users/filter?");
	        if (category != null) url.append("category=").append(category).append("&");
	        if (expiringSoon != null && expiringSoon) url.append("expiringSoon=true");
	        return url.toString();
	    }

	    // 一覧に戻る
	    return "redirect:/users";
	}
	
	@PostMapping("/add-to-shopping-list/{id}")   // ✅ JS fetchから呼ばれるPOSTエンドポイント
	public String addToShoppingList(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {

	    // 🍀 サービス呼び出しでShoppingListに追加
	    addToShoppingListService.addItemToList(id);

	    // ✅ フラッシュメッセージを追加（画面に出す場合）
	    redirectAttributes.addFlashAttribute("successMessage", "買い物リストに追加しました！");

	    // 🍀 JS側ではページ遷移しないので空文字でも問題なし
	    return "redirect:/users";  
	}

	@GetMapping("/filter")
	public String filterItems(
	        @RequestParam(required = false) Integer category,
	        @RequestParam(required = false) Boolean expiringSoon,
	        Model model) {

	    List<Items> filteredItems = getFilterItemsService.filterItems(category, expiringSoon);
	    
	    // ✅ メッセージ生成を追加
	    for (Items item : filteredItems) {
	        if (item.getDeadline() != null) {
	            long days = java.time.temporal.ChronoUnit.DAYS.between(
	                    java.time.LocalDate.now(), item.getDeadline());
	            if (days < 0) {
	                item.setMessage("期限切れです、、");
	            } else if (days <= 3) {
	                item.setMessage("気を付けて！");
	            } else {
	                item.setMessage("");
	            }
	        } else {
	            item.setMessage("");
	        }
	    }

	    // カテゴリー一覧（今後拡張しやすいようにDBやEnumから取得）
	    List<Categories> categories = getAllCategoriesService.getAllCategories();

	    model.addAttribute("items", filteredItems);
	    model.addAttribute("categories", categories);
	    model.addAttribute("selectedCategory", category);
	    model.addAttribute("expiringSoon", expiringSoon);

	    return "home"; // 一覧ページのテンプレート名
	}
	
	
	
}