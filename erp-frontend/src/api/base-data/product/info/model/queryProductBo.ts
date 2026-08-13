export interface QueryProductBo {
  /**
   * ID
   */
  id: string;

  /**
   * 编号
   */
  code: string;

  /**
   * 名称
   */
  name: string;

  /**
   * 简称
   */
  shortName: string;

  /**
   * SKU
   */
  skuCode: string;

  /**
   * 分类名称
   */
  categoryName: string;

  /**
   * 制造商名称
   */
  brandName: string;

  /**
   * 航材类型
   */
  productType: number;

  /**
   * 状态
   */
  available: boolean;

  /**
   * 创建时间
   */
  createTime: string;

  /**
   * 修改时间
   */
  updateTime: string;
}
