package com.pp.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pp.shortlink.admin.common.convention.result.Result;
import com.pp.shortlink.admin.common.convention.result.Results;
import com.pp.shortlink.admin.remote.ShortLinkRemoteService;
import com.pp.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.pp.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.pp.shortlink.admin.remote.dto.req.ShortLinkUpdateReqDTO;
import com.pp.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.pp.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 短链接后管控制层
 */
@RequiredArgsConstructor
@RestController
public class ShortLinkController {

    /**
     * 后续重构为SpringCloud Feign 调用
     */
    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

    /**
     * 新增短链接
     *
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/admin/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return shortLinkRemoteService.createShortLink(requestParam);
    }

    /**
     * 修改短链接
     *  "validDateType":1,
     *  "validDate":"2025-01-01 00:00:00",
     *  "describe":"123123"
     */
    @PutMapping("/api/short-link/admin/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam) {
        shortLinkRemoteService.updateShortLink(requestParam);
        return Results.success();
    }

    /**
     * 分页查询短链接
     * @param requestParam
     * @return
     */
    @GetMapping("/api/short-link/admin/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> shortLinkPage(ShortLinkPageReqDTO requestParam) {
        return shortLinkRemoteService.pageShortLink(requestParam);
    }

//    /**
//     * 分页查询回收站短链接
//     */
//    @GetMapping("/api/short-link/admin/v1/recycle-bin/page")
//    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
//      return shortLinkRemoteService.pageRecycleBinShortLink(requestParam);
//    }

}
