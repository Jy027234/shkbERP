export interface QueryPartNumberBo {
  /**
   * ID
   */
  id: string;

  /**
   * 编号
   */
  code: string;

  /**
   * 名称
   */
  name: string;

  /**
   * 机型ID
   */
  machineTypeId: string;

  /**
   * 机型名称
   */
  machineTypeName: string;

  /**
   * 状态
   */
  available: boolean;

  /**
   * 备注
   */
  description: string;
}
