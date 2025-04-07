package com.pp.shortlink.admin.toolkit;

import java.util.Random;

/**
 * 分组id随机生成器
 */
public final class RandomCodeGenerator {

    // 所有可用字符：0-9, A-Z, a-z
    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 6;
    private static final Random RANDOM = new Random();

    /**
     * 生成一个包含数字和字母的6位随机字符串
     * @return 6位随机字符串
     */
    public static String generateRandomCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = RANDOM.nextInt(CHAR_POOL.length());
            code.append(CHAR_POOL.charAt(index));
        }
        return code.toString();
    }

}
