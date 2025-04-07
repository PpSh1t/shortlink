package com.pp.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pp.shortlink.admin.dao.entity.GroupDO;
import com.pp.shortlink.admin.dao.mapper.GroupMapper;
import com.pp.shortlink.admin.service.GroupService;
import com.pp.shortlink.admin.toolkit.RandomCodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    @Override
    public void saveGroup(String groupName) {
        String gid = null;
        do {
            gid = RandomCodeGenerator.generateRandomCode();

        } while (!availableGid(gid));

        GroupDO groupDO = GroupDO.builder()
                .gid(gid)
                .name(groupName)
                .build();
        baseMapper.insert(groupDO);
    }

    private boolean availableGid(String gid) {
        //生成六位数字字母随机数作为gid
        gid = RandomCodeGenerator.generateRandomCode();
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                //TODO 设置用户名
                .eq(GroupDO::getUsername, null);

        GroupDO availableGroupFlag = baseMapper.selectOne(queryWrapper);
        return availableGroupFlag == null;
    }
}
