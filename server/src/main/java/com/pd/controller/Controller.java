package com.pd.controller;

import com.pd.target.MyAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class Controller {
    Logger log = LoggerFactory.getLogger(Controller.class);
    @Value("${server.port}")
    String port;
    @RequestMapping(value ="/test", produces = "text/html; charset=utf-8")
    @MyAnnotation(methodName = "test")
    public String test(@RequestParam String url) throws Exception {
//        if(true) {
//            throw new RuntimeException("1111111111");
//        }
        return url;
    }
    @RequestMapping(value ="/", produces = "text/html; charset=utf-8")
    public String index() {
        return "访问端口：" + port;
    }
    @GetMapping("mono")
    public Mono<Object> mono() throws Exception{
        return Mono.create(monoSink -> {
            log.info("创建 Mono");
            monoSink.success("hello webflux");
        })
                .doOnSubscribe(subscription -> { //当订阅者去订阅发布者的时候，该方法会调用
                    log.info("{}",subscription);
                }).doOnNext(o -> { //当订阅者收到数据时，改方法会调用
                    log.info("{}",o);
                });
    }
}
