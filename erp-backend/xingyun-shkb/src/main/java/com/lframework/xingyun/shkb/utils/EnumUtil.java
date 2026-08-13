package com.lframework.xingyun.shkb.utils;

import com.lframework.starter.web.core.enums.BaseEnum;

import java.io.Serializable;

/**
 * 枚举工具类
 */
public class EnumUtil {

    /**
     * 根据枚举编码获取枚举实例
     *
     * @param enumClass 枚举类
     * @param code      编码
     * @param <T>       枚举类型
     * @param <C>       编码类型
     * @return 枚举实例
     */
    public static <T extends Enum<T> & BaseEnum<C>, C extends Serializable> T getByCode(Class<T> enumClass, C code) {
        if (code == null) {
            return null;
        }

        T[] enumConstants = enumClass.getEnumConstants();
        for (T enumConstant : enumConstants) {
            if (enumConstant.getCode().equals(code)) {
                return enumConstant;
            }
        }
        return null;
    }
}
