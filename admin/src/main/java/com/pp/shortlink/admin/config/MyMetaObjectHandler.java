package com.pp.shortlink.admin.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/*
MyBatis-Plus 提供了一个便捷的自动填充功能，用于在插入或更新数据时自动填充某些字段，
如创建时间、更新时间等。以下是如何使用这一功能的详细说明。

原理概述
自动填充功能通过实现 com.baomidou.mybatisplus.core.handlers.MetaObjectHandler 接口来实现。
你需要创建一个类来实现这个接口，并在其中定义插入和更新时的填充逻辑。

使用步骤
1. 定义实体类
在实体类中，你需要使用 @TableField 注解来标记哪些字段需要自动填充，并指定填充的策略。
2. 实现 MetaObjectHandler
创建一个类来实现 MetaObjectHandler 接口，并重写 insertFill 和 updateFill 方法。
3. 配置自动填充处理器
确保你的 MyMetaObjectHandler 类被 Spring 管理，可以通过 @Component 或 @Bean 注解来实现。
*/

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", Date::new, Date.class);
        this.strictInsertFill(metaObject, "updateTime", Date::new, Date.class);
        this.strictInsertFill(metaObject, "delFlag", () -> 0, Integer.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", Date::new, Date.class);
    }
}
