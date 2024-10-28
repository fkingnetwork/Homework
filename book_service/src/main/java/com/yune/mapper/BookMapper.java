package com.yune.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yune.domain.Book;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Service;

public interface BookMapper extends BaseMapper<Book> {

    @Select("select * from Books where book_id = #{bookId}")
    Book selectoneBook(int bookId);

    @Update("update Books set stock=stock-#{num} where book_id=#{bookId}")
    void dropOneStock(@Param("num") int num,@Param("bookId") int bookId);

}
