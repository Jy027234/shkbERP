export interface GetMaterialOrderBo {
  /**
   * ID
   */
  id: string;

  /**
   * 发料单号
   */
  code: string;

  /**
   * 仓库ID
   */
  scId: string;

  /**
   * 仓库名称
   */
  scName: string;

  /**
   * 发料申请单ID
   */
  materialApplyId?: string;

  /**
   * 发料申请单号
   */
  materialApplyCode?: string;

  /**
   * 总数量
   */
  totalNum: number;

  /**
   * 总金额
   */
  totalAmount: number;

  /**
   * 备注
   */
  description?: string;

  /**
   * 创建人
   */
  createBy: string;

  /**
   * 创建时间
   */
  createTime: string;

  /**
   * 明细
   */
  details: GetMaterialOrderDetailBo[];
}

export interface GetMaterialOrderDetailBo {
  /**
   * ID
   */
  id: string;

  /**
   * 航材ID
   */
  productId: string;

  /**
   * 航材名称
   */
  productName: string;

  /**
   * 类目名称
   */
  categoryName: string;

  /**
   * 品牌名称
   */
  brandName: string;

  /**
   * 规格
   */
  spec: string;

  /**
   * 单位
   */
  unit: string;

  /**
   * 发料数量
   */
  orderNum: number;

  /**
   * 已出库数量
   */
  outNum: number;

  /**
   * 含税价
   */
  taxPrice: number;

  /**
   * 含税金额
   */
  taxAmount: number;

  /**
   * 备注
   */
  description?: string;
}
