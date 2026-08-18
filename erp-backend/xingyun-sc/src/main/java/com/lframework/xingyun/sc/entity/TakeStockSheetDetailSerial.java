package com.lframework.xingyun.sc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lframework.starter.web.core.dto.BaseDto;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 盘点单序列号明细（一条序列号一条明细）
 */
@Data
@TableName("tbl_take_stock_sheet_detail_serial")
public class TakeStockSheetDetailSerial implements BaseDto {

  private static final long serialVersionUID = 1L;

  /**
   * ID
   */
  private String id;

  /**
   * 盘点单ID
   */
  private String sheetId;

  /**
   * 盘点单明细ID
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
   * 批次号（盘盈序列号归属批次）
   */
  private String batchNumber;

  /**
   * 实盘状态：1实盘在库、0实盘缺失
   */
  private Integer takeStatus;

  /**
   * 备注
   */
  private String description;

  /**
   * 创建时间
   */
  private LocalDateTime createTime;
}
