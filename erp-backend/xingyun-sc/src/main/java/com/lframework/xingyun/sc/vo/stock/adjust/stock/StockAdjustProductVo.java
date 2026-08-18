package com.lframework.xingyun.sc.vo.stock.adjust.stock;

import com.lframework.starter.web.core.components.validation.TypeMismatch;
import com.lframework.starter.web.core.vo.BaseVo;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockAdjustProductVo implements BaseVo, Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 商品ID
   */
  @ApiModelProperty(value = "商品ID", required = true)
  @NotBlank(message = "商品ID不能为空！")
  private String productId;

  /**
   * 调整库存数量
   */
  @ApiModelProperty(value = "调整库存数量", required = true)
  @NotNull(message = "调整库存数量不能为空！")
  @TypeMismatch(message = "调整库存数量格式有误！")
  private Integer stockNum;

  /**
   * 批次明细（批次管理商品逐批次指定）
   */
  @ApiModelProperty("批次明细")
  @Valid
  private List<StockAdjustBatchDetailVo> batchDetails;

  /**
   * 序列号明细（序列号管理商品逐序列号指定，一条序列号一条明细）
   */
  @ApiModelProperty("序列号明细")
  @Valid
  private List<StockAdjustSerialDetailVo> serialDetails;

  /**
   * 备注
   */
  @ApiModelProperty("备注")
  private String description;
}
