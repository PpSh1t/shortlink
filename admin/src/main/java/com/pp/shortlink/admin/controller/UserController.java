package com.pp.shortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.pp.shortlink.admin.common.convention.result.Result;
import com.pp.shortlink.admin.common.convention.result.Results;
import com.pp.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.pp.shortlink.admin.dto.resp.UserActualRespDTO;
import com.pp.shortlink.admin.dto.resp.UserRespDTO;
import com.pp.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制层
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 根据用户名查询用户信息
     *
     */
    @GetMapping("/api/short-link/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username) {
        return Results.success(userService.getUserByUsername(username));
    }

    /**
     * 根据用户名查询用户真实信息
     *
     */
    @GetMapping("/api/short-link/v1/actual/user/{username}")
    public Result<UserActualRespDTO> getActualUserByUsername(@PathVariable("username") String username) {
        return Results.success(BeanUtil.toBean(userService.getUserByUsername(username), UserActualRespDTO.class));
    }


    /**
     * 查询用户名是否可用
     *
     */
    @GetMapping("/api/short-link/v1/user/availableUsername")
    public Result<Boolean> availableUsername(@RequestParam("username") String username) {
        return Results.success(userService.availableUsername(username));
    }

    /**
     * 注册用户
     */
    @PostMapping("/api/short-link/v1/user")
    public Result<Void> register(@RequestBody UserRegisterReqDTO requestParam) {
        userService.register(requestParam);
        return Results.success();
    }

}
