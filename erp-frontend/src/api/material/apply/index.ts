import { defHttp } from "@/utils/http/axios";
import { PageResult } from "@/api/model/pageResult";

// 领料申请查询参数接口
export interface QueryMaterialApplyParams {
  pageIndex: number;
  pageSize: number;
  applyCode?: string;
  contractCode?: string;
  createTimeStart?: string;
  createTimeEnd?: string;
  // 审批状态（0：待审批，1：审批通过，2：审批拒绝）
  approvalStatus?: number;
  // 是否已创建发料单（true：已创建，false：未创建）
  hasMaterialOrder?: boolean;
}

// 领料申请BO接口
export interface MaterialApplyBo {
  id: string;
  applyCode: string;
  contractCode?: string;
  machineTypeName?: string;
  partNumberName?: string;
  replacementPartCode?: string;
  nonReplacementPartCode?: string;
  createTime: string;
  approvalStatus: number;
  approvalStatusText: string;
  approvalTime?: string;
  remark?: string;
  // 是否已创建发料单
  hasMaterialOrder?: boolean;
}

// 审批领料申请参数接口
export interface ApproveMaterialApplyVo {
  ids: string[];
  approved: boolean;
  comment?: string;
}

const baseUrl = '/shkb/contract-task';
const region = 'cloud-api';

/**
 * 查询领料申请列表
 * @param params 查询参数
 */
export function query(params: QueryMaterialApplyParams): Promise<PageResult<MaterialApplyBo>> {
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
 * 审批领料申请
 * @param data 审批参数
 */
export function approveMaterialApply(data: ApproveMaterialApplyVo): Promise<void> {
  return defHttp.post<void>(
    {
      url: `${baseUrl}/material-apply/approve`,
      data,
    },
    {
      region,
    },
  );
}

/**
 * 发料单申请补提重审（按申请编号）
 */
export function reopenByApplyCode(applyCode: string): Promise<void> {
  return defHttp.post<void>(
    {
      url: `${baseUrl}/material-apply/reopen?applyCode=${encodeURIComponent(applyCode)}`,
    },
    {
      region,
    },
  );
}

/**
 * 获取任务换件清单
 * @param taskId 任务ID
 * @param scId 仓库ID
 * @returns 换件清单列表
 */
export function getTaskPartList(taskId: string, scId: string) {
  return defHttp.get(
    {
      url: `${baseUrl}/parts-list`,
      params: { taskId, scId },
    },
    {
      region,
    },
  );
}

/**
 * 发料出库
 * @param params 发料参数
 * @returns 发料结果
 */
export function issueMaterial(params: {
  taskId: string;
  scId: string; // 仓库ID
  remark?: string;
}) {
  return defHttp.post(
    {
      url: `${baseUrl}/issue-material`,
      data: params,
    },
    {
      region,
    },
  );
}
