package com.yune.Controller;

import com.google.code.kaptcha.Producer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
@Api("验证码接口")
@Controller
public class CaptchaController {
    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private StringRedisTemplate redisTemplate;
    @ApiOperation("生成验证码")
    @GetMapping("/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 设置响应头信息
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setContentType("image/jpeg");

        // 生成验证码文本
        String captchaText = kaptchaProducer.createText();

        // 使用 Redis 存储验证码，过期时间设置为5分钟
        String sessionId = request.getSession().getId();
        System.out.println(sessionId);
        redisTemplate.opsForValue().set("pre",sessionId,5,TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(sessionId, captchaText, 5, TimeUnit.MINUTES);

        // 创建验证码图片
        BufferedImage captchaImage = kaptchaProducer.createImage(captchaText);

        // 将图片写入响应
        ImageIO.write(captchaImage, "jpg", response.getOutputStream());
    }
}
