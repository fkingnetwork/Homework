package com.yune.client;

import com.yune.domain.Book;
import com.yune.domain.Inventory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient("book-service")
public interface bookCliet {
    @PostMapping("book/queryById")
    public Book qbI(@RequestBody int bookId);

    @PutMapping("book/stock/delete")
    public void DeleteStock(Inventory inventory);
}
