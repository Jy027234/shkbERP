import { defHttp } from '/@/utils/http/axios';
import { ProductStockBatchFileBo } from './model/productStockBatchFileBo';

const baseUrl = '/stock/product/batch/file';
const region = 'cloud-api';

/**
 * 获取批次库存附件列表
 * @param batchId 批次ID
 */
export function query(batchId: string): Promise<ProductStockBatchFileBo[]> {
  return defHttp.get<ProductStockBatchFileBo[]>(
    {
      url: baseUrl + '/list',
      params: {
        batchId
      },
    },
    {
      region,
    },
  );
}

/**
 * 上传批次库存附件
 * @param batchId 批次ID
 * @param files 文件列表
 */
export function upload(batchId: string, files: File[]): Promise<string[]> {
  // 创建FormData对象用于提交multipart/form-data请求
  const formData = new FormData();
  
  // 添加批次ID到FormData
  formData.append('batchId', batchId);
  
  // 添加多个文件
  if (files && files.length > 0) {
    files.forEach(file => {
      formData.append('files', file);
    });
  }
  
  return defHttp.post<string[]>(
    {
      url: baseUrl + '/upload',
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
 * 删除批次库存附件
 * @param id 附件ID
 */
export function remove(id: string): Promise<void> {
  return defHttp.delete<void>(
    {
      url: baseUrl + '/' + id,
    },
    {
      region,
    },
  );
}

/**
 * 批量删除批次库存附件
 * @param ids 附件ID列表
 */
export function batchRemove(ids: string[]): Promise<number> {
  return defHttp.delete<number>(
    {
      url: baseUrl + '/batch',
      data: ids
    },
    {
      region,
    },
  );
}
