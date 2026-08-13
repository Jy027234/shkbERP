package com.lframework.xingyun.shkb.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.lframework.starter.web.core.enums.BaseEnum;

/**
 * 任务类型
 */

public enum TaskType implements BaseEnum<String> {
    AVIATION("AVIATION", "民航维修任务"),
    RECEIVE_WB("RECEIVE_WB", "返厂WB任务"),
    RECEIVE_L("RECEIVE_L", "返厂L任务");

    @EnumValue
    private final String code;

    private final String desc;

    TaskType(String code, String desc) {

        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {

        return this.code;
    }

    @Override
    public String getDesc() {

        return this.desc;
    }
}
