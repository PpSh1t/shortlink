package com.pp.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pp.shortlink.admin.dao.entity.UserDO;
import com.pp.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.pp.shortlink.admin.dto.resp.UserRespDTO;

/**
 * 用户接口层
 */
public interface UserService extends IService<UserDO> {

    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户返回实体
     */
    UserRespDTO getUserByUsername(String username);

    /**
     * 查询用户是否存在
     * @param username  用户名
     * @return  用户存在返回True，不存在返回False
     */
    Boolean availableUsername(String username);

    /**
     * 注册用户
     * @param requestParam 注册用户请求参数
     */
    void register(UserRegisterReqDTO requestParam);





}
