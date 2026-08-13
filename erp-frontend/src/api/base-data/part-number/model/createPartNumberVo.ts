export interface CreatePartNumberVo {
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
   * 备注
   */
  description: string;

  /**
   * 状态
   */
  available: boolean;
}
