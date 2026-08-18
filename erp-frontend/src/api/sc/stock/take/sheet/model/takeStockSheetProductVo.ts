export interface TakeStockBatchDetailVo {
  /**
   * 批次号
   */
  batchNumber: string;

  /**
   * 实盘数量
   */
  takeNum: number;

  /**
   * 备注
   */
  description: string;
}

export interface TakeStockSerialDetailVo {
  /**
   * 序列号
   */
  serialNumber: string;

  /**
   * 批次号（盘盈序列号归属批次）
   */
  batchNumber: string;

  /**
   * 实盘状态：1实盘在库、0实盘缺失
   */
  takeStatus: number;

  /**
   * 备注
   */
  description: string;
}

export interface TakeStockSheetProductVo {
  /**
   * 航材ID
   */
  productId: string;

  /**
   * 盘点数量
   */
  takeNum: number;

  /**
   * 批次明细（批次管理商品逐批次录入）
   */
  batchDetails?: TakeStockBatchDetailVo[];

  /**
   * 序列号明细（序列号管理商品逐序列号录入）
   */
  serialDetails?: TakeStockSerialDetailVo[];

  /**
   * 备注
   */
  description: string;
}
