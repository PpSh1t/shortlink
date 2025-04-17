package com.pp.shortlink.project.dto.resp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.Date;


/**
 * 短链接分页返回参数
 */
@Data
public class ShortLinkPageRespDTO extends Page {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 域名
     */
    @TableField(value = "domain")
    private String domain;

    /**
     * 短链接
     */
    @TableField(value = "short_uri")
    private String shortUri;

    /**
     * 完整短链接
     */
    @TableField(value = "full_short_url")
    private String fullShortUrl;

    /**
     * 原始链接
     */
    @TableField(value = "origin_url")
    private String originUrl;

    /**
     * 分组标识
     */
    @TableField(value = "gid")
    private String gid;

    /**
     * 有效期类型 0：永久有效 1：用户自定义
     */
    @TableField(value = "valid_date_type")
    private Integer validDateType;

    /**
     * 有效期
     */
    @TableField(value = "valid_date")
    private Date validDate;

    /**
     * 描述
     */
    @TableField(value = "`describe`")
    private String describe;

    /**
     * 网站标识
     */
    private String favicon;

}
