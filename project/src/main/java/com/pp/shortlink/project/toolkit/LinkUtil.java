package com.pp.shortlink.project.toolkit;


import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

import java.util.Date;
import java.util.Optional;

import static com.pp.shortlink.project.common.constant.ShortLinkConstant.DEFAULT_CACHE_VALID_TIME;

/**
 * 短链接工具类
 */
public class LinkUtil {


    /**
     * 根据一个有效期时间 validDate，计算当前时间到这个时间之间的毫秒数，
     * 也就是缓存还应该存在多久。
     * 如果传进来的 validDate 是 null，就返回一个默认的缓存时间。
     * @param validDate 有效期时间
     * @return 当前时间到这个时间之间的毫秒数
     */
    public static long getLinkCacheValidTime(Date validDate) {
        return Optional.ofNullable(validDate)
                .map(each -> DateUtil.between(new Date(),each, DateUnit.MS ))
                .orElse(DEFAULT_CACHE_VALID_TIME);
    }
}
