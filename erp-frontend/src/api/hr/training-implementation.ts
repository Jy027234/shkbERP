import { defHttp } from '@/utils/http/axios';

const baseUrl = '/training-implementation';

// 查询培训实施列表
export function query(params: any) {
  return defHttp.post({
    url: `${baseUrl}/query`,
    data: params,
  });
}

// 获取培训实施详情
export function get(id: string) {
  return defHttp.get({
    url: `${baseUrl}/${id}`,
  });
}

// 创建培训实施
export function create(data: any) {
  return defHttp.post({
    url: baseUrl,
    data,
  });
}

// 更新培训实施
export function update(data: any) {
  return defHttp.put({
    url: baseUrl,
    data,
  });
}

// 删除培训实施
export function del(id: string) {
  return defHttp.delete({
    url: `${baseUrl}/${id}`,
  });
}

// 批量删除培训实施
export function batchDelete(ids: string[]) {
  return defHttp.delete({
    url: `${baseUrl}/batch`,
    params: {
      ids: ids.join(','),
    },
  });
}

// 修改状态
export function updateStatus(id: string, status: number) {
  return defHttp.put({
    url: `${baseUrl}/status`,
    data: { id, status },
  });
}

// 开始培训
export function start(id: string) {
  return defHttp.put({
    url: `${baseUrl}/start?id=${id}`,
  });
}

// 完成培训
export function complete(id: string) {
  return defHttp.put({
    url: `${baseUrl}/complete?id=${id}`,
  });
}

// 完成培训（带文件上传）
export function completeWithFile(formData: FormData) {
  return defHttp.put(
    {
      url: `${baseUrl}/complete`,
      data: formData,
      headers: {
        // 不要手动设置Content-Type，让浏览器自动设置带boundary的Content-Type
        'X-Requested-With': 'XMLHttpRequest',
      },
      // 确保不会将FormData转换为其他格式
      transformRequest: [(data) => data],
    },
    {
      // 确保响应被正确处理
      isTransformResponse: true,
      // 确保参数不会被拼接到URL
      joinParamsToUrl: false,
    },
  );
}

// 取消培训
export function cancel(id: string) {
  return defHttp.put({
    url: `${baseUrl}/cancel?id=${id}`,
  });
}

// 查询学员列表
export function queryParticipants(implementationId: string, params?: any) {
  return defHttp.post({
    url: '/training-participant/query',
    data: { implementationId, ...params },
  });
}

// 批量创建学员
export function createParticipants(data: any[]) {
  return defHttp.post({
    url: '/training-participant/batch',
    data,
  });
}

// 更新学员
export function updateParticipant(data: any) {
  return defHttp.put({
    url: '/training-participant',
    data,
  });
}

// 删除学员
export function deleteParticipant(id: string) {
  return defHttp.delete({
    url: `/training-participant/${id}`,
  });
}

// 批量删除学员
export function batchDeleteParticipants(ids: string[]) {
  return defHttp.delete({
    url: '/training-participant/batch',
    data: ids,
  });
}
