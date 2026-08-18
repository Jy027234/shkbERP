package com.lframework.xingyun.sc.vo.stock.transfer;

import com.lframework.starter.web.core.vo.BaseVo;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 仓库调拨单序列号明细VO（一条序列号一条明细）
 */
@Data
public class ScTransferSerialDetailVo implements BaseVo, Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 序列号
   */
  @ApiModelProperty(value = "序列号", required = true)
  @NotBlank(message = "序列号不能为空！")
  private String serialNumber;

  /**
   * 批次号（收货时在转入仓归属批次）
   */
  @ApiModelProperty("批次号")
  private String batchNumber;

  /**
   * 备注
   */
  @ApiModelProperty("备注")
  private String description;
}
