package com.yune.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yune.Domain.R;
import com.yune.Domain.User;
import com.yune.Domain.UserDTO;
import com.yune.Mapper.UserMapper;
import com.yune.Service.UserService;
import com.yune.client.TradeClient;
import com.yune.client.UserClient;
import com.yune.config.AES;
import com.yune.domain.ExpiredDetail;
import com.yune.unit.UserContext;
import com.yune.util.JwtTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@EnableAutoConfiguration
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private JwtTool jwtTool;
    @Autowired
    private TradeClient tradeClient;
    @Autowired
    private UserMapper userMapper;

    @Override
    public R login(UserDTO userDTO, String redisCaptcha, String captcha, HttpSession session) throws Exception {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if(username.isEmpty() || password.isEmpty()){
            return R.error("用户名或密码为空");
        }
        //数据库查询用户
        User user = lambdaQuery().eq(User::getUsername, username).one();
        if(user == null){
            return R.error("用户不存在");
        }
        List<ExpiredDetail> expired = tradeClient.isExpired(user.getId());
        if(!expired.isEmpty()){
            if(expired.size()>=3 && expired.size()<=6){
                userMapper.changeStatus(user.getId(),0);
            }else if(expired.size()>6){
                userMapper.changeStatus(user.getId(),-1);
            }
        }
        if(user.getStatus()==-1){
            return R.error("用户被冻结");
        }
        if(!AES.decrypt(user.getPassword()).equals(password)){
            return R.error("密码错误");
        }
        if (redisCaptcha == null || !redisCaptcha.equalsIgnoreCase(captcha)) {
            return R.error("验证码错误");
        }

        if(user.getId()==0){
            return R.error_admin("admin");
        }
        //生成token
        String token = jwtTool.createToken(user.getId(), Duration.ofMinutes(300));
        return R.ok(token);
    }

    @Override
    public R register(UserDTO userDTO) throws Exception {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        String password = userDTO.getPassword();
        String encryptedPassword = AES.encrypt(password);
        user.setPassword(encryptedPassword);
        save(user);
        return R.ok();

    }

    @Override
    public R quit() {
        UserContext.rmUser();
        return R.ok();
    }
}
