export interface GetProductStockBatchBo {
  /**
   * ID
   */
  id: string;

  /**
   * 仓库ID
   */
  scId: string;

  /**
   * 仓库编号
   */
  scCode: string;

  /**
   * 仓库名称
   */
  scName: string;

  /**
   * 航材ID
   */
  productId: string;

  /**
   * 航材编号
   */
  productCode: string;

  /**
   * 航材名称
   */
  productName: string;

  /**
   * 航材分类名称
   */
  categoryName: string;

  /**
   * 航材制造商名称
   */
  brandName: string;

  /**
   * 库存数量
   */
  quantity: number;

  /**
   * 批次号
   */
  batchNumber: string;

  /**
   * 生产日期
   */
  productionDate: string;

  /**
   * 失效日期
   */
  expiryDate: string;
}
