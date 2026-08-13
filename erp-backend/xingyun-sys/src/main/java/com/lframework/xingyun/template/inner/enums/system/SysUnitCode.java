package com.lframework.xingyun.template.inner.enums.system;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.lframework.starter.web.core.enums.BaseEnum;

public enum SysUnitCode implements BaseEnum<String> {
  SHKB("SHKB", "SHKB"), BMBJ("BMBJ", "BMBJ"), BMZD("BMZD", "BMZD"),SHZD("SHZD", "SHZD");

  @EnumValue
  private final String code;

  private final String desc;

  SysUnitCode(String code, String desc) {

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
