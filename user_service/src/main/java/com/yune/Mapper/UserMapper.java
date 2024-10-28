package com.yune.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yune.Domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Update("update user set status=#{status} where id = #{id}")
    void changeStatus(@Param("id") Long id,@Param("status") int status);
    @Update("update  user set update_time = #{time}where id = #{id}")
    void updateTime(@Param("id") Long id,@Param("time") LocalDateTime time);
}
