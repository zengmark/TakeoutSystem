package com.takeout.takeoutmenuservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan(basePackages = "com.takeout")
public class MenuApplication {
    public static void main(String[] args) {
        SpringApplication.run(MenuApplication.class, args);
    }
}
