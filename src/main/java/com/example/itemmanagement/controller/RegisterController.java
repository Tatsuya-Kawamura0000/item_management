package com.example.itemmanagement.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.itemmanagement.form.RegisterForm;
import com.example.itemmanagement.service.RegisterService;

@Controller
public class RegisterController {
	
	@Autowired
	private RegisterService registerService;
	
	@GetMapping("/register")
    public String showRegisterForm(Model model) {
		
		model.addAttribute("registerForm", new RegisterForm());
		
        return "register";
    }
	
	@PostMapping("/register")
	public String registerUser(@ModelAttribute @Valid RegisterForm form, BindingResult bindingResult, Model model) {

		    // バリデーションエラーがある場合は再表示
	        if (bindingResult.hasErrors()) {
	            return "register";
	        }
		
		
	        // パスワード一致チェック
	        if (!form.getPassword().equals(form.getConfirmPassword())) {
	            model.addAttribute("confirmPassworderrorMessage", "パスワードが一致しません");
	            return "register";
	        }

	        registerService.register(form);

	        return "redirect:/login";
	}

}
