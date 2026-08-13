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

// 添加调试日志
const debug = true;
function log(...args: any[]) {
  if (debug) {
    console.log('[Contract API]', ...args);
  }
}

const baseUrl = '/shkb/contract';
const region = 'cloud-api';

/**
 * 根据ID查询
 * @param id
 */
export function get(id: string): Promise<GetContractBo> {
  console.log('[Contract API] 获取合同详情, ID:', id);
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
  ).then(res => {
    console.log('[Contract API] 合同详情数据:', res);
    return res;
  });
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
  console.log('[Contract API] 生成合同任务, 参数:', data);
  return defHttp.post<void>(
    {
      url: `${baseUrl}/create-task`,
      data,
    },
    {
      region,
    },
  ).then(res => {
    console.log('[Contract API] 生成合同任务成功:', res);
    return res;
  });
}

/**
 * 修改合同状态
 * @param data 包含合同ID、状态和备注的请求参数
 */
export function updateContractStatus(data: UpdateContractStatusVo): Promise<void> {
  log('修改合同状态, 参数:', data);
  return defHttp.put<void>(
    {
      url: `${baseUrl}/status`,
      data,
    },
    {
      region,
      errorMessageMode: 'message',
    },
  ).then(res => {
    log('修改合同状态成功:', res);
    return res;
  });
}

/**
 * 恢复合同（从关闭状态恢复到关闭前的状态）
 * @param id 合同ID
 */
export function restoreContract(id: string): Promise<void> {
  log('恢复合同, ID:', id);
  return defHttp.post<void>(
    {
      url: `${baseUrl}/restore?id=${encodeURIComponent(id)}`,
    },
    {
      region,
      errorMessageMode: 'message',
    },
  ).then(res => {
    log('恢复合同成功:', res);
    return res;
  });
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
