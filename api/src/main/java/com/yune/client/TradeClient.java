package com.yune.client;

import com.yune.domain.ExpiredDetail;
import com.yune.unit.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("trade-service")
public interface TradeClient {
    @GetMapping("/trade/expired")
    public List<ExpiredDetail> isExpired(@RequestParam("userId") Long userId);
}
