package com.example.itemmanagement.service;

import com.example.itemmanagement.dto.ItemSummary;
import com.example.itemmanagement.entity.Items;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ItemSummaryService {

    public ItemSummary summarize(List<Items> items) {

        int expired = 0;
        int warning = 0;

        for (Items item : items) {
            if (item.getDeadline() != null) {

                long days = ChronoUnit.DAYS.between(
                        LocalDate.now(), item.getDeadline());

                if (days < 0) expired++;
                else if (days <= 3) warning++;
            }
        }

        return new ItemSummary(expired, warning);
    }
}
