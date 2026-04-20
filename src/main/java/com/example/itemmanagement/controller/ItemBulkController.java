package com.example.itemmanagement.controller;

import com.example.itemmanagement.security.LoginUser;
import com.example.itemmanagement.service.UpdateItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

public class ItemBulkController {

    private final UpdateItemService updateItemService;

    public ItemBulkController(UpdateItemService updateItemService) {
        this.updateItemService = updateItemService;
    }

    @PostMapping("/bulk-delete")
    @ResponseBody
    public ResponseEntity<?> bulkDelete(@RequestBody List<Integer> ids,
                                        @AuthenticationPrincipal LoginUser loginUser){

        Integer userId = loginUser.getId();

        updateItemService.bulkDeleteFromItems(ids, userId);

        return ResponseEntity.ok().build();
    }
}
