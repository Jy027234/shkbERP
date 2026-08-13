import { defHttp } from '/@/utils/http/axios';

const baseUrl = '/shkb/contract-task';
const region = 'cloud-api';

/**
 * 工卡Bo接口
 */
export interface ContractTaskWorkCardBo {
  id: string;
  taskId: string;
  workCardId: string;
  workCardCode: string;
  workCardName: string;
  machineTypeId: string;
  machineTypeName: string;
  partNumberId: string;
  partNumberName: string;
  repairTypeId: string;
  repairTypeName: string;
}

/**
 * 工卡Vo接口
 */
export interface ContractTaskWorkCardVo {
  taskId: string;
  workCardIds: string[];
}

/**
 * 查询任务工卡列表
 * @param taskId 任务ID
 */
export function getWorkCards(taskId: string): Promise<ContractTaskWorkCardBo[]> {
  return defHttp.get<ContractTaskWorkCardBo[]>(
    {
      url: baseUrl + '/work-cards',
      params: { taskId }
    },
    {
      region,
    },
  );
}

/**
 * 批量添加任务工卡
 * @param data 工卡数据
 */
export function batchAddWorkCards(data: ContractTaskWorkCardVo): Promise<void> {
  return defHttp.post<void>(
    {
      url: baseUrl + '/work-card/add',
      data,
    },
    {
      region,
    },
  );
}

/**
 * 批量删除任务工卡
 * @param data 工卡数据
 */
export function batchDeleteWorkCards(data: ContractTaskWorkCardVo): Promise<void> {
  return defHttp.post<void>(
    {
      url: baseUrl + '/work-card/delete',
      data,
    },
    {
      region,
    },
  );
}

// 导出API对象
export const contractTaskWorkCardApi = {
  getWorkCards,
  batchAddWorkCards,
  batchDeleteWorkCards
};
