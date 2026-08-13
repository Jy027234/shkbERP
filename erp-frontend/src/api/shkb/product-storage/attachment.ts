import { defHttp } from '@/utils/http/axios';

const baseUrl = '/shkb/product/storage';
const region = 'cloud-api';

/**
 * 获取接收单附件列表
 * @param productStorageId 成品出入库ID
 */
export function getProductStorageAttachments(productStorageId: string) {
  return defHttp.get(
    {
      url: `${baseUrl}/attachment/list`,
      params: { productStorageId },
    },
    { region },
  );
}

/**
 * 上传接收单附件
 * @param productStorageId 成品出入库ID
 * @param files 文件列表
 */
export function uploadProductStorageAttachments(productStorageId: string, files: File[]) {
  const formData = new FormData();
  formData.append('productStorageId', productStorageId);
  if (files && files.length > 0) {
    files.forEach((file) => formData.append('files', file));
  }
  return defHttp.post(
    {
      url: `${baseUrl}/attachment/upload`,
      data: formData,
      headers: { 'X-Requested-With': 'XMLHttpRequest' },
      transformRequest: [(data) => data],
    },
    {
      region,
      isTransformResponse: true,
      joinParamsToUrl: false,
    },
  );
}

/**
 * 删除接收单附件
 * @param id 附件ID
 */
export function deleteProductStorageAttachment(id: string) {
  return defHttp.delete(
    {
      url: `${baseUrl}/attachment/${id}`,
    },
    { region },
  );
}

/**
 * 批量删除接收单附件
 * @param ids 附件ID列表
 */
export function batchDeleteProductStorageAttachments(ids: string[]) {
  return defHttp.delete(
    {
      url: `${baseUrl}/attachment/batch`,
      data: ids,
    },
    { region },
  );
}
