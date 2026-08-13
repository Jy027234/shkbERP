import { PageVo } from '@/api/model/pageVo';

export interface PartNumberSelectorVo extends PageVo {
  /**
   * 编号
   */
  code?: string;

  /**
   * 名称
   */
  name?: string;

  /**
   * 机型ID
   */
  machineTypeId?: string;

  /**
   * 状态
   */
  available?: boolean;
}
