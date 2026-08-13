/**
 * 修改航材序列号库存信息请求参数
 */
export interface UpdateProductStockSerialVo {
  // ID
  id: string;

  // 生产日期
  productionDate?: string;

  // 失效日期
  expiryDate?: string;
}
