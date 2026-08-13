package com.lframework.xingyun.shkb.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.lframework.starter.web.core.enums.BaseEnum;

/**
 * 合同状态
 */

public enum ContractStatus implements BaseEnum<Integer> {
    WAIT_CREATE(0, "待生成合同任务"),
    TASK_EXECUTING(1, "任务执行中"),
    CONTRACT_CLOSE(2, "合同关闭"),
    TASK_RETURN(3, "任务退修"),
    TASK_CLOSE(4, "任务关闭"),
    WAIT_DELIVERY(5, "待交付")
    ;
    @EnumValue
    private final Integer code;

    private final String desc;

    ContractStatus(Integer code, String desc) {

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
