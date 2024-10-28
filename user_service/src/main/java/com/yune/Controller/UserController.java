package com.yune.Controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yune.Domain.R;
import com.yune.Domain.User;
import com.yune.Domain.UserDTO;
import com.yune.Mapper.UserMapper;
import com.yune.Service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
@Api("用户相关接口")
@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private UserMapper userMapper;
    @ApiOperation("登录接口")
    @PostMapping("/login")
    public R login(@RequestBody @Validated UserDTO userDTO, @RequestParam("captcha") String captcha, HttpSession sessionx) throws Exception {
        String sessionId = redisTemplate.opsForValue().get("pre");
        String redisCaptcha = redisTemplate.opsForValue().get(sessionId);
        return userService.login(userDTO,redisCaptcha,captcha,sessionx);
    }
    @ApiOperation("注册接口")
    @PostMapping("/register")
    public R register(@RequestBody UserDTO userDTO) throws Exception {
        return userService.register(userDTO);
    }
    @ApiOperation("根据用户Id查询状态")
    @GetMapping("/status")
    public int getStatusByID(@RequestParam("userId") int userId){
        return userService.getById(userId).getStatus();
    }
    @ApiOperation("退出登录接口")
    @PostMapping("/quit")
    public R quit(){
        return userService.quit();
    }

    //管理员使用的接口 用户列表，删除用户，修改状态
    @ApiOperation("Admin查询所有用户")
    @CrossOrigin
    @GetMapping("/adminUser")
    public R listAllusers(){
        List<User> list = userService.list();
        if (list.isEmpty()){
            return R.error("查询失败");
        }
        return R.ok(list);
    }
    @ApiOperation("Admin删除用户")
    @CrossOrigin
    @PutMapping("/deleteUser")
    public R deleteUser(@RequestParam("id") int id){
        if (userService.removeById(id)) {
            return R.ok();
        }
        else return R.error("删除失败");
    }
    @ApiOperation("Admin改变用户状态")
    @CrossOrigin
    @PutMapping("/changeStatus")
    public R changeStatus(@RequestParam("id") Long id,@RequestParam("status")int status){
        userMapper.changeStatus(id,status);
        return R.ok();
    }
}

