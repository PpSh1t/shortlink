package com.pp.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pp.shortlink.admin.common.biz.user.UserContext;
import com.pp.shortlink.admin.dao.entity.GroupDO;
import com.pp.shortlink.admin.dao.mapper.GroupMapper;
import com.pp.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import com.pp.shortlink.admin.service.GroupService;
import com.pp.shortlink.admin.toolkit.RandomCodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
                .sortOrder(0)
                .username(UserContext.getUsername())
                .build();
        baseMapper.insert(groupDO);
    }

    @Override
    public List<ShortLinkGroupSaveReqDTO> listGroup() {
        System.out.println("当前查询的用户名是: " + UserContext.getUsername());
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getDelFlag, 0)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime);
        List<GroupDO> groupDOList = baseMapper.selectList(queryWrapper);
        return BeanUtil.copyToList(groupDOList, ShortLinkGroupSaveReqDTO.class);
    }

    private boolean availableGid(String gid) {
        //生成六位数字字母随机数作为gid
        gid = RandomCodeGenerator.generateRandomCode();
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                //TODO 设置用户名
                .eq(GroupDO::getUsername, UserContext.getUsername());

        GroupDO availableGroupFlag = baseMapper.selectOne(queryWrapper);
        return availableGroupFlag == null;
    }
}
