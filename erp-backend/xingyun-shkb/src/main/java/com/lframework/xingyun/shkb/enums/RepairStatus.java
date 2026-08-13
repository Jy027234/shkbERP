package com.lframework.xingyun.shkb.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.lframework.starter.web.core.enums.BaseEnum;

/**
 * 维修状态
 */
public enum RepairStatus implements BaseEnum<String> {
    WAIT_CHECK("WAIT_CHECK", "待检查"),
    CHECKING("CHECKING", "检查中"),
    REPAIRING("REPAIRING", "维修中"),
    WAITING_FOR_PARTS("WAITING_FOR_PARTS", "等料暂停"),
    PAUSED_OTHER("PAUSED_OTHER", "其他暂停"),
    WAIT_ASSEMBLY("WAIT_ASSEMBLY", "待装配"),
    WAITING_FOR_REPAIR("TESTING", "测试中"),
    COMPLETED("COMPLETED", "完工");

    @EnumValue
    private final String code;

    private final String desc;

    RepairStatus(String code, String desc) {

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
