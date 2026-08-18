package com.lframework.xingyun.sc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lframework.starter.web.core.dto.BaseDto;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 仓库调拨单批次明细（在途库存按未收数量单独记录）
 */
@Data
@TableName("tbl_sc_transfer_order_detail_batch")
public class ScTransferOrderDetailBatch implements BaseDto {

  private static final long serialVersionUID = 1L;

  /**
   * ID
   */
  private String id;

  /**
   * 仓库调拨单ID
   */
  private String orderId;

  /**
   * 仓库调拨单明细ID
   */
  private String orderDetailId;

  /**
   * 商品ID
   */
  private String productId;

  /**
   * 批次号
   */
  private String batchNumber;

  /**
   * 调拨数量
   */
  private Integer transferNum;

  /**
   * 已收货数量
   */
  private Integer receivedNum;

  /**
   * 备注
   */
  private String description;

  /**
   * 创建时间
   */
  private LocalDateTime createTime;
}
