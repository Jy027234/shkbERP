export interface StockAdjustBatchDetailVo {
  /**
   * 批次号
   */
  batchNumber: string;

  /**
   * 调整数量（入库/出库均为正数）
   */
  stockNum: number;

  /**
   * 备注
   */
  description: string;
}

export interface StockAdjustSerialDetailVo {
  /**
   * 序列号
   */
  serialNumber: string;

  /**
   * 批次号（入库序列号归属批次）
   */
  batchNumber: string;

  /**
   * 备注
   */
  description: string;
}

export interface StockAdjustProductVo {
  /**
   * 航材ID
   */
  productId: string;

  /**
   * 调整库存数量
   */
  stockNum: number;

  /**
   * 批次明细（批次管理商品逐批次指定）
   */
  batchDetails?: StockAdjustBatchDetailVo[];

  /**
   * 序列号明细（序列号管理商品逐序列号指定）
   */
  serialDetails?: StockAdjustSerialDetailVo[];

  /**
   * 备注
   */
  description: string;
}
