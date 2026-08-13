import { defHttp } from '/@/utils/http/axios';
import { ResponseEnum } from '@/enums/httpEnum';
import { PageResult } from '@/api/model/pageResult';
import { QueryProductStockSerialVo } from '@/api/sc/stock/product-stock-serial/model/queryProductStockSerialVo';
import { QueryProductStockSerialBo } from '@/api/sc/stock/product-stock-serial/model/queryProductStockSerialBo';
import { UpdateProductStockSerialVo } from '@/api/sc/stock/product-stock-serial/model/updateProductStockSerialVo';
import { GetProductStockSerialBo } from '@/api/sc/stock/product-stock-serial/model/getProductStockSerialBo';

const baseUrl = '/stock/product/serial';
const region = 'cloud-api';

/**
 * 查询航材序列号库存
 */
export function query(params: QueryProductStockSerialVo): Promise<PageResult<QueryProductStockSerialBo>> {
  return defHttp.get<PageResult<QueryProductStockSerialBo>>(
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
export function exportList(params: QueryProductStockSerialVo): Promise<void> {
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
 * 查询序列号库存详情
 */
export function get(id: string): Promise<GetProductStockSerialBo> {
  return defHttp.get<GetProductStockSerialBo>(
    {
      url: baseUrl + '/' + id,
    },
    {
      region,
    },
  );
}

/**
 * 修改序列号库存信息
 */
export function updateInfo(params: UpdateProductStockSerialVo): Promise<void> {
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

/**
 * 修改序列号
 */
export function updateSerialNumber(params: {
  id: string;
  serialNumber: string;
}): Promise<void> {
  return defHttp.post<void>(
    {
      url: baseUrl + '/modify-serial-number',
      data: params,
    },
    {
      region,
    },
  );
}
