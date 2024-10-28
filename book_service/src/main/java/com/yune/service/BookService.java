package com.yune.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yune.domain.Book;
import com.yune.domain.Inventory;
import com.yune.unit.R;

public interface BookService extends IService<Book> {
    IPage<Book> getBooksByPage(int currentPage, int PageSize);

    Book queryById(int bookId);

    void dropOneStock(Inventory inventory);

    R selectByTittle(String title,String author,String genre);



}
