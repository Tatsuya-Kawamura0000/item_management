package com.example.itemmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
    public String showRegisterForm() {
        return "register";
    }
	
	@PostMapping("/register")
    public String registerUser(@ModelAttribute RegisterForm form) {

		registerService.register(form);  // Serviceで登録処理

        return "redirect:/login";
    }

}
