package com.lframework.xingyun.sc.vo.stock.take.sheet;

import com.lframework.starter.web.core.components.validation.TypeMismatch;
import com.lframework.starter.web.core.vo.BaseVo;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 盘点单序列号明细VO（一条序列号一条明细）
 */
@Data
public class TakeStockSerialDetailVo implements BaseVo, Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 序列号
   */
  @ApiModelProperty(value = "序列号", required = true)
  @NotBlank(message = "序列号不能为空！")
  private String serialNumber;

  /**
   * 批次号（盘盈序列号归属批次）
   */
  @ApiModelProperty("批次号")
  private String batchNumber;

  /**
   * 实盘状态：1实盘在库、0实盘缺失
   */
  @ApiModelProperty(value = "实盘状态：1实盘在库、0实盘缺失", required = true)
  @NotNull(message = "实盘状态不能为空！")
  @TypeMismatch(message = "实盘状态格式有误！")
  private Integer takeStatus;

  /**
   * 备注
   */
  @ApiModelProperty("备注")
  private String description;
}
