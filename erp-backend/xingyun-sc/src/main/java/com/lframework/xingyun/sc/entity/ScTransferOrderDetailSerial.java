package com.lframework.xingyun.sc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lframework.starter.web.core.dto.BaseDto;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 仓库调拨单序列号明细（一条序列号一条明细，在途状态单独记录）
 */
@Data
@TableName("tbl_sc_transfer_order_detail_serial")
public class ScTransferOrderDetailSerial implements BaseDto {

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
   * 序列号
   */
  private String serialNumber;

  /**
   * 调拨状态：1在途、2已收货
   */
  private Integer transferStatus;

  /**
   * 批次号（收货时在转入仓归属批次）
   */
  private String batchNumber;

  /**
   * 备注
   */
  private String description;

  /**
   * 创建时间
   */
  private LocalDateTime createTime;
}
