import { defHttp } from '/@/utils/http/axios';
import { ResponseEnum } from '@/enums/httpEnum';
import { PageResult } from '@/api/model/pageResult';
import { QueryProductStockBatchVo } from '@/api/sc/stock/product-stock-batch/model/queryProductStockBatchVo';
import { QueryProductStockBatchBo } from '@/api/sc/stock/product-stock-batch/model/queryProductStockBatchBo';
import { UpdateProductStockBatchVo } from '@/api/sc/stock/product-stock-batch/model/updateProductStockBatchVo';
import { GetProductStockBatchBo } from '@/api/sc/stock/product-stock-batch/model/getProductStockBatchBo';

const baseUrl = '/stock/product/batch';
const region = 'cloud-api';

/**
 * 查询航材批次库存
 */
export function query(params: QueryProductStockBatchVo): Promise<PageResult<QueryProductStockBatchBo>> {
  return defHttp.get<PageResult<QueryProductStockBatchBo>>(
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
export function exportList(params: QueryProductStockBatchVo): Promise<void> {
  return defHttp.get<void>(
    {
      url: baseUrl + '/export-view',
      params,
    },
    {
      region,
      responseType: ResponseEnum.BLOB,
    },
  );
}

/**
 * 查询批次库存详情
 */
export function get(id: string): Promise<GetProductStockBatchBo> {
  return defHttp.get<GetProductStockBatchBo>(
    {
      url: baseUrl + '/' + id,
    },
    {
      region,
    },
  );
}

/**
 * 修改批次库存信息
 */
export function updateInfo(params: UpdateProductStockBatchVo): Promise<void> {
  return defHttp.post<void>(
    {
      url: baseUrl + '/modify',
      data: params,
    },
    {
      region,
    },
  );
}
