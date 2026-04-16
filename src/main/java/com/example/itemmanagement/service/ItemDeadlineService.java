package com.example.itemmanagement.service;

import com.example.itemmanagement.entity.Items;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ItemDeadlineService {

    public void applyDeadlineMessage(List<Items> items) {
        for (Items item : items) {
            if (item.getDeadline() != null) {

                long days = ChronoUnit.DAYS.between(
                        LocalDate.now(), item.getDeadline());

                if (days < 0) {
                    item.setMessage("期限切れです");
                } else if (days <= 3) {
                    item.setMessage("期限間近");
                } else {
                    item.setMessage("");
                }
            } else {
                item.setMessage("");
            }
        }
    }
}
