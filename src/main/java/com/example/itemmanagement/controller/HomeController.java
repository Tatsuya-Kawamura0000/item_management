package com.example.itemmanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.itemmanagement.entity.Categories;
import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.form.AddItemForm;
import com.example.itemmanagement.service.AddItemService;
import com.example.itemmanagement.service.GetAllCategoriesService;
import com.example.itemmanagement.service.GetAllItemsService;

@Controller
@RequestMapping("/users")
public class HomeController {

	@Autowired
	private GetAllItemsService getAllItemsService;
	
	@Autowired
	private GetAllCategoriesService getAllCategoriesService;
	
	@Autowired
	private  AddItemService addItemService;

	
	@GetMapping
	public String index(Model model) {

		
		List<Items> items = getAllItemsService.getAllItems();
		
		model.addAttribute("items", items);

		return "home"; //ホーム画面を返す
	}

	@GetMapping("/add")									//ユーザ登録画面をリクエストされた時
	public String create(Model model) {

		List<Categories> categories = getAllCategoriesService.getAllCategories();
		
		model.addAttribute("categories", categories);

		model.addAttribute("form", new AddItemForm());

		return "add";								//ユーザ登録画面を返す

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
}