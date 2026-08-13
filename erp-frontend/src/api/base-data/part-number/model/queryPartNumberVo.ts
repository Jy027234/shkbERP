import { SortPageVo } from '@/api/model/sortPageVo';

export interface QueryPartNumberVo extends SortPageVo {
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
