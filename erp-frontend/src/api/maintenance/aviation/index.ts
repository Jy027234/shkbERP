import { ContractTaskBo } from "../model/contractTaskBo";
import { defHttp } from "/@/utils/http/axios";
import { PageResult } from "/@/api/model/pageResult";

// 维修状态枚举
export enum RepairStatus {
  WAIT_CHECK = "WAIT_CHECK",       // 待检查
  CHECKING = "CHECKING",           // 检查中
  REPAIRING = "REPAIRING",         // 维修中
  WAITING_FOR_PARTS = "WAITING_FOR_PARTS", // 等料暂停
  PAUSED_OTHER = "PAUSED_OTHER",   // 其他暂停
  TESTING = "TESTING",             // 测试中
  COMPLETED = "COMPLETED"          // 完工
}

// 添加维修状态记录参数接口
export interface AddRepairStatusRecordVo {
  taskId: string;           // 任务ID
  repairStatus: string;     // 维修状态
  description?: string;     // 备注说明
}

// 任务状态枚举
export enum TaskStatus {
  PENDING = "PENDING",             // 待处理
  OFFLINE_APPRAISAL = "OFFLINE_APPRAISAL", // 线下鉴定
  DISPATCHED = "DISPATCHED",       // 已派发
  REPAIRING = "REPAIRING",         // 维修中
  RETURNED = "RETURNED",           // 退修
  COMPLETED = "COMPLETED",         // 完成
  CLOSED = "CLOSED"                // 关闭
}

// 修改任务状态参数接口
export interface UpdateTaskStatusVo {
  taskId: string;           // 任务ID
  taskStatus: string;       // 任务状态
  reason?: string;          // 退修原因
}

// 线下鉴定参数接口
export interface OfflineAppraisalVo {
  id: string;       // 任务ID
  approved: boolean; // 是否通过
  description?: string; // 备注
}

// 任务派发参数接口
export interface DispatchTaskVo {
  id: string;       // 任务ID
  taskUserId: string; // 派发给用户ID
}

const baseUrl = '/shkb/contract-task';
const region = 'cloud-api';

/**
 * 根据ID查询
 * @param id
 */
export function get(id: string): Promise<ContractTaskBo> {
  return defHttp.get<ContractTaskBo>(
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
 * 查询列表
 * @param params
 */
export function query(params): Promise<PageResult<ContractTaskBo>> {
  return defHttp.get<PageResult<ContractTaskBo>>(
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
 * 线下鉴定
 * @param data 包含任务ID、是否通过和备注的请求参数
 */
export function offlineAppraisal(data: OfflineAppraisalVo): Promise<void> {
  return defHttp.post<void>(
    {
      url: `${baseUrl}/offline-appraisal`,
      data,
    },
    {
      region,
    },
  );
}

/**
 * 任务派发
 * @param data 包含任务ID和派发给用户ID的请求参数
 * @returns 如果创建了新合同，则返回新合同ID；否则返回null
 */
export function dispatchTask(data: DispatchTaskVo): Promise<string | null> {
  return defHttp.post<string | null>(
    {
      url: `${baseUrl}/dispatch`,
      data,
    },
    {
      region,
    },
  );
}

/**
 * 添加任务维修执行状态记录
 * @param data 维修状态记录数据
 */
export function addRepairStatusRecord(data: AddRepairStatusRecordVo): Promise<string> {
  return defHttp.post<string>(
    {
      url: baseUrl + '/repair-status/add',
      data,
    },
    {
      region,
      errorMessageMode: 'message',
    },
  );
}

/**
 * 获取任务维修执行状态记录列表
 * @param taskId 任务ID
 */
export function getRepairStatusRecords(taskId: string): Promise<any[]> {
  return defHttp.get<any[]>(
    {
      url: baseUrl + '/repair-status/list',
      params: { taskId },
    },
    {
      region,
    },
  );
}

/**
 * 修改任务状态
 * @param data 任务状态信息
 */
export function updateTaskStatus(data: UpdateTaskStatusVo): Promise<void> {
  return defHttp.put<void>(
    {
      url: baseUrl + '/status',
      data,
    },
    {
      region,
      errorMessageMode: 'message',
    },
  );
}