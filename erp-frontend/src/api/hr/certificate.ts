import { defHttp } from '@/utils/http/axios';

const region = 'shkb';
const baseUrl = '/shkb/employee-certificate';

// 查询证书列表
export function query(params: any) {
  return defHttp.post({
    url: `${baseUrl}/query`,
    data: params,
  });
}

// 获取证书详情
export function get(id: string) {
  return defHttp.get({
    url: `${baseUrl}/${id}`,
  });
}

// 创建证书
export function create(data: any) {
  return defHttp.post({
    url: baseUrl,
    data,
  });
}

// 更新证书
export function update(data: any) {
  return defHttp.put({
    url: baseUrl,
    data,
  });
}

// 删除证书
export function del(id: string) {
  return defHttp.delete({
    url: `${baseUrl}/${id}`,
  });
}

// 批量删除证书
export function batchDelete(ids: string[]) {
  return defHttp.delete({
    url: `${baseUrl}/batch`,
    params: {
      ids: ids.join(','),
    },
  });
}

// 获取证书统计
export function getStatistics() {
  return defHttp.get({
    url: `${baseUrl}/statistics`,
  });
}

// 导出证书列表
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
