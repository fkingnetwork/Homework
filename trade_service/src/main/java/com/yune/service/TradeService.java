package com.yune.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yune.domain.ExpiredDetail;
import com.yune.domain.Order;
import com.yune.domain.OrderDetail;
import com.yune.unit.R;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;

public interface TradeService extends IService<Order> {
    R submit(List<Integer> list, HttpSession session);

    R sumfee(HttpSession session);

    List<ExpiredDetail> isExpired(Long userId);

    R listmyOrder(HttpSession session);

    R listOrderDetail(int orderId);

    R getAllDetails(HttpSession session);

    List<OrderDetail> admin();

    R admindelete(int id);

    R adminExtend(int id, LocalDate time);
}
