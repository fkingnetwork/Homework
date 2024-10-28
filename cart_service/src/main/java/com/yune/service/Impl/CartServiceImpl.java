package com.yune.service.Impl;
import cn.hutool.core.collection.ListUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yune.client.UserClient;
import com.yune.client.bookCliet;
import com.yune.domain.Book;
import com.yune.domain.Cart;
import com.yune.domain.CartDto;

import com.yune.mapper.CartMapper;
import com.yune.service.CartService;
import com.yune.unit.CollUtils;
import com.yune.unit.JwtTool;
import com.yune.unit.R;

import com.yune.unit.UserContext;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {
    @Autowired
    private StringRedisTemplate redisTemplate;


    private final bookCliet bookCliet;
    @Autowired
    private JwtTool jwtTool;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private UserClient userClient;



    @Override
    public R add(CartDto cartDto, HttpSession session) {
        Cart one = lambdaQuery().eq(Cart::getBookId, cartDto.getBookId()).one();
        if(one!=null){
            return R.error("you have added this book");
        }
        if(cartDto.getBookNum()<=0){
            return R.error("数量不得小于0");
        }

        //0.获取登录用户 from
        Long id = UserContext.getUser();


        if(userClient.getStatusByID(id.intValue())==0){
            return R.error("该账户未还书籍数量以达到3本，已被冻结，请先还书");
        }
        //1.根据id查询书,通过openfeign调用bookservcie
        Book book = bookCliet.qbI(cartDto.getBookId());
        //2.查询失败
        if(book==null){
            return R.error("something wrong");
        }
        //3.查询成功
        //3.1查询库存
        if(book.getStock()<=cartDto.getBookNum()){
            //4.库存不足
            return R.error("lack of stock");
        }
        //5.库存充足，写入数据库。

        Cart cart = Cart.builder()
                .bookName(book.getTitle())
                .bookId(cartDto.getBookId())
                .bookNum(cartDto.getBookNum())
                .borrowDate(cartDto.getBorrowDate())
                .returnDate(cartDto.getReturnDate())
                .userId(id.intValue())
                .expiredFee(30.2)
                .build();
        
        save(cart);
        return R.ok();

    }


    @Override
    public List<Cart> queryMyCarts(HttpSession session) {
        //0.获取登录用户 from redis
        Long id = UserContext.getUser();

        List<Cart> list = lambdaQuery().eq(Cart::getUserId, id).list();
        if(CollUtils.isEmpty(list)){
            return CollUtils.emptyList();
        }
        return list;
    }

    @Override
    public void deleteByIds(List<Integer> ids) {
        //0.获取登录用户 from redis
        Long id  = UserContext.getUser();
        //构建删除条件，bookid和userId
        QueryWrapper<Cart> cartQueryWrapper = new QueryWrapper<>();
        cartQueryWrapper.lambda()
                .eq(Cart::getUserId,id.intValue())
                .in(Cart::getBookId,ids);
        remove(cartQueryWrapper);
    }

    @Override
    public R updatebookById(CartDto cart, HttpSession session) {
        if (bookCliet.qbI(cart.getBookId()).getStock()<cart.getBookNum()) {
            return R.error("库存不足");
        }
        Cart pre = lambdaQuery().eq(Cart::getBookId, cart.getBookId()).one();
        BeanUtils.copyProperties(cart,pre);
        List<Integer> list = ListUtil.toList(cart.getBookId());
        deleteByIds(list);
        save(pre);
        return R.ok();
    }

    @Override
    public Cart queryByBookId(int bookid) {
        Cart one = lambdaQuery().eq(Cart::getBookId, bookid).one();
        return one;
    }
}
