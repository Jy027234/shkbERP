import { QueryContractBo, CreateContractVo, UpdateContractVo, QueryContractVo } from "./model/contractBo";
import { GetContractBo } from "./model/getContractBo";
import { defHttp } from "/@/utils/http/axios";
import { PageResult } from "/@/api/model/pageResult";

// 生成合同任务请求参数接口
export interface CreateContractTaskVo {
  contractId: string;
}

// 修改合同状态请求参数接口
export interface UpdateContractStatusVo {
  contractId: string;
  contractStatus: string;
  remark?: string;
}

const baseUrl = '/shkb/contract';
const region = 'cloud-api';

/**
 * 根据ID查询
 * @param id
 */
export function get(id: string): Promise<GetContractBo> {
  return defHttp.get<GetContractBo>(
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
 * 查询合同列表
 * @param params
 */
export function query(params): Promise<PageResult<QueryContractBo>> {
  return defHttp.get<PageResult<QueryContractBo>>(
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
 * 创建合同
 * @param data
 */
export function create(data: CreateContractVo): Promise<void> {
  return defHttp.post<void>(
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
 * 修改合同
 * @param data
 */
export function update(data: UpdateContractVo): Promise<void> {
  return defHttp.put<void>(
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
 * 生成合同任务
 * @param data 包含合同ID的请求参数
 */
export function createContractTask(data: CreateContractTaskVo): Promise<void> {
  return defHttp.post<void>(
    {
      url: `${baseUrl}/create-task`,
      data,
    },
    {
      region,
    },
  );
}

/**
 * 修改合同状态
 * @param data 包含合同ID、状态和备注的请求参数
 */
export function updateContractStatus(data: UpdateContractStatusVo): Promise<void> {
  return defHttp.put<void>(
    {
      url: `${baseUrl}/status`,
      data,
    },
    {
      region,
      errorMessageMode: 'message',
    },
  );
}

/**
 * 导出合同
 * @param data 查询条件
 */
export function exportContract(data: QueryContractVo) {
  return defHttp.post<Blob>(
    {
      url: `${baseUrl}/export`,
      data,
      responseType: 'blob',
    },
    {
      region,
      isReturnNativeResponse: true,
    },
  );
}
