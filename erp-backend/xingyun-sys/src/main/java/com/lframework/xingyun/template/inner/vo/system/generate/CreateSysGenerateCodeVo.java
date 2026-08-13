package com.lframework.xingyun.template.inner.vo.system.generate;

import com.lframework.starter.web.core.components.validation.IsJsonArray;
import com.lframework.starter.web.core.vo.BaseVo;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSysGenerateCodeVo implements BaseVo, Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 规则ID
   */
  @ApiModelProperty(value = "规则ID", required = true)
  @NotNull(message = "请输入规则ID！")
  private Integer id;

  /**
   * 名称
   */
  @ApiModelProperty(value = "名称", required = true)
  @NotBlank(message = "请输入名称！")
  private String name;
}
