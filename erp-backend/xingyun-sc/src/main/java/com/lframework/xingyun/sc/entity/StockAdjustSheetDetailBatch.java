package com.lframework.xingyun.sc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lframework.starter.web.core.dto.BaseDto;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 库存调整单批次明细
 */
@Data
@TableName("tbl_stock_adjust_sheet_detail_batch")
public class StockAdjustSheetDetailBatch implements BaseDto {

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
   * 批次号
   */
  private String batchNumber;

  /**
   * 调整数量（入库/出库均为正数，方向由调整单业务类型决定）
   */
  private Integer stockNum;

  /**
   * 备注
   */
  private String description;

  /**
   * 创建时间
   */
  private LocalDateTime createTime;
}
