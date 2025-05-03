package com.pp.shortlink.admin.remote;

import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pp.shortlink.admin.common.convention.result.Result;
import com.pp.shortlink.admin.remote.dto.req.RecycleBinSaveReqDTO;
import com.pp.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.pp.shortlink.admin.remote.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.pp.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.pp.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.pp.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.pp.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import org.springframework.web.bind.annotation.RequestParam;

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
     * 更新短链接
     *
     * @param requestParam 更新短链接请求参数
     */
    default void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        HttpUtil.createRequest(Method.PUT, "http://127.0.0.1:8001/api/short-link/v1/update")
                .body(JSON.toJSONString(requestParam))
                .contentType("application/json")
                .execute();

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


    /**
     * 根据 URL 获取标题
     *
     * @param url 目标网站地址
     * @return 网站标题
     */
    default Result<String> getTitleByUrl(@RequestParam("url") String url) {
        String resultStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/title?url=" + url);
        return JSON.parseObject(resultStr, new TypeReference<>() {
        });
    }

    /**
     * 保存回收站
     *
     * @param requestParam 请求参数
     * @return
     */
    default void saveRecycleBin(RecycleBinSaveReqDTO requestParam) {
        HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/save", JSON.toJSONString(requestParam));
    }

    /**
     * 分页查询回收站短链接
     *
     * @param requestParam 分页短链接请求参数
     * @return 查询短链接响应
     */
    default Result<IPage<ShortLinkPageRespDTO>> pageRecycleBinShortLink(ShortLinkPageReqDTO requestParam) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("gid", requestParam.getGid());
        resultMap.put("current", requestParam.getCurrent());
        resultMap.put("size", requestParam.getSize());
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/page", resultMap);
        return JSON.parseObject(resultPageStr, new TypeReference<>() {
        });
    }
}
