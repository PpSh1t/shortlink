package com.pp.shortlink.admin.dto.req;

import lombok.Data;

/**
 * 短链接修改请求参数
 */
@Data
public class ShortLinkGroupUpdateReqDTO {
    /**
     * 分组名称
     */
    private String name;

    /**
     * 分组标识
     */
    private String gid;

}
