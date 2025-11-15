package com.aliyara.customerservice.client.supplyClient;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient ("supply-service")
public interface OrderFeignClient {
}