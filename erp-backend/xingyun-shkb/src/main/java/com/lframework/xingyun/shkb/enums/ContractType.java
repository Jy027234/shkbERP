package com.lframework.xingyun.shkb.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.lframework.starter.web.core.enums.BaseEnum;

/**
 * 合同类型
 */
public enum ContractType  implements BaseEnum<Integer> {
    AVIATION(1, "民航维修合同"),
    RECEIVE_WB(2, "返厂WB合同"),
    RECEIVE_L(3, "返厂L合同");

    @EnumValue
    private final Integer code;

    private final String desc;

    ContractType(Integer code, String desc) {

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
    
    /**
     * 根据编码查找枚举
     * @param code 编码
     * @return 枚举
     */
    public static ContractType findByCode(Integer code) {
        if (code == null) {
            return null;
        }
        
        for (ContractType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        
        return null;
    }
}
