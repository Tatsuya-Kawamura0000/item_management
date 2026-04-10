package com.example.itemmanagement.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.itemmanagement.entity.Categories;
import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.entity.ShoppingListItem;
import com.example.itemmanagement.form.AddItemForm;
import com.example.itemmanagement.mapper.ShoppingListMapper;
import com.example.itemmanagement.security.LoginUser;
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
	public String index(Model model,
	        @AuthenticationPrincipal LoginUser loginUser) {

	    // ログインユーザー情報取得
	    String username = loginUser.getUsername();
	    Integer userId = loginUser.getId();

	    // ユーザーごとの食材一覧取得
	    List<Items> items = getAllItemsService.getAllItems(userId);

		//home.htmlが、th:each="item : ${filteredItems}"で表示しているため
		List<Items> filteredItems = items;

	    int expiredCount = 0;
	    
	    int warningCount = 0;
	    
	    
	    for (Items item : filteredItems) {
	        if (item.getDeadline() != null) {

	            long days = java.time.temporal.ChronoUnit.DAYS.between(
	                    java.time.LocalDate.now(), item.getDeadline());

	            if (days < 0) {
	                item.setMessage("期限切れです");
	                expiredCount++;						//期限切れ食材をカウント
	            } else if (days <= 3) {
	                item.setMessage("気を付けて");
	                warningCount++;						//期限間近食材をカウント
	            } else {
	                item.setMessage("");
	            }

	        } else {
	            item.setMessage("");
	        }
	    }

	    //カテゴリー情報取得
	    List<Categories> categories = getAllCategoriesService.getAllCategories();

	    model.addAttribute("categories", categories);
	    model.addAttribute("items", items);
		model.addAttribute("filteredItems", filteredItems);
	    model.addAttribute("expiredCount", expiredCount);	//期限切れ食材数を渡す
	    model.addAttribute("warningCount", warningCount);	//期限間近食材をカウント
	    
	    int shoppingCount = addToShoppingListService.getShoppingListCount(userId); //買い物リストのアイテム数を取得
	    model.addAttribute("shoppingCount", shoppingCount);
	    
	    model.addAttribute("selectedCategory", null);

		Map<Integer, Integer> categoryCounts =
				getAllCategoriesService.getCategoryCounts(userId);

		model.addAttribute("categoryCounts", categoryCounts);
	    
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
	public String create(
	        @Validated @ModelAttribute("form") AddItemForm form,
	        BindingResult result,
	        Model model,
	        RedirectAttributes redirectAttributes,
	        @AuthenticationPrincipal LoginUser loginUser) {

	    List<Categories> categories = getAllCategoriesService.getAllCategories();

	    if (result.hasErrors()) {

	        model.addAttribute("categories", categories);
	        return "add";
	    }

	    // ログインユーザーID取得
	    Integer userId = loginUser.getId();

	    // userIdを渡して登録
	    addItemService.add(form, userId);

	    redirectAttributes.addFlashAttribute("successMessage", "食材を登録しました！");

	    return "redirect:/users/add";
	}
	
	@PostMapping("/stop/{id}")
	public String stop(
	        @PathVariable("id") int id,
	        RedirectAttributes redirectAttributes,
	        @AuthenticationPrincipal LoginUser loginUser) {

	    Integer userId = loginUser.getId();

	    // ユーザーID込みで取得
	    Items item = getAllItemsService.getItemById(id, userId);

	    if (item.isFavorite()) {
	        redirectAttributes.addFlashAttribute("confirmAddToList", true);
	        redirectAttributes.addFlashAttribute("targetItemId", id);
	    }

	    // ユーザーID込みで削除
	    stopItemService.stopItem(id, userId);

	    redirectAttributes.addFlashAttribute("successMessage", "食材を使い切りました！");

	    return "redirect:/users";
	}
	
	@GetMapping("/edit/{id}")
	public String edit(
	        @PathVariable("id") int id,
	        Model model,
	        @AuthenticationPrincipal LoginUser loginUser) {

	    Integer userId = loginUser.getId();

	    // id + userId で取得
	    Items item = getAllItemsService.getItemById(id, userId);

	    List<Categories> categories = getAllCategoriesService.getAllCategories();

	    model.addAttribute("item", item);
	    model.addAttribute("categories", categories);

	    return "edit";
	}


	@PostMapping("/update/{id}")
	public String update(
	        @PathVariable("id") int id,
	        @Validated @ModelAttribute("item") Items item,
	        BindingResult result,
	        Model model,
	        @AuthenticationPrincipal LoginUser loginUser) {

	    if (result.hasErrors()) {

	        List<Categories> categories = getAllCategoriesService.getAllCategories();
	        model.addAttribute("categories", categories);

	        return "edit";
	    }

	    Integer userId = loginUser.getId();

	    updateItemService.updateItem(id, userId, item);

	    return "redirect:/users";
	}


	
	@GetMapping("/shoppingList")
	public String shoppingList(
	        Model model,
	        HttpServletRequest request,
	        @AuthenticationPrincipal LoginUser loginUser) {

	    // ログインユーザーID取得
	    Integer userId = loginUser.getId();

	    // CSRFトークン
	    CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
	    model.addAttribute("_csrf", csrfToken);

	    // ユーザーごとの買い物リスト取得
	    List<ShoppingListItem> listItems = shoppingListMapper.findAll(userId);
	    model.addAttribute("listItems", listItems);

	    // カテゴリー一覧
	    List<Categories> categories = getAllCategoriesService.getAllCategories();
	    model.addAttribute("categories", categories);

	    return "shoppingList";
	}
	
	@PostMapping("/favorite/{id}")
	public String toggleFavorite(
	        @PathVariable("id") int id,
	        @RequestParam(required = false) Integer category,
	        @RequestParam(required = false) Boolean expiringSoon,
	        @AuthenticationPrincipal LoginUser loginUser) {

	    Integer userId = loginUser.getId();

	    // id + userId で取得
	    Items item = getAllItemsService.getItemById(id, userId);

	    // favorite反転
	    item.setFavorite(!item.isFavorite());

	    // 更新
	    updateItemService.updateFavorite(item, userId);

	    // フィルター条件がある場合
	    if (category != null || expiringSoon != null) {
	        StringBuilder url = new StringBuilder("redirect:/users/filter?");
	        if (category != null) url.append("category=").append(category).append("&");
	        if (expiringSoon != null && expiringSoon) url.append("expiringSoon=true");
	        return url.toString();
	    }

	    return "redirect:/users";
	}
	
	@PostMapping("/add-to-shopping-list/{id}")
	public String addToShoppingList(
	        @PathVariable("id") int id,
	        @AuthenticationPrincipal LoginUser loginUser,
	        RedirectAttributes redirectAttributes) {

	    Integer userId = loginUser.getId();

	    // サービス呼び出しでShoppingListに追加
	    addToShoppingListService.addItemToList(id, userId);

	    // フラッシュメッセージ
	    redirectAttributes.addFlashAttribute("successMessage", "買い物リストに追加しました！");

	    return "redirect:/users";  
	}

	@GetMapping("/filter")
	public String filterItems(
	        @RequestParam(required = false) Integer category,
	        @RequestParam(required = false) Boolean expiringSoon,
	        @AuthenticationPrincipal LoginUser loginUser,
	        Model model) {

	    Integer userId = loginUser.getId();

		List<Items> items = getAllItemsService.getAllItems(userId);
	    List<Items> filteredItems = getFilterItemsService.filterItems(category, expiringSoon, userId);

	    int expiredCount = 0;
	    int warningCount = 0;

	    for (Items item : filteredItems) {

	        if (item.getDeadline() != null) {

	            long days = java.time.temporal.ChronoUnit.DAYS.between(
	                    java.time.LocalDate.now(), item.getDeadline());

	            if (days < 0) {
	                item.setMessage("期限切れです");
	                expiredCount++;
	            } else if (days <= 3) {
	                item.setMessage("気を付けて");
	                warningCount++;
	            } else {
	                item.setMessage("");
	            }

	        } else {
	            item.setMessage("");
	        }
	    }

	    List<Categories> categories = getAllCategoriesService.getAllCategories();

	    model.addAttribute("items", items);
		model.addAttribute("filteredItems", filteredItems);
	    model.addAttribute("categories", categories);
	    model.addAttribute("selectedCategory", category);
	    model.addAttribute("expiringSoon", expiringSoon);

	    // これを追加
	    model.addAttribute("expiredCount", expiredCount);
	    model.addAttribute("warningCount", warningCount);

	    int shoppingCount = addToShoppingListService.getShoppingListCount(userId);
	    model.addAttribute("shoppingCount", shoppingCount);

		Map<Integer, Integer> categoryCounts =
				getAllCategoriesService.getCategoryCounts(userId);

		model.addAttribute("categoryCounts", categoryCounts);

	    return "home";
	}
	
	@PostMapping("/bulk-delete")
	@ResponseBody
	public ResponseEntity<?> bulkDelete(@RequestBody List<Integer> ids,
	        @AuthenticationPrincipal LoginUser loginUser){

	    Integer userId = loginUser.getId();

	    updateItemService.bulkDeleteFromItems(ids, userId);

	    return ResponseEntity.ok().build();
	}

	@PostMapping("/bulk-add-shopping-list")
	@ResponseBody
	public ResponseEntity<?> bulkAddShoppingList(
			@RequestBody List<Integer> ids,
			@AuthenticationPrincipal LoginUser loginUser){

		Integer userId = loginUser.getId();
		//重複していたアイテムのリスト
		List<String> duplicatedItems = new ArrayList<>();

		for(Integer id : ids){

			List<String> result = addToShoppingListService.addItemToList(id, userId);

			if(!result.isEmpty()){
				duplicatedItems.addAll(result);
			}
		}
		// 重複が1つでもあればまとめて返す
		if(!duplicatedItems.isEmpty()){
			String message = String.join("、", duplicatedItems)
					+ " はすでに買い物リストに存在しています";

			return ResponseEntity
						.badRequest()
						.body(message);
			}


		return ResponseEntity.ok().build();
	}
}
	
