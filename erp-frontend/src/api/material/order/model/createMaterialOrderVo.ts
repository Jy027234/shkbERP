export interface CreateMaterialOrderVo {
  /**
   * 仓库ID
   */
  scId: string;

  /**
   * 发料申请单ID
   */
  materialApplyId?: string;

  /**
   * 备注
   */
  description?: string;

  /**
   * 明细
   */
  details: CreateMaterialOrderDetailVo[];
}

export interface CreateMaterialOrderDetailVo {
  /**
   * 航材ID
   */
  productId: string;

  /**
   * 发料数量
   */
  orderNum: number;

  /**
   * 含税价
   */
  taxPrice: number;

  /**
   * 备注
   */
  description?: string;
}
