import { defHttp } from "/@/utils/http/axios";
import { PageResult } from "/@/api/model/pageResult";

// 工卡Bo接口
export interface WorkCardBo {
  id: string;
  code: string;
  name: string;
  machineTypeId: string;
  machineTypeName: string;
  partNumberId: string;
  partNumberName: string;
  partNumber: string;
  customerId: string;
  customerName: string;
  repairTypeId?: string;
  repairTypeName?: string;
  approvalDate: string;
  version?: string;
  available: boolean;
  description: string;
  createBy: string;
  createTime: string;
}

// 工卡必换件Bo接口
export interface WorkCardProductBo {
  id: string;
  productId: string;
  productCode: string;
  productName: string;
  productSpec: string;
  productUnit: string;
  workCardId: string;
  quantity: number;
}

// 创建工卡参数接口
export interface CreateWorkCardVo {
  code: string;
  name: string;
  partNumberId: string;
  repairTypeId?: string;
  customerId?: string;
  approvalDate?: string;
  available: boolean;
  description?: string;
  version?: string;
}

// 更新工卡参数接口
export interface UpdateWorkCardVo extends CreateWorkCardVo {
  id: string;
}

// 查询工卡参数接口
export interface QueryWorkCardVo {
  code?: string;
  name?: string;
  machineTypeId?: string;
  partNumberCode?: string;
  customerId?: string;
  repairTypeId?: string;
  available?: boolean;
  createTimeStart?: string;
  createTimeEnd?: string;
  pageIndex?: number;
  pageSize?: number;
}

// 工卡必换件参数接口
export interface WorkCardProductVo {
  workCardId: string;
  productIds: string[];
}

// 批量修改工卡必换件数量参数接口
export interface BatchUpdateWorkCardProductVo {
  workCardId: string;
  products: {
    id: string;
    quantity: number;
  }[];
}

const baseUrl = '/shkb/work-card';
const region = 'cloud-api';

/**
 * 查询列表
 * @param params 查询参数
 */
export function query(params: QueryWorkCardVo): Promise<PageResult<WorkCardBo>> {
  return defHttp.get<PageResult<WorkCardBo>>(
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
 * @param id 工卡ID
 */
export function get(id: string): Promise<WorkCardBo> {
  return defHttp.get<WorkCardBo>(
    {
      url: baseUrl,
      params: { id }
    },
    {
      region,
    },
  );
}

/**
 * 新增工卡
 * @param data 工卡数据
 */
export function create(data: CreateWorkCardVo): Promise<string> {
  return defHttp.post<string>(
    {
      url: baseUrl,
      data,
    },
    {
      region,
    },
  );
}

/**
 * 修改工卡
 * @param data 工卡数据
 */
export function update(data: UpdateWorkCardVo): Promise<void> {
  return defHttp.post<void>(
    {
      url: baseUrl + '/update',
      data,
    },
    {
      region,
    },
  );
}

/**
 * 删除工卡
 * @param id 工卡ID
 */
export function deleteById(id: string): Promise<void> {
  return defHttp.get<void>(
    {
      url: baseUrl + '/delete',
      params: { id }
    },
    {
      region,
    },
  );
}

/**
 * 查询工卡必换件列表
 * @param workCardId 工卡ID
 */
export function getProducts(workCardId: string): Promise<WorkCardProductBo[]> {
  return defHttp.get<WorkCardProductBo[]>(
    {
      url: baseUrl + '/products',
      params: { workCardId }
    },
    {
      region,
    },
  );
}

/**
 * 批量添加工卡必换件
 * @param data 必换件数据
 */
export function batchAddProducts(data: WorkCardProductVo): Promise<void> {
  return defHttp.post<void>(
    {
      url: baseUrl + '/product/add',
      data,
    },
    {
      region,
    },
  );
}

/**
 * 批量删除工卡必换件
 * @param data 必换件数据
 */
export function batchDeleteProducts(data: WorkCardProductVo): Promise<void> {
  return defHttp.post<void>(
    {
      url: baseUrl + '/product/delete',
      data,
    },
    {
      region,
    },
  );
}

/**
 * 批量修改工卡必换件数量
 * @param data 必换件数量数据
 */
export function batchUpdateProductQuantity(data: BatchUpdateWorkCardProductVo): Promise<void> {
  return defHttp.post<void>(
    {
      url: baseUrl + '/product/update-quantity',
      data,
    },
    {
      region,
    },
  );
}

// 导出API对象
export const workCardApi = {
  query,
  get,
  create,
  update,
  delete: deleteById,
  getProducts,
  batchAddProducts,
  batchDeleteProducts,
  batchUpdateProductQuantity
};
