import { PageVo } from '@/api/model/pageVo';

export interface PurchaseOrderSelectorForReceiveVo extends PageVo {
  code?: string;

  supplierId?: string;

  scId?: string;

  createBy?: string;

  createStartTime?: string;

  createEndTime?: string;

  status?: number;

  received?: boolean;
}