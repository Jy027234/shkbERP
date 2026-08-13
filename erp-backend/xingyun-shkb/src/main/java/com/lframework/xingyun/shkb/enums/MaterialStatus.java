package com.lframework.xingyun.shkb.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.lframework.starter.web.core.enums.BaseEnum;
import java.util.ArrayList;
import java.util.List;

public enum MaterialStatus implements BaseEnum<String> {
  
  PENDING_PREPARATION("pending", "待备料"),
  PREPARING("preparing", "备料中"),
  PARTIAL_PICKED("partial", "部分领料"),
  COMPLETED("completed", "领料完成");

  @EnumValue
  private final String code;

  private final String desc;

  MaterialStatus(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  @Override
  public String getCode() {
    return code;
  }

  @Override
  public String getDesc() {
    return desc;
  }

  public static MaterialStatus getByCode(String code) {
    for (MaterialStatus status : values()) {
      if (status.code.equals(code)) {
        return status;
      }
    }
    return null;
  }

  public static List<MaterialStatus> getAll() {
    List<MaterialStatus> list = new ArrayList<>();
    for (MaterialStatus status : values()) {
      list.add(status);
    }
    return list;
  }
}