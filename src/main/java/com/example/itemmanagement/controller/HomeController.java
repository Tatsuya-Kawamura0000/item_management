package com.example.itemmanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.itemmanagement.entity.Categories;
import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.form.AddItemForm;
import com.example.itemmanagement.service.AddItemService;
import com.example.itemmanagement.service.GetAllCategoriesService;
import com.example.itemmanagement.service.GetAllItemsService;
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

	
	@GetMapping											//home画面をリクエストされた時
	public String index(Model model) {

		
		List<Items> items = getAllItemsService.getAllItems();
		
		
	    for (Items item : items) {						// 期限3日以内かどうかのフラグをセット
	    	
	        if (item.getDeadline() != null) {
	        	
	            boolean expiringSoon = java.time.temporal.ChronoUnit.DAYS.between(
	            		
	                    java.time.LocalDate.now(), item.getDeadline()) <= 3;
	            
	            item.setExpiringSoon(expiringSoon);
	            
	        } else {
	        	
	            item.setExpiringSoon(false);
	            
	        }
	        
	    }
		
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
	public String create(@Validated @ModelAttribute("form") AddItemForm form, BindingResult result, Model model) {

		List<Categories> categories = getAllCategoriesService.getAllCategories();


			if (result.hasErrors()) {							//バリデーションでエラーを捕まえたとき

				model.addAttribute("categoryId", categories);
				
				model.addAttribute("form", form);

				return "add";							//ユーザ登録画面を返す

			}

		
		addItemService.add(form);					// 食材追加処理実行

		return "redirect:/users";

		
	}
	
	@PostMapping("/stop/{id}")										//使い切ったボタンを押した食品のIDを受け取る
	public String stop(@PathVariable("id") int id, Model model) {
 		
		stopItemService.stopItem(id);								//食品の論理削除（）メソッド呼び出し。status=1→0 に。
		
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
	public String shoppingList(Model model) {

		return "shoppingList";									

	}
	
}