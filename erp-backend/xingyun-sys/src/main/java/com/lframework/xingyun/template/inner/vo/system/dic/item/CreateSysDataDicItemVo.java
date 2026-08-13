package com.lframework.xingyun.template.inner.vo.system.dic.item;

import com.lframework.starter.web.core.components.validation.IsCode;
import com.lframework.starter.web.core.vo.BaseVo;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSysDataDicItemVo implements BaseVo, Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 编号
   */
  @ApiModelProperty(value = "编号", required = true)
  @NotBlank(message = "请输入编号！")
  @IsCode
  private String code;

  /**
   * 名称
   */
  @ApiModelProperty(value = "名称", required = true)
  @NotBlank(message = "请输入名称！")
  private String name;

  /**
   * 字典ID
   */
  @ApiModelProperty("字典ID")
  @NotBlank(message = "字典ID不能为空！")
  private String dicId;

  /**
   * 排序
   */
  @ApiModelProperty("排序")
  @NotNull(message = "请输入排序！")
  private Integer orderNo;
}
