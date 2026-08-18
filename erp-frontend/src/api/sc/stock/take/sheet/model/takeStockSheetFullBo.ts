export interface TakeStockSheetFullBo {
  /**
   * ID
   */
  id: string;

  /**
   * 业务单据号
   */
  code: string;

  /**
   * 盘点任务ID
   */
  planId: string;

  /**
   * 盘点任务号
   */
  planCode: string;

  /**
   * 预先盘点单ID
   */
  preSheetId: string;

  /**
   * 预先盘点单号
   */
  preSheetCode: string;

  /**
   * 仓库名称
   */
  scName: string;

  /**
   * 盘点任务-盘点类别
   */
  takeType: number;

  /**
   * 业务名称
   */
  bizName: string;

  /**
   * 盘点任务-盘点状态
   */
  takeStatus: number;

  /**
   * 状态
   */
  status: number;

  /**
   * 备注
   */
  description: string;

  /**
   * 拒绝理由
   */
  refuseReason: string;

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
   * 明细
   */
  details: SheetDetailBo[];
}

export interface SheetDetailBo {
  /**
   * ID
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
  isBatch: boolean;

  /**
   * 是否序列号管理
   */
  isSerial: boolean;

  /**
   * 批次明细
   */
  batchDetails?: SheetBatchDetailBo[];

  /**
   * 序列号明细
   */
  serialDetails?: SheetSerialDetailBo[];

  /**
   * 库存数量
   */
  stockNum: number;

  /**
   * 盘点数量
   */
  takeNum: number;

  /**
   * 备注
   */
  description: string;

  /**
   * 盘点任务ID
   */
  planId: string;
}

export interface SheetBatchDetailBo {
  /**
   * 批次号
   */
  batchNumber: string;

  /**
   * 系统批次库存数量（录入时快照）
   */
  stockNum: number;

  /**
   * 实盘数量
   */
  takeNum: number;

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
   * 批次号
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
