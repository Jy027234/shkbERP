import { defHttp } from '@/utils/http/axios';

const baseUrl = '/machine/task/tightening';

/**
 * 查询拧紧机设备任务列表
 */
export function query(params: any) {
  return defHttp.get({
    url: `${baseUrl}/query`,
    params,
  });
}

/**
 * 获取拧紧机设备任务详情
 */
export function detail(id: string) {
  return defHttp.get({
    url: `${baseUrl}/detail`,
    params: { id },
  });
}
