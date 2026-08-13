import { QueryContractBo, CreateContractVo, UpdateContractVo } from "./model/contractBo";
import { GetContractBo } from './model/getContractBo';
import { PageResult } from "/@/api/model/pageResult";
import * as ContractApi from "./index";
import { CONTRACT_TYPE } from "/@/enums/biz/contractType";

/**
 * 根据ID查询返厂WB合同详情
 * @param id
 */
export function get(id: string): Promise<GetContractBo> {
  return ContractApi.get(id);
}

/**
 * 查询返厂WB合同列表
 * @param params
 */
export function query(params): Promise<PageResult<QueryContractBo>> {
  // 设置合同类型为返厂WB
  params.contractType = CONTRACT_TYPE.get('FACTORY_WB')!.code;
  return ContractApi.query(params);
}

/**
 * 创建返厂WB合同
 * @param data
 */
export function create(data: CreateContractVo): Promise<void> {
  // 设置合同类型为返厂WB
  data.contractType = CONTRACT_TYPE.get('FACTORY_WB')!.code;
  return ContractApi.create(data);
}

/**
 * 修改返厂WB合同
 * @param data
 */
export function update(data: UpdateContractVo): Promise<void> {
  // 确保合同类型为返厂WB（RECEIVE_WB）的数字枚举值 2
  // 在后端，ContractType.RECEIVE_WB 对应数字 2
  data.contractType = 2; // 强制设置为 RECEIVE_WB 的枚举值
  
  return ContractApi.update(data);
}
