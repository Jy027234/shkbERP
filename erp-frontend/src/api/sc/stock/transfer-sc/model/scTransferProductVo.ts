export interface ScTransferBatchDetailVo {
  /**
   * 批次号
   */
  batchNumber: string;

  /**
   * 调拨数量
   */
  transferNum: number;

  /**
   * 备注
   */
  description: string;
}

export interface ScTransferSerialDetailVo {
  /**
   * 序列号
   */
  serialNumber: string;

  /**
   * 批次号（收货时在转入仓归属批次）
   */
  batchNumber: string;

  /**
   * 备注
   */
  description: string;
}

export interface ScTransferProductVo {
  /**
   * 航材ID
   */
  productId: string;

  /**
   * 调拨数量
   */
  transferNum: number;

  /**
   * 批次明细（批次管理商品逐批次指定库存）
   */
  batchDetails?: ScTransferBatchDetailVo[];

  /**
   * 序列号明细（序列号管理商品逐序列号指定库存）
   */
  serialDetails?: ScTransferSerialDetailVo[];

  /**
   * 备注
   */
  description: string;
}
