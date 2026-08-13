import { SortPageVo } from '@/api/model/sortPageVo';

export interface QueryProductStockLogVo extends SortPageVo {
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

  /**
   * 创建起始时间
   */
  createStartTime: string;

  /**
   * 创建截止时间
   */
  createEndTime: string;

  /**
   * 业务类型
   */
  bizType: number;
}
