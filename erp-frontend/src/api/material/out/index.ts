import { defHttp } from '@/utils/http/axios';
import { PageResult } from '@/api/model/pageResult';
import { ContentTypeEnum, ResponseEnum } from '@/enums/httpEnum';

const baseUrl = '/material/out/sheet';
const region = 'cloud-api';

export interface QueryMaterialOutSheetParams {
  pageIndex: number;
  pageSize: number;
  code?: string;
  scId?: string;
  supplierId?: string;
  materialUserId?: string;
  materialOrderId?: string;
  materialOrderCode?: string;
  status?: number;
  createTimeStart?: string;
  createTimeEnd?: string;
}

export interface MaterialOutSheetDetail {
  productId: string;
  outNum: number;
  orderNum: number;
  taxPrice?: number;
  description?: string;
  stockBatchId?: string;
  serials?: string[];
  serialNumbers?: string;
  materialOrderDetailId?: string;
}

export interface CreateMaterialOutSheetPayload {
  scId: string;
  supplierId?: string;
  materialUserId?: string;
  materialDate?: string;
  materialOrderId?: string;
  description?: string;
  details: MaterialOutSheetDetail[];
}

/**
 * 查询列表
 */
export function query(params: QueryMaterialOutSheetParams): Promise<PageResult<Record<string, unknown>>> {
  return defHttp.get<PageResult<Record<string, unknown>>>({
    url: baseUrl + '/query',
    params,
  }, { region });
}

/**
 * 根据ID查询
 */
export function get(id: string) {
  return defHttp.get({
    url: baseUrl,
    params: {
      id,
    },
  }, { region });
}

/**
 * 新增
 */
export function create(data: CreateMaterialOutSheetPayload): Promise<string> {
  return defHttp.post({
    url: baseUrl,
    data,
  }, { region, contentType: ContentTypeEnum.JSON });
}

/**
 * 修改
 */
export function update(data: CreateMaterialOutSheetPayload & { id: string }): Promise<void> {
  return defHttp.put({
    url: baseUrl,
    data,
  }, { region, contentType: ContentTypeEnum.JSON });
}

/**
 * 审核通过
 */
export function approvePass(data: { id: string; description?: string }): Promise<void> {
  return defHttp.patch({
    url: baseUrl + '/approve/pass',
    data,
  }, { region, contentType: ContentTypeEnum.JSON });
}

/**
 * 批量审核通过
 */
export function batchApprovePass(data: string[]): Promise<void> {
  return defHttp.patch({
    url: baseUrl + '/approve/pass/batch',
    data,
  }, { region, contentType: ContentTypeEnum.JSON });
}

/**
 * 直接审核通过
 */
export function directApprovePass(data: CreateMaterialOutSheetPayload): Promise<string> {
  return defHttp.post({
    url: baseUrl + '/direct/approve/pass',
    data,
  }, { region, contentType: ContentTypeEnum.JSON });
}

/**
 * 标记为可领料
 */
export function approveRefuse(data: { id: string; refuseReason: string }): Promise<void> {
  return defHttp.patch({
    url: baseUrl + '/mark/pickable',
    data,
  }, { region, contentType: ContentTypeEnum.JSON });
}

/**
 * 批量标记为可领料
 */
export function batchApproveRefuse(data: { id: string; refuseReason: string }): Promise<void> {
  return defHttp.patch({
    url: baseUrl + '/mark/pickable/batch',
    data,
  }, { region, contentType: ContentTypeEnum.JSON });
}

/**
 * 根据ID删除
 */
export function deleteById(id: string): Promise<void> {
  return defHttp.delete({
    url: `${baseUrl}?id=${encodeURIComponent(id)}`,
  }, { region });
}

/**
 * 批量删除
 */
export function batchDelete(ids: string | string[]): Promise<void> {
  return defHttp.delete({
    url: baseUrl + '/batch',
    data: Array.isArray(ids) ? ids : [ids],
  }, { region, contentType: ContentTypeEnum.JSON });
}

/**
 * 导出
 */
export function exportList(data) {
  return defHttp.post(
    {
      url: baseUrl + '/export',
      data,
    },
    {
      region,
      responseType: ResponseEnum.BLOB,
      contentType: ContentTypeEnum.JSON,
    },
  );
}

/**
 * 打印
 */
export function print(id) {
  return defHttp.get(
    {
      url: baseUrl + '/print',
      params: {
        id,
      },
    },
    {
      region,
    },
  );
}

/**
 * 导出发料出库单Word
 */
export function exportWord(id) {
  return defHttp.get(
    {
      url: baseUrl + '/export/word',
      params: {
        id,
      },
      responseType: 'blob',
    },
    {
      isReturnNativeResponse: true,
      errorMessageMode: 'none',
    },
  );
}

/**
 * 查询批次库存
 */
export function getBatchStock(scId: string, productId: string) {
  return defHttp.get({
    url: baseUrl + '/batch/stock',
    params: {
      scId,
      productId,
    },
  }, { region });
}

/**
 * 查询序列号库存
 */
export function getSerialStock(scId: string, productId: string) {
  return defHttp.get({
    url: baseUrl + '/serial/stock',
    params: {
      scId,
      productId,
    },
  }, { region });
}

/**
 * 根据关键字查询发料出库可选航材
 */
export function searchProducts(scId: string, condition: string) {
  return defHttp.get({
    url: baseUrl + '/product/search',
    params: {
      scId,
      condition,
    },
  }, { region });
}
