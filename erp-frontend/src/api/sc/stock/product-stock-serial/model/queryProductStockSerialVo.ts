import { PageVo } from '@/api/model/pageVo';

/**
 * 查询航材序列号库存请求参数
 */
export interface QueryProductStockSerialVo extends PageVo {
  // 仓库ID
  scId?: string;

  // 航材编号
  productCode?: string;

  // 航材名称
  productName?: string;

  // 航材分类ID
  categoryId?: string;

  // 航材制造商ID
  brandId?: string;
  
  // 序列号
  serialNumber?: string;
}
