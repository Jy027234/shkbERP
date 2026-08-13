import { defHttp } from '/@/utils/http/axios';
import { PageResult } from '@/api/model/pageResult';
import { ContentTypeEnum, ResponseEnum } from '@/enums/httpEnum';
import { QueryProductSelectorVo } from '@/api/base-data/product/info/model/queryProductSelectorVo';
import { ProductSelectorBo } from '@/api/base-data/product/info/model/productSelectorBo';
import { QueryProductBo } from '@/api/base-data/product/info/model/queryProductBo';
import { QueryProductVo } from '@/api/base-data/product/info/model/queryProductVo';
import { GetProductBo } from '@/api/base-data/product/info/model/getProductBo';
import { CreateProductVo } from '@/api/base-data/product/info/model/createProductVo';
import { UpdateProductVo } from '@/api/base-data/product/info/model/updateProductVo';

const baseUrl = '/basedata/product';
const selectorBaseUrl = '/selector';
const region = 'cloud-api';

export function selector(params: QueryProductSelectorVo): Promise<PageResult<ProductSelectorBo>> {
  return defHttp.get<PageResult<ProductSelectorBo>>(
    {
      url: selectorBaseUrl + '/product',
      params,
    },
    {
      region,
    },
  );
}

/**
 * 航材导出
 * @param data 查询条件
 */
export function exportCustom(data: QueryProductVo) {
  return defHttp.post<Blob>(
    {
      url: baseUrl + '/export/custom',
      data,
      responseType: 'blob',
    },
    {
      region,
      isReturnNativeResponse: true,
    },
  );
}

/**
 * 航材批量修改信息导入（仅机型）
 */
export function importAviationBatchUpdateExcel(data: { id: string; file: Blob }): Promise<any> {
  return defHttp.post<any>(
    {
      url: baseUrl + '/import/custom/batch-update',
      data,
    },
    {
      contentType: ContentTypeEnum.BLOB,
      region,
    },
  );
}

/**
 * 下载航材批量修改机型模板
 */
export function downloadAviationBatchUpdateTemplate(): Promise<void> {
  return defHttp.get<void>(
    {
      url: baseUrl + '/import/custom/batch-update/template',
    },
    {
      responseType: ResponseEnum.BLOB,
      region,
    },
  );
}

export function loadProduct(ids: string[]): Promise<ProductSelectorBo[]> {
  return defHttp.post<ProductSelectorBo[]>(
    {
      url: selectorBaseUrl + '/product/load',
      data: ids,
    },
    {
      contentType: ContentTypeEnum.JSON,
      region,
    },
  );
}

/**
 * 查询列表
 */
export function query(params: QueryProductVo): Promise<PageResult<QueryProductBo>> {
  return defHttp.get<PageResult<QueryProductBo>>(
    {
      url: baseUrl + '/query',
      params,
    },
    {
      region,
    },
  );
}

/**
 * 根据ID查询
 * @param id
 */
export function get(id: string): Promise<GetProductBo> {
  return defHttp.get<GetProductBo>(
    {
      url: baseUrl,
      params: {
        id: id,
      },
    },
    {
      region,
    },
  );
}

/**
 * 新增
 * @param data
 */
export function create(data: CreateProductVo): Promise<void> {
  return defHttp.post<void>(
    {
      url: baseUrl,
      data,
    },
    {
      contentType: ContentTypeEnum.JSON,
      region,
    },
  );
}

/**
 * 修改
 * @param data
 */
export function update(data: UpdateProductVo): Promise<void> {
  return defHttp.put<void>(
    {
      url: baseUrl,
      data,
    },
    {
      contentType: ContentTypeEnum.JSON,
      region,
    },
  );
}

/**
 * 下载导入模板
 */
export function downloadImportTemplate(): Promise<void> {
  return defHttp.get<void>(
    {
      url: baseUrl + '/import/template',
    },
    {
      responseType: ResponseEnum.BLOB,
      region,
    },
  );
}

/**
 * 下载航材导入模板
 */
export function downloadCustomImportTemplate(): Promise<void> {
  return defHttp.get<void>(
    {
      url: baseUrl + '/import/custom/template',
    },
    {
      responseType: ResponseEnum.BLOB,
      region,
    },
  );
}

/**
 * 导入
 */
export function importExcel(data: { id: string; file: Blob }): Promise<void> {
  return defHttp.post<void>(
    {
      url: baseUrl + '/import',
      data,
    },
    {
      contentType: ContentTypeEnum.BLOB,
      region,
    },
  );
}

/**
 * 航材导入
 */
export function importCustomExcel(data: { id: string; file: Blob }): Promise<any> {
  return defHttp.post<any>(
    {
      url: baseUrl + '/import/custom',
      data,
    },
    {
      contentType: ContentTypeEnum.BLOB,
      region,
    },
  );
}
