package com.lframework.xingyun.sc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lframework.starter.web.core.dto.BaseDto;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 库存调整单序列号明细（一条序列号一条明细）
 */
@Data
@TableName("tbl_stock_adjust_sheet_detail_serial")
public class StockAdjustSheetDetailSerial implements BaseDto {

  private static final long serialVersionUID = 1L;

  /**
   * ID
   */
  private String id;

  /**
   * 库存调整单ID
   */
  private String sheetId;

  /**
   * 库存调整单明细ID
   */
  private String sheetDetailId;

  /**
   * 商品ID
   */
  private String productId;

  /**
   * 序列号
   */
  private String serialNumber;

  /**
   * 批次号（入库序列号归属批次）
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
