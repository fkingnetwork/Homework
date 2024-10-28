package com.yune.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yune.domain.Order;
import com.yune.domain.OrderDetail;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TradeMapper extends BaseMapper<Order> {


    @Insert("INSERT INTO order_detail (order_id, user_id, book_id, borrow_date, return_date, expired_fee, book_num,book_name) " +
            "VALUES (#{orderId}, #{userId}, #{bookId}, #{borrowDate}, #{returnDate}, #{expiredFee}, #{bookNum},#{bookName})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id") // 自动回填主键
    void insertOrderDetail(OrderDetail orderDetail);

    @Select("SELECT SUM(expired_fee) FROM order_detail WHERE user_id = #{userId} AND return_date < #{now}")
    Double getTotalFee(@Param("userId") Long userId, @Param("now") LocalDateTime now);




    @Select("SELECT return_date from order_detail where user_id = #{userId}")
    List<LocalDateTime> getReturnTime(Long userId);


    @Select("select * from order_detail where return_date = #{time} ")
    List<OrderDetail> queryExpiredBookByReturnTime(LocalDateTime time);


    @Select("select * from order_detail where return_date < #{time} ")
    List<OrderDetail> adminselect(LocalDateTime time);


    @Select("select * from order_detail where order_id = #{orderId}")
    List<OrderDetail> queryDetail(int orderId);


    @Select("select * from order_detail where user_id = #{userId}")
    List<OrderDetail> getallDetails(int userId);

    @Select("select * from order_detail where id = #{id}")
    OrderDetail selectOneorderdetail(int id);

    @Delete("delete from order_detail where id=#{id}")
    boolean admindelete(int id);
    @Update("update order_detail set return_date = #{time} where id = #{id}")
    boolean adminextend(@Param("id") int id,@Param("time")  LocalDate time);

    @Update("update user set status=#{status} where id = #{id}")
    void changeStatus(@Param("id") Long id,@Param("status") int status);
}
