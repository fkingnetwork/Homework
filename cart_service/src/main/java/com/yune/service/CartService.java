package com.yune.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yune.domain.Cart;
import com.yune.domain.CartDto;
import com.yune.unit.R;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface CartService extends IService<Cart> {
    R add(CartDto cartDto, HttpSession session);

    List<Cart> queryMyCarts(HttpSession session);

    void deleteByIds(List<Integer> ids);

    R updatebookById(CartDto cart, HttpSession session);

    Cart queryByBookId(int bookid);
}
