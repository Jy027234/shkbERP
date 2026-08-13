import { QueryContractBo, CreateContractVo, UpdateContractVo } from "./model/contractBo";
import { GetContractBo } from './model/getContractBo';
import { PageResult } from "/@/api/model/pageResult";
import * as ContractApi from "./index";
import { CONTRACT_TYPE } from "/@/enums/biz/contractType";

/**
 * 根据ID查询民航维修合同详情
 * @param id
 */
export function get(id: string): Promise<GetContractBo> {
  return ContractApi.get(id);
}

/**
 * 查询民航维修合同列表
 * @param params
 */
export function query(params): Promise<PageResult<QueryContractBo>> {
  // 设置合同类型为民航维修
  params.contractType = CONTRACT_TYPE.get('AVIATION')!.code;
  return ContractApi.query(params);
}

/**
 * 创建民航维修合同
 * @param data
 */
export function create(data: CreateContractVo): Promise<void> {
  // 设置合同类型为民航维修
  data.contractType = CONTRACT_TYPE.get('AVIATION')!.code;
  return ContractApi.create(data);
}

/**
 * 修改民航维修合同
 * @param data
 */
export function update(data: UpdateContractVo): Promise<void> {
  // 确保合同类型为民航维修（AVIATION）的数字枚举值 1
  // 在后端，ContractType.AVIATION 对应数字 1
  data.contractType = 1; // 强制设置为 AVIATION 的枚举值
  
  return ContractApi.update(data);
}
