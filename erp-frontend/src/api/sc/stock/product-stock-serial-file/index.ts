import { defHttp } from '/@/utils/http/axios';
import { ProductStockSerialFileBo } from './model/productStockSerialFileBo';

const baseUrl = '/stock/product/serial/file';
const region = 'cloud-api';

/**
 * 获取序列号库存附件列表
 * @param serialId 序列号库存ID
 */
export function query(serialId: string): Promise<ProductStockSerialFileBo[]> {
  return defHttp.get<ProductStockSerialFileBo[]>(
    {
      url: baseUrl + '/list',
      params: {
        serialId
      },
    },
    {
      region,
    },
  );
}

/**
 * 上传序列号库存附件
 * @param serialId 序列号库存ID
 * @param files 文件列表
 */
export function upload(serialId: string, files: File[]): Promise<string[]> {
  // 创建FormData对象用于提交multipart/form-data请求
  const formData = new FormData();
  
  // 添加序列号库存ID到FormData
  formData.append('serialId', serialId);
  
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
 * 删除序列号库存附件
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
 * 批量删除序列号库存附件
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
