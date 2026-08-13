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
  COMPLETED = "COMPLETED"          // 完工
}

// 添加维修状态记录参数接口
export interface AddRepairStatusRecordVo {
  taskId: string;           // 任务ID
  repairStatus: RepairStatus; // 维修状态
  description?: string;     // 备注说明
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

// 修改放行文件编号参数接口
export interface UpdateApprovalFileNumberVo {
  id: string;               // 任务ID
  approvalFileNumber: string; // 放行文件编号
}

export interface QueryContractTaskParams {
  pageIndex?: number;
  pageSize?: number;
  taskType?: string;
  contractCode?: string;
  customerId?: string;
  machineTypeId?: string;
  partNumberId?: string;
  partNumberCode?: string;
  storageTimeStart?: string;
  storageTimeEnd?: string;
  plannedCompletionTimeStart?: string;
  plannedCompletionTimeEnd?: string;
  taskStatus?: string;
  repairStatus?: string;
  approvalFileNumber?: string;
}

export interface UpdateContractTaskVo {
  id: string;
  machineTypeId: string;
  partNumberId: string;
  serialNumber: string;
  repairTypeIds: string[];
  otherRepairRequirements?: string;
  storageTime: string;
  plannedCompletionTime: string;
  taskStatus: string;
  taskType: string;
  description?: string;
}

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
 * 修改合同任务
 */
export function update(data: UpdateContractTaskVo): Promise<void> {
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
 * 导出任务必换件清单
 * @param taskId 任务ID
 */
export function exportTaskReplacementParts(taskId: string) {
  return defHttp.post<Blob>(
    {
      url: `${baseUrl}/replacement-parts/export?taskId=${encodeURIComponent(taskId)}`,
      responseType: 'blob',
    },
    {
      region,
      isReturnNativeResponse: true,
    },
  );
}

/**
 * 修改合同任务放行文件编号
 * @param data 包含任务ID和放行文件编号的请求参数
 */
export function updateApprovalFileNumber(data: UpdateApprovalFileNumberVo): Promise<void> {
  return defHttp.put<void>(
    {
      url: `${baseUrl}/approval-file-number`,
      data,
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
export function query(params: QueryContractTaskParams): Promise<PageResult<ContractTaskBo>> {
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
 * 查询全部合同任务。复用分页接口，避免新增一个不受控的大结果集端点。
 */
export async function queryAll(params: QueryContractTaskParams): Promise<ContractTaskBo[]> {
  const pageSize = 200;
  const results: ContractTaskBo[] = [];
  let pageIndex = 1;

  while (true) {
    const page = await query({ ...params, pageIndex, pageSize });
    if (page.datas) {
      results.push(...page.datas);
    }
    if (!page.hasNext) {
      return results;
    }
    pageIndex += 1;
  }
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
 * 获取任务必换件列表
 * @param taskId 任务ID
 * @returns 必换件列表
 */
export function getTaskReplacementParts(taskId: string): Promise<any[]> {
  return defHttp.get<any[]>(
    {
      url: `${baseUrl}/replacement-parts`,
      params: { taskId },
    },
    {
      region,
    },
  );
}

/**
 * 保存任务必换件数量
 * @param data 包含任务ID和必换件列表的请求参数
 */
export interface TaskReplacementPartProduct {
  workCardId: string; // 工卡ID
  productId: string;  // 航材ID
  quantity: number;   // 数量
}

export interface SaveTaskReplacementPartsQuantityVo {
  taskId: string;                      // 任务ID
  products: TaskReplacementPartProduct[]; // 必换件列表
}

export function saveTaskReplacementPartsQuantity(data: SaveTaskReplacementPartsQuantityVo): Promise<void> {
  return defHttp.post<void>(
    {
      url: `${baseUrl}/replacement-parts/save`,
      data,
    },
    {
      region,
    },
  );
}

/**
 * 获取任务非必换件记录列表
 * @param taskId 任务ID
 * @returns 非必换件记录列表
 */
export interface TaskNonPartProductBo {
  id: string;           // 记录ID
  productId: string;     // 航材ID
  productCode: string;   // 航材编码
  productName: string;   // 航材名称
  machineTypeName?: string; // 机型名称
  partNumber?: string;   // 件号
  quantity: number;      // 数量
  reason?: string;       // 原因说明
  createTime: string;    // 创建时间
  createBy: string;      // 创建人ID
  createByName: string;  // 创建人名称
  attachments?: Array<{
    id: string;          // 附件ID
    name: string;        // 附件名称
    url: string;         // 附件URL
  }>;                    // 附件列表
}

export function getTaskNonPartProducts(taskId: string): Promise<TaskNonPartProductBo[]> {
  return defHttp.get<TaskNonPartProductBo[]>(
    {
      url: `${baseUrl}/non-part/list`,
      params: { taskId },
    },
    {
      region,
    },
  );
}

/**
 * 保存任务非必换件记录
 * @param data 包含任务ID、航材信息和文件列表的请求参数
 * @returns 新创建的非必换件记录ID
 */
export interface TaskNonPartFileVo {
  url: string;       // 文件访问路径
  fileSuffix: string; // 文件后缀
  fileSize: string;   // 文件大小
  fileName: string;   // 文件名称
  contentType: string; // ContentType
}

export interface SaveTaskNonPartProductVo {
  taskId: string;     // 任务ID
  productId: string;   // 航材ID
  quantity: number;    // 数量
  reason?: string;     // 原因说明
  files: TaskNonPartFileVo[]; // 附件列表
}

/**
 * 保存任务非必换件记录（新版本，支持直接上传文件）
 * @param taskId 任务ID
 * @param productId 航材ID
 * @param quantity 数量
 * @param reason 原因说明
 * @param files 文件列表
 */
export function saveTaskNonPartProduct(
  taskId: string,
  productId: string,
  quantity: number,
  reason: string | undefined,
  files: File[]
): Promise<string> {
  // 创建FormData对象用于提交multipart/form-data请求
  const formData = new FormData();
  
  // 添加所有必要的参数到FormData
  formData.append('taskId', taskId);
  formData.append('productId', productId);
  formData.append('quantity', quantity.toString());
  
  // 添加可选参数
  if (reason) {
    formData.append('reason', reason);
  }
  
  // 添加多个文件
  if (files && files.length > 0) {
    files.forEach(file => {
      formData.append('files', file);
    });
  }
  
  return defHttp.post<string>(
    {
      url: `${baseUrl}/non-part/save`,
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
 * 删除任务非必换件记录
 * @param id 非必换件记录ID
 */
export function deleteTaskNonPartProduct(id: string): Promise<void> {
  return defHttp.delete<void>(
    {
      url: `${baseUrl}/non-part/delete/${id}`,
    },
    {
      region,
    },
  );
}



/**
 * 修改任务非必换件数量
 * @param data 包含非必换件记录ID、数量和原因的请求参数
 */
export interface UpdateTaskNonPartProductVo {
  id: string;       // 非必换件记录ID
  quantity: number;  // 数量
  reason?: string;   // 原因说明
}

export function updateTaskNonPartProductQuantity(data: UpdateTaskNonPartProductVo): Promise<void> {
  return defHttp.put<void>(
    {
      url: `${baseUrl}/non-part/update/quantity`,
      data,
    },
    {
      region,
    },
  );
}

/**
 * 批量修改任务非必换件数量
 * @param data 包含多个非必换件记录的数量修改信息
 */
export interface BatchUpdateTaskNonPartProductVo {
  taskId: string;
  records: UpdateTaskNonPartProductVo[];
}

export function batchUpdateTaskNonPartProductQuantity(data: BatchUpdateTaskNonPartProductVo): Promise<void> {
  return defHttp.put<void>(
    {
      url: baseUrl + '/non-part/batch-update/quantity',
      data,
    },
    {
      region,
      errorMessageMode: 'message',
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
 * 领料申请参数接口
 */
export interface CreateMaterialApplyVo {
  taskId: string;     // 任务ID
  remark?: string;    // 备注
}

/**
 * 发起领料申请
 * @param data 包含任务ID和备注的请求参数
 * @returns 领料申请ID
 */
export function createMaterialApply(data: CreateMaterialApplyVo): Promise<string> {
  return defHttp.post<string>(
    {
      url: `${baseUrl}/material-apply/create`,
      data,
    },
    {
      region,
      errorMessageMode: 'message',
    },
  );
}

/**
 * 领料申请查询参数接口
 */
export interface QueryMaterialApplyVo {
  pageIndex?: number;     // 页码
  pageSize?: number;      // 每页条数
  contractCode?: string;  // 合同编号
  applyCode?: string;     // 申请编号
  startTime?: string;     // 申请开始时间
  endTime?: string;       // 申请结束时间
}

/**
 * 领料申请返回数据接口
 */
export interface MaterialApplyBo {
  id: string;              // 申请ID
  taskId: string;          // 任务ID
  applyCode: string;       // 申请编号
  contractCode: string;    // 合同编号
  machineType?: string;    // 机型
  partNumber?: string;     // 件号
  createTime: string;      // 申请时间
  approvalStatus: number;  // 审批状态（0：审批中，1：审批通过，2：审批拒绝）
  approvalStatusText: string; // 审批状态文本
  remark?: string;         // 备注
  approvalTime?: string;   // 审批时间
}

/**
 * 查询领料申请列表
 * @param params 查询参数
 * @returns 领料申请列表分页结果
 */
export function queryMaterialApply(params: QueryMaterialApplyVo): Promise<PageResult<MaterialApplyBo>> {
  return defHttp.get<PageResult<MaterialApplyBo>>(
    {
      url: `${baseUrl}/material-apply/query`,
      params,
    },
    {
      region,
    },
  );
}

/**
 * 替换必换件
 * @param id 任务必换件记录ID
 * @param productId 新的航材ID
 */
export function replaceReplacementPart(id: string, productId: string): Promise<void> {
  return defHttp.post<void>(
    {
      url: `${baseUrl}/replacement-part/replace?id=${encodeURIComponent(id)}&productId=${encodeURIComponent(productId)}`,
    },
    {
      region,
      errorMessageMode: 'message',
    },
  );
}

/**
 * 还原必换件
 * @param id 任务必换件记录ID
 */
export function restoreReplacementPart(id: string): Promise<void> {
  return defHttp.post<void>(
    {
      url: `${baseUrl}/replacement-part/restore?id=${encodeURIComponent(id)}`,
    },
    {
      region,
      errorMessageMode: 'message',
    },
  );
}
