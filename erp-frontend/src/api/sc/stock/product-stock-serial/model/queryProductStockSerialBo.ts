/**
 * 查询航材序列号库存结果
 */
export interface QueryProductStockSerialBo {
  // ID
  id: string;

  // 仓库ID
  scId: string;

  // 仓库编号
  scCode: string;

  // 仓库名称
  scName: string;

  // 航材ID
  productId: string;

  // 航材编号
  productCode: string;

  // 航材名称
  productName: string;

  // 航材分类ID
  categoryId: string;

  // 航材分类名称
  categoryName: string;

  // 航材品牌ID
  brandId: string;

  // 航材品牌名称
  brandName: string;

  // 序列号
  serialNumber: string;

  // 生产日期
  productionDate: string;

  // 失效日期
  expiryDate: string;
}
