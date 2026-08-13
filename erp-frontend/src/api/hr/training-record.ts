import { defHttp } from '@/utils/http/axios';

const region = 'shkb';
const baseUrl = '/shkb/employee-training';

// 查询员工列表（用于下拉选择）
export function queryEmployees(params: any) {
  return defHttp.get({
    url: '/shkb/employee/query',
    params,
  });
}

// 查询培训记录列表
export function query(params: any) {
  return defHttp.post({
    url: `${baseUrl}/query`,
    data: params,
  });
}

// 获取培训记录详情
export function get(id: string) {
  return defHttp.get({
    url: `${baseUrl}/${id}`,
  });
}

// 创建培训记录
export function create(data: any) {
  return defHttp.post({
    url: baseUrl,
    data,
  });
}

// 更新培训记录
export function update(data: any) {
  return defHttp.put({
    url: baseUrl,
    data,
  });
}

// 删除培训记录
export function del(id: string) {
  return defHttp.delete({
    url: `${baseUrl}/${id}`,
  });
}

// 批量删除培训记录
export function batchDelete(ids: string[]) {
  return defHttp.delete({
    url: `${baseUrl}/batch`,
    params: {
      ids: ids.join(','),
    },
  });
}

// 获取培训记录统计
export function getStatistics() {
  return defHttp.get({
    url: `${baseUrl}/statistics`,
  });
}

// 导出培训记录列表
export function exportList(data: any) {
  return defHttp.post(
    {
      url: `${baseUrl}/export`,
      data,
      responseType: 'blob',
    },
    {
      region,
      isReturnNativeResponse: true,
    },
  );
}
