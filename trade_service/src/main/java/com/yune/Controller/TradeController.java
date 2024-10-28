package com.yune.Controller;

import com.yune.domain.Cart;
import com.yune.domain.ExpiredDetail;
import com.yune.domain.OrderDetail;
import com.yune.exceptions.UnauthorizedException;
import com.yune.service.TradeService;
import com.yune.unit.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/trade")
@Api("订单相关接口")
public class TradeController {

    @Autowired
    private TradeService tradeService;


    @ExceptionHandler(UnauthorizedException.class)
    public void handleTokenExpired(HttpServletResponse response) throws IOException {
        // 检测到 token 过期后，重定向到登录页面
        response.sendRedirect("/user/login");
    }
    @ApiOperation("提交订单")
    @PostMapping
    public R submitTrade(@RequestBody List<Integer> list, HttpSession session){
        return tradeService.submit(list, session);
    }
    @ApiOperation("查询所有订单")
    @GetMapping
    public R ListMyOrder(HttpSession session){
        return tradeService.listmyOrder(session);
    }
    @ApiOperation("查询所有订单详细信息")
    @GetMapping("/details")
    public R GetAllDetails(HttpSession session){
        return tradeService.getAllDetails(session);
    }
    @ApiOperation("根据订单Id查询详细信息")
    @GetMapping("/{orderId}")
    public R ListOrderDetail(@PathVariable("orderId")int orderId){
        return tradeService.listOrderDetail(orderId);
    }
    @ApiOperation("查询总逾期金额")
    @GetMapping("/totalExpiredFee")
    public R sumUserExpiredFee(HttpSession session){
        return tradeService.sumfee(session);
    }
    @ApiOperation("插叙用户所有逾期书籍相关信息")
    @GetMapping("/expired")
    public List<ExpiredDetail> isExpired(@RequestParam("userId") Long userId){
        return tradeService.isExpired(userId);
    }


    //管理员使用接口
    @CrossOrigin
    @ApiOperation("Admin查询所有逾期书籍")
    @GetMapping("/AdminGETExpiredBooks")
    public R ListAllExpiredBooks(){
        List<OrderDetail> admin = tradeService.admin();
        return R.ok(admin);
    }
    @CrossOrigin
    @ApiOperation("Admin删除逾期书籍")
    @DeleteMapping("/AdminDeleteBook")
    public R ADminDelete(@RequestParam("id") int id){
        return tradeService.admindelete(id);
    }

    @CrossOrigin
    @ApiOperation("Admin延长书籍过期时间")
    @PutMapping("/AdminExtendTime")
    public R AdminExtendTime(
            @RequestParam("id") int id,
            @RequestParam("time") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate time) {

        log.info("time is {}",time);

        return tradeService.adminExtend(id, time);
    }


}
