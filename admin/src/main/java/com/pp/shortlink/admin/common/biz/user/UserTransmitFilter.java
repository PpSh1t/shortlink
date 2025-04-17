package com.pp.shortlink.admin.common.biz.user;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.pp.shortlink.admin.common.convention.Exception.ClientException;
import com.pp.shortlink.admin.common.convention.result.Results;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static com.pp.shortlink.admin.common.enums.UserErrorCodeEnum.USER_TOKEN_FAIL;

/**
 * 用户信息传输过滤器
 */
@RequiredArgsConstructor
public class UserTransmitFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;

    private final List<String> IGNORE_URI = Lists.newArrayList(
            "/api/short-link/admin/v1/user/login",
            "/api/short-link/admin/v1/user/availableUsername"
    );

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String requestURI = httpServletRequest.getRequestURI();
        String method = httpServletRequest.getMethod();

        // 特殊处理注册接口：POST /api/short-link/admin/v1/user
        boolean isRegisterRequest = requestURI.equals("/api/short-link/admin/v1/user") && "POST".equalsIgnoreCase(method);

        // 非白名单接口且非注册接口执行 token 校验
        if (!IGNORE_URI.contains(requestURI) && !isRegisterRequest) {
            String username = httpServletRequest.getHeader("username");
            String token = httpServletRequest.getHeader("token");

            // ❗ 如果 username 或 token 是空的，直接拦截
            if (StrUtil.isBlank(username) || StrUtil.isBlank(token)) {
                returnJson((HttpServletResponse) servletResponse,
                        JSON.toJSONString(Results.failure(new ClientException(USER_TOKEN_FAIL))));
                return;
            }

            try {
                Object userInfoJsonStr = stringRedisTemplate.opsForHash().get("login_" + username, token);
                if (userInfoJsonStr == null) {
                    throw new ClientException(USER_TOKEN_FAIL);
                }
                UserInfoDTO userInfoDTO = JSON.parseObject(userInfoJsonStr.toString(), UserInfoDTO.class);
                UserContext.setUser(userInfoDTO);
            } catch (Exception e) {
                returnJson((HttpServletResponse) servletResponse,
                        JSON.toJSONString(Results.failure(new ClientException(USER_TOKEN_FAIL))));
                return;
            }
        }

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }
    }

    private void returnJson(HttpServletResponse response, String json) throws Exception {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(json);
        } catch (IOException e) {
            // 可以打个日志，这里就省了
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}