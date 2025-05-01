package com.pp.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短链接跳转实体
 */
@TableName(value ="t_link_goto")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShortLinkGotoDO {
    /**
     * ID
     */
    private long id;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

}

