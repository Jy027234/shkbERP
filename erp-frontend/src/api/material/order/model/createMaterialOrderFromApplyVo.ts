export interface CreateMaterialOrderFromApplyVo {
  /**
   * 发料申请单ID
   */
  materialApplyId: string;

  /**
   * 仓库ID
   */
  scId: string;

  /**
   * 备注
   */
  description?: string;
}
