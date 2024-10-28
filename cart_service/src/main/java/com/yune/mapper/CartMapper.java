package com.yune.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yune.domain.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CartMapper extends BaseMapper<Cart> {

    @Select("select * from cart where book_id = #{bookid}")
    Cart queryByBookId(int bookid);
}
