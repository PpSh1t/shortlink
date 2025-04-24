package com.pp.shortlink.admin.remote;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pp.shortlink.admin.common.convention.result.Result;
import com.pp.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.pp.shortlink.admin.remote.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.pp.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.pp.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.pp.shortlink.project.dto.resp.ShortLinkCreateRespDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短链接中台远程调用服务
 */
public interface ShortLinkRemoteService {
    /**
     * 创建短链接
     *
     * @param requestParam 创建短链接请求参数
     * @return 短链接创建响应
     */
    default Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateReqDTO requestParam) {
        String resultBodyStr = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/create", JSON.toJSONString(requestParam));
        return JSON.parseObject(resultBodyStr, new TypeReference<>() {
        });

    }

    /**
     * 分页查询短链接
     *
     * @param requestParam 分页短链接请求参数
     * @return 查询短链接响应
     */
    default Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("gid", requestParam.getGid());
        resultMap.put("current", requestParam.getCurrent());
        resultMap.put("size", requestParam.getSize());
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/page", resultMap);
        return JSON.parseObject(resultPageStr, new TypeReference<>() {
        });
    }

    /**
     * 查询分组短链接总量
     *
     * @param requestParam 分组短链接总量请求参数
     * @return 查询分组短链接总量响应
     */
    default Result<List<ShortLinkGroupCountQueryRespDTO>> listShortLinkGroupCount(List<String> requestParam) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("requestParam", requestParam);
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/count", resultMap);
        return JSON.parseObject(resultPageStr, new TypeReference<>() {
        });
    }
}
