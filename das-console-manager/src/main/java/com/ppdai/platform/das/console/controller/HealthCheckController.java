package com.ppdai.platform.das.console.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by wang.liang on 2018/8/23.
 */
@Slf4j
@RestController
@RequestMapping("/")
public class HealthCheckController {

    @RequestMapping(value = "/hs", method = RequestMethod.GET)
    public void _health_check(@Context final HttpServletResponse response) throws IOException {
        OutputStream outputStream = response.getOutputStream();//获取OutputStream输出流
        response.setHeader("content-type", "text/html;charset=UTF-8");
        byte[] dataByteArr = "OK".getBytes("UTF-8");//将字符转换成字节数组，指定以UTF-8编码进行转换
        log.info("hs invoked Ok");
        outputStream.write(dataByteArr);//使用OutputStream流向客户端输出字节数组
    }

}
