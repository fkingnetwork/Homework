package com.yune.controller;

import com.yune.domain.Cart;
import com.yune.domain.CartDto;
import com.yune.service.CartService;
import com.yune.unit.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
@Api("预顶相关接口")
@RestController
@RequestMapping("/cart")
@EnableFeignClients
public class CartController {
    @Autowired
    private CartService cartService;
    @ApiOperation("添加到预定库")
    @PostMapping("/add")
    public R add(@RequestBody CartDto cartDto, HttpSession session){
        return cartService.add(cartDto,session);
    }
    @ApiOperation("根据Id查询预定库中书籍")
    @GetMapping("/queryByBookId")
    public Cart queryByBookId(@RequestParam("bookid") int bookid){
        return cartService.queryByBookId(bookid);
    }
    @ApiOperation("查询预定库中所有书籍")
    @GetMapping
    public List<Cart> queryMyCarts(HttpSession session){return cartService.queryMyCarts(session);}
    @ApiOperation("跟新预定库中书籍借阅时间")
    @PutMapping
    public R updateCart(@RequestBody CartDto cart,HttpSession session){return cartService.updatebookById(cart,session);}
    @ApiOperation("删除预定库中的书籍")
    @DeleteMapping
    public void deleteByIds(@RequestParam("ids") List<Integer> ids,HttpSession session){cartService.deleteByIds(ids);}

}
