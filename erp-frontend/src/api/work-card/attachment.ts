import { defHttp } from '/@/utils/http/axios';

const baseUrl = '/shkb/work-card';
const region = 'cloud-api';

/**
 * 获取工卡附件列表
 * @param workCardId 工卡ID
 */
export function getWorkCardAttachments(workCardId: string) {
  return defHttp.get(
    {
      url: `${baseUrl}/attachment/list`,
      params: { workCardId },
    },
    {
      region,
    },
  );
}

/**
 * 上传工卡附件
 * @param workCardId 工卡ID
 * @param files 文件列表
 */
export function uploadWorkCardAttachments(workCardId: string, files: File[]) {
  // 创建FormData对象用于提交multipart/form-data请求
  const formData = new FormData();
  
  // 添加工卡ID到FormData
  formData.append('workCardId', workCardId);
  
  // 添加多个文件
  if (files && files.length > 0) {
    files.forEach(file => {
      formData.append('files', file);
    });
  }
  
  return defHttp.post(
    {
      url: `${baseUrl}/attachment/upload`,
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
 * 删除工卡附件
 * @param id 附件ID
 */
export function deleteWorkCardAttachment(id: string) {
  return defHttp.delete(
    {
      url: `${baseUrl}/attachment/${id}`,
    },
    {
      region,
    },
  );
}

/**
 * 批量删除工卡附件
 * @param ids 附件ID列表
 */
export function batchDeleteWorkCardAttachments(ids: string[]) {
  return defHttp.delete(
    {
      url: `${baseUrl}/attachment/batch`,
      data: ids,
    },
    {
      region,
    },
  );
}
