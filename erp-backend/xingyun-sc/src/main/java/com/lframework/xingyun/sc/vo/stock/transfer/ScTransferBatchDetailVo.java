package com.lframework.xingyun.sc.vo.stock.transfer;

import com.lframework.starter.web.core.components.validation.TypeMismatch;
import com.lframework.starter.web.core.vo.BaseVo;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 仓库调拨单批次明细VO（逐批次指定库存）
 */
@Data
public class ScTransferBatchDetailVo implements BaseVo, Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 批次号
   */
  @ApiModelProperty(value = "批次号", required = true)
  @NotBlank(message = "批次号不能为空！")
  private String batchNumber;

  /**
   * 调拨数量
   */
  @ApiModelProperty(value = "调拨数量", required = true)
  @NotNull(message = "调拨数量不能为空！")
  @TypeMismatch(message = "调拨数量格式有误！")
  private Integer transferNum;

  /**
   * 备注
   */
  @ApiModelProperty("备注")
  private String description;
}
