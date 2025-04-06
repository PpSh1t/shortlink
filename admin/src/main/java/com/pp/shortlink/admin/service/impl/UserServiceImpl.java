package com.pp.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pp.shortlink.admin.common.convention.Exception.ClientException;
import com.pp.shortlink.admin.common.enums.UserErrorCodeEnum;
import com.pp.shortlink.admin.dao.entity.UserDO;
import com.pp.shortlink.admin.dao.mapper.UserMapper;
import com.pp.shortlink.admin.dto.req.UserLoginReqDTO;
import com.pp.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.pp.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.pp.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.pp.shortlink.admin.dto.resp.UserRespDTO;
import com.pp.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.pp.shortlink.admin.common.constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;
import static com.pp.shortlink.admin.common.enums.UserErrorCodeEnum.USER_NAME_EXIST;
import static com.pp.shortlink.admin.common.enums.UserErrorCodeEnum.USER_SAVE_ERROR;

/**
 * 用户接口实现层
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;

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

        //使用 Redisson 提供的分布式锁 RLock，以防止高并发情况下重复插入相同用户名。
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY + requestParam.getUsername());

        try {
             /*
                使用 Redisson 的 tryLock 方法尝试获取锁
                如果获取成功，则继续执行插入数据库插入操作
                如果锁未成功获取（可能由于其他线程已持有锁），可能会导致注册失败或超时
            */
            if (lock.tryLock()) {
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
                return;
            }
            //没有获取到锁，则返回用户名已存在
            throw new ClientException(USER_NAME_EXIST);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void update(UserUpdateReqDTO requestParam) {

        //TODO 验证当前用户是否为登录用户

        //构建更新条件
        LambdaUpdateWrapper<UserDO> updateWrapper = Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername());

        //执行更新操作
        baseMapper.update(BeanUtil.toBean(requestParam, UserDO.class), updateWrapper);

    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        //取出用户名、密码与请求参数相同未注销的用户
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername())
                .eq(UserDO::getPassword, requestParam.getPassword())
                .eq(UserDO::getDelFlag, 0);
        UserDO userDO = baseMapper.selectOne(queryWrapper);

        //验证用户名是否存在
        if (userDO == null) {
            //不存在
            throw new ClientException(UserErrorCodeEnum.USER_NULL);
        }

        //检查用户是否已登录
        //检查 Redis 中是否存在以 "login_" + 用户名 为键的记录。
        //如果记录存在，说明用户已经登录。
        Boolean hasLogin = stringRedisTemplate.hasKey("login_" + requestParam.getUsername());
        if (hasLogin != null && hasLogin) {
            throw new ClientException("用户已登录");
        }

        //生成一个唯一的会话标识（uuid），用于标识当前用户的登录会话。
        String uuid = UUID.randomUUID().toString();
        //存储用户信息到 Redis（使用 Hash 结构）
        //使用 Redis 的 Hash 数据结构存储用户信息。
        //键为 "login_" + 用户名。
        //字段名为 uuid，值为用户信息的 JSON 字符串。
        stringRedisTemplate.opsForHash().put("login_" + requestParam.getUsername(), uuid, JSON.toJSONString(userDO));

        //设置 Redis 键的过期时间为 30 分钟。
        stringRedisTemplate.expire("login_" + requestParam.getUsername(), 30L, TimeUnit.MINUTES);

        //将生成的 uuid 封装到响应对象中，返回给调用方。
        return new UserLoginRespDTO(uuid);
    }

    @Override
    public Boolean checkLogin(String username, String token) {
        //通过 Redis 的 Hash 数据结构，检查 "login_" + 用户名 键中是否存在对应的 token 字段。
        return stringRedisTemplate.opsForHash().get("login_" + username, token) != null;
    }

    @Override
    public void logout(String username, String token) {
        //验证用户是否已登录
        if (checkLogin(username, token)) {
            stringRedisTemplate.delete("login_" + username);
            return;
        }
        throw new ClientException("用户Token不存在或未登录");

    }
}
