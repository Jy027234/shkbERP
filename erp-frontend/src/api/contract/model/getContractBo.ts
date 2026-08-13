export interface RepairTypeVo {
  /**
   * 维修类型ID
   */
  id: string;

  /**
   * 维修类型编码
   */
  code: string;

  /**
   * 维修类型名称
   */
  name: string;
}

export interface GetContractBo {
  /**
   * ID
   */
  id: string;

  /**
   * 编号
   */
  code: string;

  /**
   * 合同名称
   */
  name: string;

  /**
   * 合同类型
   */
  contractType: number;

  /**
   * 合同类型名称
   */
  contractTypeName: string;

  /**
   * 客户ID
   */
  customerId: string;

  /**
   * 客户名称
   */
  customerName: string;

   /**
   * 客户简码
   */
   mnemonicCode: string;

  /**
   * 件号ID
   */
  partNumberId: string;

  /**
   * 件号编码
   */
  partNumberCode: string;

  /**
   * 件号名称
   */
  partNumberName: string;

  /**
   * 机型ID
   */
  machineTypeId: string;

  /**
   * 机型编码
   */
  machineTypeCode: string;

  /**
   * 机型名称
   */
  machineTypeName: string;

  /**
   * 序号
   */
  serialNumber: string;

  /**
   * 维修类型列表
   */
  repairTypes: RepairTypeVo[];

  /**
   * 其他维修需求
   */
  otherRepairRequirements: string;

  /**
   * 合同时间
   */
  contractTime: string;

  /**
   * 入库时间
   */
  storageTime: string;

  /**
   * 计划完工时间
   */
  plannedCompletionTime: string;

  /**
   * 交付时间
   */
  deliveryTime: string;

  /**
   * 合同价格
   */
  contractPrice: number;

  /**
   * 更换件价格
   */
  replacementPartPrice: number;

  /**
   * 实际完工时间
   */
  actualCompletionTime: string;

  /**
   * 合同状态
   */
  contractStatus: number;

  /**
   * 合同状态名称
   */
  contractStatusName: string;

  /**
   * 状态
   */
  available: boolean;

  /**
   * 备注
   */
  description: string;

  /**
   * 创建人ID
   */
  createBy: string;

  /**
   * 创建时间
   */
  createTime: string;

  /**
   * 修改人ID
   */
  updateBy: string;

  /**
   * 修改时间
   */
  updateTime: string;
}
