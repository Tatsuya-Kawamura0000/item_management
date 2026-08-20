package com.example.itemmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FoodListController {

    @GetMapping("/foods")
    public String index() {
        return "index";
    }
}