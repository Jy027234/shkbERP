import { defHttp } from '@/utils/http/axios';

const baseUrl = '/shkb/tool';

/**
 * 新增工具
 * @param data 工具数据
 * @returns 操作结果
 */
export function create(data: any) {
  // 如果是FormData类型，使用特殊处理方式
  if (data instanceof FormData) {
    return defHttp.post(
      {
        url: baseUrl,
        data,
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
  } else {
    // 普通JSON数据处理
    return defHttp.post({
      url: baseUrl,
      data,
    });
  }
}

/**
 * 查询工具列表
 * @param params 查询参数
 * @returns 工具列表
 */
export function query(params: any) {
  return defHttp.get({
    url: `${baseUrl}/query`,
    params,
  });
}

/**
 * 根据ID查询工具
 * @param id 工具ID
 * @returns 工具详情
 */
export function get(id: string) {
  return defHttp.get({
    url: `${baseUrl}/${id}`,
  });
}

/**
 * 修改工具
 * @param data 工具数据
 * @returns 操作结果
 */
export function update(data: any) {
  return defHttp.put({
    url: baseUrl,
    data,
  });
}

/**
 * 删除工具
 * @param id 工具ID
 * @returns 操作结果
 */
export function deleteById(id: string) {
  return defHttp.delete({
    url: baseUrl,
    params: { id },
  });
}

/**
 * 批量删除工具
 * @param ids 工具ID数组
 * @returns 操作结果
 */
export function batchDelete(ids: string[]) {
  return defHttp.delete({
    url: `${baseUrl}/batch`,
    params: { ids },
  });
}
