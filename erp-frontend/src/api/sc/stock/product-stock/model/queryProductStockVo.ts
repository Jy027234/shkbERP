import { SortPageVo } from '@/api/model/sortPageVo';

export interface QueryProductStockVo extends SortPageVo {
  /**
   * 仓库ID
   */
  scId: string;

  /**
   * 航材编号
   */
  productCode: string;

  /**
   * 航材名称
   */
  productName: string;

  /**
   * 航材分类ID
   */
  categoryId: string;

  /**
   * 航材制造商ID
   */
  brandId: string;
}
