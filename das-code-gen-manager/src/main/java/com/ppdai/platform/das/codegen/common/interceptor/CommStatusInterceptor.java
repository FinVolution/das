package com.ppdai.platform.das.codegen.common.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by liang.wang on 2018/8/28.
 * 404,500等拦截
 */
@Slf4j
@Component
public class CommStatusInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (null == modelAndView) {
            modelAndView = new ModelAndView();
        }
        switch (response.getStatus()) {
            case 404:
                modelAndView.setViewName("err/404/index.html");
                break;
            case 500:
                modelAndView.setViewName("err/500/index.html");
                break;
        }
    }
}
