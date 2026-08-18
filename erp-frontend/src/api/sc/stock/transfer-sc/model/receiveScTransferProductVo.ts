export interface ReceiveBatchDetailVo {
  /**
   * 批次号
   */
  batchNumber: string;

  /**
   * 本次收货数量
   */
  receiveNum: number;
}

export interface ReceiveSerialDetailVo {
  /**
   * 序列号
   */
  serialNumber: string;
}

export interface ReceiveScTransferProductVo {
  /**
   * 航材ID
   */
  productId: string;

  /**
   * 收货数量
   */
  receiveNum: number;

  /**
   * 批次收货明细（批次管理商品）
   */
  batchDetails?: ReceiveBatchDetailVo[];

  /**
   * 序列号收货明细（序列号管理商品）
   */
  serialDetails?: ReceiveSerialDetailVo[];
}
