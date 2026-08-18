package com.lframework.xingyun.sc.vo.stock.adjust.stock;

import com.lframework.starter.web.core.components.validation.TypeMismatch;
import com.lframework.starter.web.core.vo.BaseVo;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 库存调整单批次明细VO（入库/出库均按批次指定，数量为正数）
 */
@Data
public class StockAdjustBatchDetailVo implements BaseVo, Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 批次号
   */
  @ApiModelProperty(value = "批次号", required = true)
  @NotBlank(message = "批次号不能为空！")
  private String batchNumber;

  /**
   * 调整数量（入库/出库均为正数）
   */
  @ApiModelProperty(value = "调整数量", required = true)
  @NotNull(message = "调整数量不能为空！")
  @TypeMismatch(message = "调整数量格式有误！")
  private Integer stockNum;

  /**
   * 备注
   */
  @ApiModelProperty("备注")
  private String description;
}
