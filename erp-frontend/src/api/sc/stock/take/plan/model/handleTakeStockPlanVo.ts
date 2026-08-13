export interface HandleTakeStockPlanVo {
  /**
   * ID
   */
  id: string;

  /**
   * 航材信息
   */
  products: ProductVo[];

  /**
   * 备注
   */
  description: string;

  /**
   * 是否允许修改数量
   */
  allowChangeNum: boolean;

  /**
   * 是否自动计算数量
   */
  autoChangeStock: boolean;
}

export interface ProductVo {
  /**
   * 航材ID
   */
  productId: string;

  /**
   * 修改后盘点数量
   */
  takeNum: number;

  /**
   * 备注
   */
  description: string;
}
