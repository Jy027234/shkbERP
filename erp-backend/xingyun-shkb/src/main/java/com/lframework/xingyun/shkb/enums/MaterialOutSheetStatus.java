package com.lframework.xingyun.shkb.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.lframework.starter.web.core.enums.BaseEnum;

/**
 * 发料出库单状态
 */
public enum MaterialOutSheetStatus implements BaseEnum<Integer> {
    // 0：备料中
    PREPARING(0, "备料中"),
    // 1：已发料
    ISSUED(1, "已发料"),
    // 2：可领料
    PICKABLE(2, "可领料");

    @EnumValue
    private final Integer code;

    private final String desc;

    MaterialOutSheetStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }
}
