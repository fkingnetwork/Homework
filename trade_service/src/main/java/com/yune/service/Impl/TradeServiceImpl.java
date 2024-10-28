package com.yune.service.Impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yune.Mapper.TradeMapper;
import com.yune.client.CartClient;
import com.yune.client.UserClient;
import com.yune.domain.*;
import com.yune.service.TradeService;
import com.yune.unit.JwtTool;
import com.yune.unit.R;

import com.yune.unit.UserContext;
import com.yune.utils.RedisLock;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.yune.client.bookCliet;

import javax.servlet.http.HttpSession;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeServiceImpl extends ServiceImpl<TradeMapper, Order> implements TradeService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private JwtTool jwtTool;
    @Autowired
    private TradeMapper tradeMapper;

    private final CartClient cartClient;

    private final bookCliet bookClient;

    private final UserClient userClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;



    @Override
    public R submit(List<Integer> listInt, HttpSession session) {
        System.out.println(listInt);
        List<Cart> list = new ArrayList<>();
        for(int id : listInt){
            Cart cart = cartClient.queryByBookId(id);
            list.add(cart);
        }
        Long userId = UserContext.getUser();
        int statusByID = userClient.getStatusByID(userId.intValue());
        if(statusByID!=1){
            return R.error("先还书");
        }
        //订单加密
        int prefix = LocalDateTime.now().getSecond();
        Order order = Order.builder()

                .userId(userId.intValue())
                .status(0)
                .createTime(LocalDateTime.now())
                .build();

        //TODO 添加阻塞
        save(order);

        for (Cart cart : list) {

            Book bookInfo = bookClient.qbI(cart.getBookId());

            if (bookInfo.getStock() < cart.getBookNum()) {

                return R.error(bookInfo.getTitle() + " 库存不足");
            }
            //数据库扣减库存
            Inventory inventory =new Inventory();
            inventory.setNum(cart.getBookNum());
            inventory.setBookId(cart.getBookId());
            //一人一单（redis分布式锁）
            RedisLock redisLock = new RedisLock(redisTemplate, "trade:"+userId);
            boolean b = redisLock.tryLock(10L);
            if(!b){
                //获取锁失败，报错
                return R.error("不允许重复借阅");
            }
            //使用队列发消息
            try {
                rabbitTemplate.convertAndSend("ExchangerMashware","Mashware", inventory);
                log.info("已发送消息：{}",inventory);
            } catch (AmqpException e) {
                throw new RuntimeException(e);
            }finally {
                redisLock.unlock();
                //删除redis中的分页缓存,根据bookId来匹配页数
                int bookId = inventory.getBookId();
                int page = bookId/10;
                System.out.println(page);
                Set<String> keys = redisTemplate.keys("books:page:" + page + ":*");
                System.out.println(keys);
                if (keys != null && !keys.isEmpty()) {
                    redisTemplate.delete(keys);  // 批量删除匹配到的所有键
                }

            }
            OrderDetail orderDetail = OrderDetail.builder()
                    .bookName(cart.getBookName())
                    .userId(userId.intValue())
                    .expiredFee(cart.getExpiredFee())
                    .orderId(order.getId())
                    .bookId(cart.getBookId())
                    .bookNum(cart.getBookNum())
                    .borrowDate(cart.getBorrowDate())
                    .returnDate(cart.getReturnDate())
                    .build();
            tradeMapper.insertOrderDetail(orderDetail);
        }
        //删除购物车记录
        cartClient.deleteByIds(listInt);
        return R.ok();
    }

    @Override
    public R sumfee(HttpSession session) {
        Long userId = UserContext.getUser();
        Double totalFee = tradeMapper.getTotalFee(userId,LocalDateTime.now());
        return R.ok(totalFee);
    }

    @Override
    public List<ExpiredDetail> isExpired(Long userId) {
        List<ExpiredDetail> list = new ArrayList<>();
        List<LocalDateTime> returnTimes = tradeMapper.getReturnTime(userId);
        returnTimes.forEach(time->{
            if(time.isEqual(LocalDateTime.now())|| time.isBefore(LocalDateTime.now())){

                //计算超时时间
                Duration between = Duration.between(time, LocalDateTime.now());

                List<OrderDetail> orderDetails = tradeMapper.queryExpiredBookByReturnTime(time);
                orderDetails.forEach(orderDetail -> {
                    int bookId = orderDetail.getBookId();
                    Book book = bookClient.qbI(bookId);
                    String title = book.getTitle();
                    ExpiredDetail expiredDetail = new ExpiredDetail();
                    expiredDetail.setOrderId(orderDetail.getOrderId());
                    expiredDetail.setExpiredDays(between);
                    expiredDetail.setBookName(title);
                    list.add(expiredDetail);
                });
            }
        });
        if(list.isEmpty()){
            return Collections.emptyList();
        }else {
            return list;
        }
    }

    @Override
    public R listmyOrder(HttpSession session) {
        Integer userId =UserContext.getUser().intValue();
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        List<Order> list = list(queryWrapper);
        if(list.isEmpty()){
            return R.ok(Collections.emptyList());
        }
        return R.ok(list);
    }

    @Override
    public R listOrderDetail(int orderId) {
        List<OrderDetail> orderDetails = tradeMapper.queryDetail(orderId);
        return R.ok(orderDetails);
    }

    @Override
    public R getAllDetails(HttpSession session) {
        int userId = UserContext.getUser().intValue();
        List<OrderDetail> orderDetails = tradeMapper.getallDetails(userId);
        return R.ok(orderDetails);
    }
    //services for admin
    @Override
    public List<OrderDetail> admin() {
        return tradeMapper.adminselect(LocalDateTime.now());

    }

    @Override
    public R admindelete(int id) {
        OrderDetail orderDetail = tradeMapper.selectOneorderdetail(id);
        int stock = orderDetail.getBookNum();
        int bookId = orderDetail.getBookId();
        Inventory inventory = new Inventory();
        inventory.setBookId(bookId);
        //用符号代表加库存
        inventory.setNum(-stock);
        bookClient.DeleteStock(inventory);
        boolean admindelete = tradeMapper.admindelete(id);
        if (!admindelete) return R.error("删除失败");
        Long userId = Long.valueOf(orderDetail.getUserId());
        List<ExpiredDetail> expired = isExpired(userId);
        if(expired.size()<6){
            tradeMapper.changeStatus(userId,0);
        }
        if(expired.size()<3){
            tradeMapper.changeStatus(userId,1);
        }
        return R.ok();

    }

    @Override
    public R adminExtend(int id, LocalDate time) {

        boolean adminextend = tradeMapper.adminextend(id, time);
        if (!adminextend) return R.error("更新时间失败");
        else return R.ok();
    }
}
