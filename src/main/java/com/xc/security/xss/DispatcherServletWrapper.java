package com.xc.security.xss;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.method.HandlerMethod;

import org.springframework.web.servlet.DispatcherServlet;

import org.springframework.web.servlet.HandlerExecutionChain;

public class DispatcherServletWrapper extends DispatcherServlet {

    protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        HandlerExecutionChain chain = super.getHandler(request);
        Object handler = chain.getHandler();
        if (!(handler instanceof HandlerMethod)) {
            return chain;
        }
        HandlerMethod hm = (HandlerMethod) handler;
        if (!hm.getBeanType().isAnnotationPresent(org.springframework.stereotype.Controller.class)) {
            return chain;
        }
        return new HandlerExecutionChainWrapper(chain, request, getWebApplicationContext());
    }
}