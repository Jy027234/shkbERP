import { defHttp } from '@/utils/http/axios';

const baseUrl = '/machine/info';

/**
 * 查询自动化设备列表
 */
export function query(params: any) {
  return defHttp.get({
    url: `${baseUrl}/query`,
    params,
  });
}

/**
 * 修改设备信息（仅名称与IP）
 */
export function update(data: { id: string; machineName: string; ipAddress?: string }) {
  return defHttp.post({
    url: `${baseUrl}/update`,
    data,
  });
}
