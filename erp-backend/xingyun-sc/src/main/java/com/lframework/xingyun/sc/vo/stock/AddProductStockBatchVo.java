package com.lframework.xingyun.sc.vo.stock;

import com.lframework.starter.web.core.components.validation.IsEnum;
import com.lframework.starter.web.core.vo.BaseVo;
import com.lframework.xingyun.sc.enums.ProductStockBizType;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 批次库存增加VO（用于盘盈等批次入库）
 */
@Data
public class AddProductStockBatchVo implements BaseVo, Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 商品ID
   */
  @ApiModelProperty(value = "商品ID", required = true)
  @NotBlank(message = "商品ID不能为空！")
  private String productId;

  /**
   * 仓库ID
   */
  @ApiModelProperty(value = "仓库ID", required = true)
  @NotBlank(message = "仓库ID不能为空！")
  private String scId;

  /**
   * 批次库存ID
   */
  @ApiModelProperty(value = "批次库存ID", required = true)
  @NotBlank(message = "批次库存ID不能为空！")
  private String stockBatchId;

  /**
   * 入库数量
   */
  @ApiModelProperty(value = "入库数量", required = true)
  @NotNull(message = "入库数量不能为空！")
  @Min(message = "入库数量必须大于0！", value = 1)
  private Integer stockNum;

  /**
   * 含税成本总金额
   */
  @ApiModelProperty("含税成本总金额")
  @Min(message = "含税成本总金额不能小于0！", value = 0)
  private BigDecimal taxAmount;

  /**
   * 入库时间
   */
  @ApiModelProperty("入库时间")
  private LocalDateTime createTime = LocalDateTime.now();

  /**
   * 业务单据ID
   */
  @ApiModelProperty("业务单据ID")
  private String bizId;

  /**
   * 业务单据明细ID
   */
  @ApiModelProperty("业务单据明细ID")
  private String bizDetailId;

  /**
   * 业务单据号
   */
  @ApiModelProperty("业务单据号")
  private String bizCode;

  /**
   * 业务类型
   */
  @ApiModelProperty(value = "业务类型", required = true)
  @NotNull(message = "业务类型不能为空！")
  @IsEnum(message = "业务类型不正确！", enumClass = ProductStockBizType.class)
  private Integer bizType;
}
