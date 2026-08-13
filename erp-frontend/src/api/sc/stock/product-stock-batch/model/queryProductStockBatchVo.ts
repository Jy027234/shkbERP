import { PageVo } from '@/api/model/pageVo';

export interface QueryProductStockBatchVo extends PageVo {
  /**
   * 仓库ID
   */
  scId?: string;

  /**
   * 航材编号
   */
  productCode?: string;

  /**
   * 航材名称
   */
  productName?: string;

  /**
   * 航材分类ID
   */
  categoryId?: string;

  /**
   * 航材制造商ID
   */
  brandId?: string;

  /**
   * 批次号
   */
  batchNumber?: string;
}
