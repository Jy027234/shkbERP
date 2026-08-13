package com.lframework.xingyun.shkb.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.lframework.starter.web.core.enums.BaseEnum;

/**
 * 合同任务状态
 */
    public enum TaskStatus implements BaseEnum<String> {
    WAIT_EVALUATION("WAIT_EVALUATION", "待技术评估"),
    WAIT_DISPATCH("WAIT_DISPATCH", "待派发"),
    EXECUTION("EXECUTION", "维修执行"),
    RETURNED("RETURNED", "任务退修"),
    // 任务关闭
    CLOSED("CLOSED", "任务关闭");



    @EnumValue
    private final String code;

    private final String desc;

    TaskStatus(String code, String desc) {

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
