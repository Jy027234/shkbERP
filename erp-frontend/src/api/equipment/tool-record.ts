import { defHttp } from '@/utils/http/axios';

const baseUrl = '/shkb/tool/record';
const region = 'cloud-api';

/**
 * 查询工具计量记录列表
 * @param params 查询参数
 * @returns 计量记录列表
 */
export function query(params: any) {
  return defHttp.get({
    url: `${baseUrl}/query`,
    params,
  });
}

/**
 * 根据ID查询工具计量记录
 * @param id 计量记录ID
 * @returns 计量记录详情
 */
export function get(id: string) {
  return defHttp.get({
    url: `${baseUrl}/${id}`,
  });
}

/**
 * 创建工具计量记录（支持附件上传）
 * @param data FormData对象，包含计量记录数据和附件
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
        region,
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
 * 修改工具计量记录（支持附件上传）
 * @param data FormData对象，包含计量记录数据和附件
 * @returns 操作结果
 */
export function update(data: any) {
  // 如果是FormData类型，使用特殊处理方式
  if (data instanceof FormData) {
    return defHttp.post(
      {
        url: baseUrl + '/update',
        data,
        headers: {
          // 不要手动设置Content-Type，让浏览器自动设置带boundary的Content-Type
          'X-Requested-With': 'XMLHttpRequest',
        },
        // 确保不会将FormData转换为其他格式
        transformRequest: [(data) => data],
      },
      {
        region,
        // 确保响应被正确处理
        isTransformResponse: true,
        // 确保参数不会被拼接到URL
        joinParamsToUrl: false,
      },
    );
  } else {
    // 普通JSON数据处理
    return defHttp.put({
      url: baseUrl,
      data,
    });
  }
}

/**
 * 删除工具计量记录
 * @param id 计量记录ID
 * @returns 操作结果
 */
export function deleteById(id: string) {
  return defHttp.delete({
    url: `${baseUrl}/${id}`,
  });
}

/**
 * 删除工具计量记录附件
 * @param id 附件ID
 * @returns 操作结果
 */
export function deleteAttachment(id: string) {
  return defHttp.delete({
    url: `${baseUrl}/attachment/${id}`,
  });
}

/**
 * 批量删除工具计量记录
 * @param ids 计量记录ID列表
 * @returns 操作结果
 */
export function batchDelete(ids: string[]) {
  return defHttp.delete({
    url: `${baseUrl}/batch`,
    data: ids,
  });
}
