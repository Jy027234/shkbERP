/**
 * 获取航材序列号库存详情
 */
export interface GetProductStockSerialBo {
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

  // 序列号
  serialNumber: string;

  // 生产日期
  productionDate: string;

  // 失效日期
  expiryDate: string;
}
