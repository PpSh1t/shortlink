package com.pp.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pp.shortlink.admin.common.convention.Exception.ClientException;
import com.pp.shortlink.admin.common.enums.UserErrorCodeEnum;
import com.pp.shortlink.admin.dao.entity.UserDO;
import com.pp.shortlink.admin.dao.mapper.UserMapper;
import com.pp.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.pp.shortlink.admin.dto.resp.UserRespDTO;
import com.pp.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import static com.pp.shortlink.admin.common.enums.UserErrorCodeEnum.USER_NAME_EXIST;
import static com.pp.shortlink.admin.common.enums.UserErrorCodeEnum.USER_SAVE_ERROR;

/**
 * 用户接口实现层
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;

    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if (userDO == null) {
            throw new ClientException(UserErrorCodeEnum.USER_NULL);
        }
        UserRespDTO result = new UserRespDTO();

        if (userDO != null) {
            BeanUtils.copyProperties(userDO, result);
            return result;
        } else {
            return null;
        }
    }

    @Override
    public Boolean availableUsername(String username) {
        return !userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    public void register(UserRegisterReqDTO requestParam) {
        //判断用户名是否可用
        if (!availableUsername(requestParam.getUsername())) {
            //不可用,返回用户已存在错误
            throw new ClientException(USER_NAME_EXIST);
        }

        /*
        将 requestParam 转换为一个 UserDO 类型的对象，
        然后通过 baseMapper.insert 方法将该对象插入到数据库中，
        并返回插入操作的结果(如果插入成功，返回值一般为 1)
        */
        int inserted = baseMapper.insert(BeanUtil.toBean(requestParam, UserDO.class));
        if (inserted < 1) {
            //插入不成功，返回注册失败错误
            throw new ClientException(USER_SAVE_ERROR);
        }
        //当前注册用户的用户名（requestParam.getUsername()）添加到布隆过滤器 userRegisterCachePenetrationBloomFilter 中
        userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());

    }

}
