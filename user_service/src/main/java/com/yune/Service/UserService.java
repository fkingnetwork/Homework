package com.yune.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yune.Domain.R;
import com.yune.Domain.User;
import com.yune.Domain.UserDTO;

import javax.servlet.http.HttpSession;

public interface UserService extends IService<User> {
    R login(UserDTO userDTO, String redisCaptcha, String captcha, HttpSession session) throws Exception;

    R register(UserDTO userDTO) throws Exception;

    R quit();
}
