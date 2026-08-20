package com.example.itemmanagement.controller;

import com.example.itemmanagement.dto.DashboardViewModel;
import com.example.itemmanagement.security.LoginUser;
import com.example.itemmanagement.service.ItemSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private ItemSummaryService itemSummaryService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal LoginUser loginUser) {

        DashboardViewModel dashboardData = itemSummaryService.getDashboardData(loginUser.getId());

        model.addAttribute("summary", dashboardData.getSummary());
        model.addAttribute("soonFoods", dashboardData.getSoonFoods());
        model.addAttribute("expiredFoods", dashboardData.getExpiredFoods());

        return "dashboard";
    }

}
