import { SortPageVo } from '@/api/model/sortPageVo';

export interface QueryMaterialOrderVo extends SortPageVo {
  /**
   * 发料单号
   */
  code?: string;

  /**
   * 仓库ID
   */
  scId?: string;

  /**
   * 合同编号
   */
  contractCode?: string;

  /**
   * 发料申请单ID
   */
  materialApplyId?: string;

  /**
   * 是否已完成出库
   */
  isOutFinish?: boolean;

  /**
   * 操作人
   */
  createBy?: string;

  /**
   * 操作起始时间
   */
  createTimeStart?: string;

  /**
   * 操作截止时间
   */
  createTimeEnd?: string;
}
