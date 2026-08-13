import { defHttp } from '/@/utils/http/axios';
import { ContentTypeEnum, ResponseEnum } from '@/enums/httpEnum';
import { A4ExcelPortraitPrintBo } from '@/api/model/a4ExcelPortraitPrintBo';
import { QueryMaterialOrderVo } from '@/api/material/order/model/queryMaterialOrderVo';
import { PageResult } from '@/api/model/pageResult';
import { QueryMaterialOrderBo } from '@/api/material/order/model/queryMaterialOrderBo';
import { GetMaterialOrderBo } from '@/api/material/order/model/getMaterialOrderBo';
import { CreateMaterialOrderVo } from '@/api/material/order/model/createMaterialOrderVo';
import { CreateMaterialOrderFromApplyVo } from '@/api/material/order/model/createMaterialOrderFromApplyVo';

const baseUrl = '/material/order';
const region = 'cloud-api';

/**
 * 打印
 */
export function print(id: string): Promise<A4ExcelPortraitPrintBo> {
  return defHttp.get<A4ExcelPortraitPrintBo>(
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
 * 发料单列表
 */
export function query(params: QueryMaterialOrderVo): Promise<PageResult<QueryMaterialOrderBo>> {
  return defHttp.get<PageResult<QueryMaterialOrderBo>>(
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
 * 导出
 */
export function exportList(data: QueryMaterialOrderVo): Promise<void> {
  return defHttp.post<void>(
    {
      url: baseUrl + '/export',
      data,
    },
    {
      region,
      responseType: ResponseEnum.BLOB,
      errorMessageMode: 'none',
    },
  );
}

/**
 * 查询详情
 */
export function get(id: string): Promise<GetMaterialOrderBo> {
  return defHttp.get<GetMaterialOrderBo>(
    {
      url: baseUrl,
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
 * 新增
 */
export function create(data: CreateMaterialOrderVo): Promise<void> {
  return defHttp.post<void>(
    {
      url: baseUrl,
      data,
    },
    {
      region,
      contentType: ContentTypeEnum.JSON,
    },
  );
}

/**
 * 基于发料申请单创建发料单（POST /material/order）
 */
export function createFromApply(data: CreateMaterialOrderFromApplyVo): Promise<string> {
  return defHttp.post<string>(
    {
      url: baseUrl,
      data,
    },
    {
      region,
      contentType: ContentTypeEnum.JSON,
    },
  );
}

/**
 * 导出换件清单Word文档
 * @param materialApplyId 发料申请单ID
 */
export function exportReplacementList(materialApplyId: string) {
  return defHttp.get(
    {
      url: baseUrl + '/replacement-list/export/by-apply',
      params: {
        materialApplyId,
      },
      responseType: 'blob',
    },
    {
      region,
      isReturnNativeResponse: true,
      errorMessageMode: 'none',
    },
  );
}

/**
 * 替换发料单明细航材
 * @param detailId 发料单明细ID
 * @param newProductId 新的航材ID
 * @param replaceReason 替换原因
 */
export function replaceDetailProduct(
  detailId: string,
  newProductId: string,
  replaceReason: string,
): Promise<void> {
  return defHttp.post<void>(
    {
      url: baseUrl + '/detail/replace',
      data: { detailId, newProductId, replaceReason },
    },
    {
      region,
      contentType: ContentTypeEnum.JSON,
    },
  );
}

/**
 * 撤回发料单
 * @param id 发料单ID
 */
export function withdraw(id: string): Promise<void> {
  return defHttp.delete<void>(
    {
      url: `${baseUrl}/withdraw?id=${encodeURIComponent(id)}`,
    },
    {
      region,
    },
  );
}

/**
 * 修改发料单明细项数量
 * @param detailId 发料单明细ID
 * @param num 新的数量
 */
export function updateDetailNum(detailId: string, num: number): Promise<void> {
  return defHttp.put<void>(
    {
      url: baseUrl + '/detail/num',
      data: { detailId, num },
    },
    {
      region,
      contentType: ContentTypeEnum.JSON,
    },
  );
}

/**
 * 新增发料单明细项
 * @param orderId 发料单ID
 * @param productId 航材ID
 * @param orderNum 发料数量
 * @param description 备注
 */
export function addDetail(orderId: string, productId: string, orderNum: number, description?: string): Promise<void> {
  return defHttp.post<void>(
    {
      url: baseUrl + '/detail',
      data: { orderId, productId, orderNum, description },
    },
    {
      region,
      contentType: ContentTypeEnum.JSON,
    },
  );
}

/**
 * 删除发料单明细项
 * @param detailId 发料单明细ID
 */
export function deleteDetail(detailId: string): Promise<void> {
  return defHttp.delete<void>(
    {
      url: `${baseUrl}/detail?detailId=${encodeURIComponent(detailId)}`,
    },
    {
      region,
    },
  );
}
