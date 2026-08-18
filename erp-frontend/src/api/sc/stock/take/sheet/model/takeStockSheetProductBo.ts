export interface TakeStockSheetProductBo {
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
  isBatch?: boolean;

  /**
   * 是否序列号管理
   */
  isSerial?: boolean;

  /**
   * 库存数量
   */
  stockNum: number;

  /**
   * 盘点任务ID
   */
  planId: string;

  /**
   * 仓库ID
   */
  scId: string;
}
