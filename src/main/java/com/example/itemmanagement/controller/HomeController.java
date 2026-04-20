package com.example.itemmanagement.controller;

import com.example.itemmanagement.dto.HomeViewModel;
import com.example.itemmanagement.security.LoginUser;
import com.example.itemmanagement.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/users")
public class HomeController {

	@Autowired
	private HomeService homeService;


	@GetMapping
	public String index(Model model,
	        @AuthenticationPrincipal LoginUser loginUser) {

	    // ログインユーザーID情報格納
	    Integer userId = loginUser.getId();

		HomeViewModel hvm = homeService.getHomeData(userId, null, null, null);

		model.addAttribute("hvm", hvm);
	    
	    return "home";

	}

	@GetMapping("/filter")
	public String filterItems(
			@RequestParam(required = false) Integer category,
			@RequestParam(required = false) Boolean expiringSoon,
			@RequestParam(required = false) Boolean expired,
			@AuthenticationPrincipal LoginUser loginUser,
			Model model) {

		Integer userId = loginUser.getId();

		HomeViewModel hvm = homeService.getHomeData(userId, category, expiringSoon, expired);

		model.addAttribute("hvm", hvm);

		return "home";
	}

}
	
