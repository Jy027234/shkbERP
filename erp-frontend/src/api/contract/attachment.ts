import { defHttp } from '/@/utils/http/axios';

const baseUrl = '/shkb/contract';
const region = 'cloud-api';

/**
 * 获取合同附件列表
 * @param contractId 合同ID
 */
export function getContractAttachments(contractId: string) {
  return defHttp.get(
    {
      url: `${baseUrl}/attachment/list`,
      params: {
        contractId,
      },
    },
    {
      region,
    },
  );
}

/**
 * 上传合同附件
 * @param contractId 合同ID
 * @param files 文件列表
 */
export function uploadContractAttachments(contractId: string, files: File[]) {
  // 创建FormData对象用于提交multipart/form-data请求
  const formData = new FormData();
  
  // 添加合同ID到FormData
  formData.append('contractId', contractId);
  
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
      // 不要设置contentType，让浏览器自动处理
      // contentType: ContentTypeEnum.FORM_DATA, // 移除这一行
      // 确保响应被正确处理
      isTransformResponse: true,
      // 确保参数不会被拼接到URL
      joinParamsToUrl: false,
    },
  );
}

/**
 * 删除合同附件
 * @param id 附件ID
 */
export function deleteContractAttachment(id: string) {
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
 * 批量删除合同附件
 * @param ids 附件ID列表
 */
export function batchDeleteContractAttachments(ids: string[]) {
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
