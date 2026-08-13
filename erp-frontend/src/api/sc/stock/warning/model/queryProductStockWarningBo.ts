export interface QueryProductStockWarningBo {
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
   * 预警下限
   */
  minLimit: number;

  /**
   * 预警上限
   */
  maxLimit: number;

  /**
   * 操作人
   */
  updateBy: string;

  /**
   * 操作时间
   */
  updateTime: string;

  /**
   * 状态
   */
  available: boolean;
}
