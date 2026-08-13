import { defHttp } from '@/utils/http/axios';

const baseUrl = '/contract-task';

/**
 * 获取任务换件清单
 * @param taskId 任务ID
 * @returns 换件清单列表
 */
export function getTaskPartList(taskId: string) {
  return defHttp.get({
    url: `${baseUrl}/part-list`,
    params: { taskId },
  });
}

/**
 * 发料出库
 * @param params 发料参数
 * @returns 发料结果
 */
export function issueMaterial(params: {
  taskId: string;
  remark?: string;
}) {
  return defHttp.post({
    url: `${baseUrl}/issue-material`,
    data: params,
  });
}
