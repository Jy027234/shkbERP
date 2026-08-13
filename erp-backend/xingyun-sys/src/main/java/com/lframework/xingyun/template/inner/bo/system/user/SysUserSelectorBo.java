package com.lframework.xingyun.template.inner.bo.system.user;

import com.lframework.xingyun.template.inner.entity.SysUser;
import com.lframework.starter.web.core.bo.BaseBo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SysUserSelectorBo extends BaseBo<SysUser> {

  /**
   * ID
   */
  @ApiModelProperty("ID")
  private String id;

  /**
   * 编号
   */
  @ApiModelProperty("编号")
  private String code;

  /**
   * 姓名
   */
  @ApiModelProperty("姓名")
  private String name;

  /**
   * 单位编码
   */
  @ApiModelProperty("单位编码")
  private String unitCode;


  /**
   * 状态
   */
  @ApiModelProperty("状态")
  private Boolean available;

  public SysUserSelectorBo() {

  }

  public SysUserSelectorBo(SysUser dto) {

    super(dto);
  }
}
