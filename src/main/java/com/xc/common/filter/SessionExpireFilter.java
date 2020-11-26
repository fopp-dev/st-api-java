package com.xc.common.filter;


import com.xc.utils.PropertiesUtil;
import com.xc.utils.redis.CookieUtils;
import com.xc.utils.redis.RedisShardedPoolUtils;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class
SessionExpireFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(SessionExpireFilter.class);

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String userLoginToken = CookieUtils.readLoginToken(httpServletRequest,
                PropertiesUtil.getProperty("user.cookie.name"));
        if (StringUtils.isNotEmpty(userLoginToken)) {
            String userjsonstr = RedisShardedPoolUtils.get(userLoginToken);
            if (StringUtils.isNotEmpty(userjsonstr)) {
                RedisShardedPoolUtils.expire(userLoginToken, 5400);
            }
        }
        String agentLoginToken = CookieUtils.readLoginToken(httpServletRequest,
                PropertiesUtil.getProperty("agent.cookie.name"));
        if (StringUtils.isNotEmpty(agentLoginToken)) {

            String agentJsonStr = RedisShardedPoolUtils.get(agentLoginToken);
            if (StringUtils.isNotEmpty(agentJsonStr)) {
                RedisShardedPoolUtils.expire(agentLoginToken, 5400);
            }
        }
        String adminLoginToken = CookieUtils.readLoginToken(httpServletRequest,
                PropertiesUtil.getProperty("admin.cookie.name"));
        if (StringUtils.isNotEmpty(adminLoginToken)) {
            String adminJsonStr = RedisShardedPoolUtils.get(adminLoginToken);
            if (StringUtils.isNotEmpty(adminJsonStr)) {
                RedisShardedPoolUtils.expire(adminLoginToken, 5400);
            }
        }
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        String origin = req.getHeader("Origin");
        if (origin == null) {
            origin = req.getHeader("Referer");
        }
        resp.setHeader("Access-Control-Allow-Origin", origin);
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {
    }
}
