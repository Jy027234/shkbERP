package com.lframework.xingyun.sc.vo.stock.take.sheet;

import com.lframework.starter.web.core.components.validation.TypeMismatch;
import com.lframework.starter.web.core.vo.BaseVo;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 盘点单批次明细VO（逐批次录入）
 */
@Data
public class TakeStockBatchDetailVo implements BaseVo, Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 批次号
   */
  @ApiModelProperty(value = "批次号", required = true)
  @NotBlank(message = "批次号不能为空！")
  private String batchNumber;

  /**
   * 实盘数量
   */
  @ApiModelProperty(value = "实盘数量", required = true)
  @NotNull(message = "实盘数量不能为空！")
  @TypeMismatch(message = "实盘数量格式有误！")
  private Integer takeNum;

  /**
   * 备注
   */
  @ApiModelProperty("备注")
  private String description;
}
