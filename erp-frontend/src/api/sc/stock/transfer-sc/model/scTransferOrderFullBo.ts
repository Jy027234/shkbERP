export interface ScTransferOrderFullBo {
  /**
   * ID
   */
  id: string;

  /**
   * 业务单据号
   */
  code: string;

  /**
   * 转出仓库ID
   */
  sourceScId: string;

  /**
   * 转出仓库名称
   */
  sourceScName: string;

  /**
   * 转入仓库ID
   */
  targetScId: string;

  /**
   * 转入仓库名称
   */
  targetScName: string;

  /**
   * 调拨数量
   */
  totalNum: number;

  /**
   * 调拨成本金额
   */
  totalAmount: number;

  /**
   * 状态
   */
  status: number;

  /**
   * 备注
   */
  description: string;

  /**
   * 修改人
   */
  updateBy: string;

  /**
   * 修改时间
   */
  updateTime: string;

  /**
   * 审核人
   */
  approveBy: string;

  /**
   * 审核时间
   */
  approveTime: string;

  /**
   * 拒绝原因
   */
  refuseReason: string;

  /**
   * 明细
   */
  details: DetailBo[];
}
export interface DetailBo {
  /**
   * ID
   */
  id: string;

  /**
   * 航材ID
   */
  productId: string;

  /**
   * 编号
   */
  productCode: string;

  /**
   * 名称
   */
  productName: string;

  /**
   * 分类名称
   */
  categoryName: string;

  /**
   * 制造商名称
   */
  brandName: string;

  /**
   * SKU
   */
  skuCode: string;

  /**
   * 简码
   */
  externalCode: string;

  /**
   * 规格
   */
  spec: string;

  /**
   * 单位
   */
  unit: string;

  /**
   * 是否批次管理
   */
  isBatch?: boolean;

  /**
   * 是否序列号管理
   */
  isSerial?: boolean;

  /**
   * 批次明细
   */
  batchDetails?: SheetBatchDetailBo[];

  /**
   * 序列号明细
   */
  serialDetails?: SheetSerialDetailBo[];

  /**
   * 调拨数量
   */
  transferNum: number;

  /**
   * 当前库存数量
   */
  curStockNum: number;

  /**
   * 已收货数量
   */
  receiveNum: number;

  /**
   * 备注
   */
  description: string;
}

export interface SheetBatchDetailBo {
  /**
   * 批次号
   */
  batchNumber: string;

  /**
   * 调拨数量
   */
  transferNum: number;

  /**
   * 已收货数量
   */
  receivedNum: number;

  /**
   * 备注
   */
  description: string;
}

export interface SheetSerialDetailBo {
  /**
   * 序列号
   */
  serialNumber: string;

  /**
   * 调拨状态：1在途、2已收货
   */
  transferStatus: number;

  /**
   * 批次号
   */
  batchNumber: string;

  /**
   * 备注
   */
  description: string;
}
