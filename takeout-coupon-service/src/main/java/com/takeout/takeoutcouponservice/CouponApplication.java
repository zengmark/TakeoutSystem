package com.takeout.takeoutcouponservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan(basePackages = "com.takeout")
@EnableFeignClients(basePackages = "com.takeout.takeoutserviceclient.service")
public class CouponApplication {
    public static void main(String[] args) {
        SpringApplication.run(CouponApplication.class, args);
    }
}
