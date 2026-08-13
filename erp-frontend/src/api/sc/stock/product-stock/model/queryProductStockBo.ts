export interface QueryProductStockBo {
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
   * 航材分类
   */
  categoryName: string;

  /**
   * 航材制造商
   */
  brandName: string;

  /**
   * 库存数量
   */
  stockNum: number;

  /**
   * 含税价格
   */
  taxPrice: number;

  /**
   * 含税金额
   */
  taxAmount: number;
}
