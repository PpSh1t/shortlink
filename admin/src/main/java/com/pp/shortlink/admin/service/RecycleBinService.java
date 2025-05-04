package com.pp.shortlink.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pp.shortlink.admin.common.convention.result.Result;
import com.pp.shortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.pp.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;

/**
 * URL 回收站接口层
 */
public interface RecycleBinService {
    /**
     * 分页查询回收站短链接
     * @param requestParam 请求参数
     * @return 返回参数
     */
    Result<IPage<ShortLinkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam);
}
