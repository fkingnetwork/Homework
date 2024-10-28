package com.yune.client;

import com.yune.domain.Cart;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@FeignClient("cart-service")
public interface CartClient {
    @GetMapping("cart/queryByBookId")
    public Cart queryByBookId(@RequestParam("bookid") int bookid);

    @DeleteMapping("cart")
    public void deleteByIds(@RequestParam("ids") List<Integer> ids);
}
