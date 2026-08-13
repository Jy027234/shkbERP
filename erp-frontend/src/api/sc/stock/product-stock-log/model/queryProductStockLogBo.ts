export interface QueryProductStockLogBo {
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
   * 原库存数量
   */
  oriStockNum: number;

  /**
   * 现库存数量
   */
  curStockNum: number;

  /**
   * 原含税成本价
   */
  oriTaxPrice: number;

  /**
   * 现含税成本价
   */
  curTaxPrice: number;

  /**
   * 含税金额
   */
  taxAmount: number;

  /**
   * 创建人
   */
  createBy: string;

  /**
   * 创建时间
   */
  createTime: string;

  /**
   * 业务单据ID
   */
  bizId: string;

  /**
   * 业务单据号
   */
  bizCode: string;

  /**
   * 业务类型
   */
  bizType: number;
}
