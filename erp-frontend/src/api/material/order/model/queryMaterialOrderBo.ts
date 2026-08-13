export interface QueryMaterialOrderBo {
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
   * 已出库数量
   */
  totalOutNum: number;

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
   * 合同编号
   */
  contractCode?: string;

  /**
   * 合同名称
   */
  contractName?: string;

  /**
   * 客户标识
   */
  customerCode?: string;

  /**
   * 客户名称
   */
  customerName?: string;

  /**
   * 机型编号
   */
  machineTypeCode?: string;

  /**
   * 机型名称
   */
  machineTypeName?: string;

  /**
   * 件号编号
   */
  partNumberCode?: string;

  /**
   * 件号名称
   */
  partNumberName?: string;

  /**
   * 是否已完成出库
   */
  isOutFinish: boolean;
}
