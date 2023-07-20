package com.coolfish.websocketonnetty.controller;

import com.coolfish.websocketonnetty.service.PushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @className: TestPushController
 * @description: TODO 类描述
 * @author: xufh
 * @date: 2023/6/2
 */
@RestController
@RequestMapping("/push")
public class TestPushController {
    @Autowired
    private PushService pushService;
    @GetMapping
    public void testPush(HttpServletRequest request){
        int remotePort = request.getRemotePort();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()){
            String string = headerNames.nextElement();
            System.out.println("string = " + string);
            String header = request.getHeader(string);
            System.out.println("header = " + header);
        }
        pushService.pushMsgToOneByKey("ly2028:Q0100001" +":"+ "zhangsan", "hello client");
    }
}
