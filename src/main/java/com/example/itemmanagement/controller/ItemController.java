package com.example.itemmanagement.controller;

import com.example.itemmanagement.entity.Categories;
import com.example.itemmanagement.entity.Items;
import com.example.itemmanagement.form.AddItemForm;
import com.example.itemmanagement.security.LoginUser;
import com.example.itemmanagement.service.AddItemService;
import com.example.itemmanagement.service.GetAllCategoriesService;
import com.example.itemmanagement.service.GetAllItemsService;
import com.example.itemmanagement.service.UpdateItemService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/items")
public class ItemController {

    private final GetAllItemsService getAllItemsService;
    private final GetAllCategoriesService getAllCategoriesService;
    private final AddItemService addItemService;
    private final UpdateItemService updateItemService;

    public ItemController(
            GetAllItemsService getAllItemsService,
            GetAllCategoriesService getAllCategoriesService,
            AddItemService addItemService,
            UpdateItemService updateItemService) {

        this.getAllItemsService = getAllItemsService;
        this.getAllCategoriesService = getAllCategoriesService;
        this.addItemService = addItemService;
        this.updateItemService = updateItemService;
    }

    @GetMapping("/add")									//食材登録画面をリクエストされた時
    public String add(Model model) {

        List<Categories> categories = getAllCategoriesService.getAllCategories();

        model.addAttribute("categories", categories);

        model.addAttribute("form", new AddItemForm());

        return "add";

    }

    @PostMapping("/add")
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

        return "redirect:/items/add";
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

        return "redirect:/";
    }
}
