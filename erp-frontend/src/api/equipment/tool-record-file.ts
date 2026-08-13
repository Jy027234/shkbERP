import { defHttp } from '@/utils/http/axios';

const baseUrl = '/shkb/tool/record/file';
const region = 'cloud-api';

/**
 * 获取工具计量记录附件列表
 * @param recordId 计量记录ID
 * @returns 附件列表
 */
export function getToolRecordFiles(recordId: string) {
  return defHttp.get(
    {
      url: `${baseUrl}/list`,
      params: {
        recordId,
      },
    },
    {
      region,
    },
  );
}

/**
 * 上传工具计量记录附件
 * @param recordId 计量记录ID
 * @param files 文件列表
 * @returns 上传结果
 */
export function uploadToolRecordFiles(recordId: string, files: File[]) {
  // 创建FormData对象用于提交multipart/form-data请求
  const formData = new FormData();
  
  // 添加计量记录ID到FormData
  formData.append('recordId', recordId);
  
  // 添加多个文件
  if (files && files.length > 0) {
    files.forEach(file => {
      formData.append('files', file);
    });
  }
  
  return defHttp.post(
    {
      url: `${baseUrl}/upload`,
      // 直接使用FormData作为数据源
      data: formData,
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
}

/**
 * 删除工具计量记录附件
 * @param id 附件ID
 * @returns 删除结果
 */
export function deleteToolRecordFile(id: string) {
  return defHttp.delete(
    {
      url: `${baseUrl}/${id}`,
    },
    {
      region,
    },
  );
}

/**
 * 批量删除工具计量记录附件
 * @param ids 附件ID列表
 * @returns 删除结果
 */
export function batchDeleteToolRecordFiles(ids: string[]) {
  return defHttp.delete(
    {
      url: `${baseUrl}/batch`,
      data: ids,
    },
    {
      region,
    },
  );
}
