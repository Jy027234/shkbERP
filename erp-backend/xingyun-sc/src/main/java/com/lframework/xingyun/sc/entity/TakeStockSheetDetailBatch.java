package com.lframework.xingyun.sc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lframework.starter.web.core.dto.BaseDto;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 盘点单批次明细
 */
@Data
@TableName("tbl_take_stock_sheet_detail_batch")
public class TakeStockSheetDetailBatch implements BaseDto {

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
   * 批次号
   */
  private String batchNumber;

  /**
   * 系统批次库存数量（录入时快照）
   */
  private Integer stockNum;

  /**
   * 实盘数量
   */
  private Integer takeNum;

  /**
   * 备注
   */
  private String description;

  /**
   * 创建时间
   */
  private LocalDateTime createTime;
}
