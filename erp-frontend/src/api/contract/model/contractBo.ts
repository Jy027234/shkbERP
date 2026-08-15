export interface RepairTypeBo {
  id: string;
  code: string;
  name: string;
}

export interface QueryContractBo {
  id: string;
  code: string;
  name: string;
  contractType: number;
  contractTypeName: string;
  contractStatus: number;
  contractStatusName: string;
  customerId: string;
  customerName: string;
  mnemonicCode: string;
  partNumberId: string;
  partNumberCode: string;
  partNumberName: string;
  machineTypeId: string;
  machineTypeCode: string;
  machineTypeName: string;
  taskStatus?: string;
  repairStatus?: string;
  repairStatusName?: string;
  repairTypes: RepairTypeBo[];
  serialNumber?: string;
  otherRepairRequirements?: string;
  contractTime: string;
  storageTime?: string;
  plannedCompletionTime?: string;
  deliveryTime?: string;
  actualCompletionTime?: string;
  contractPrice?: number;
  replacementPartPrice?: number;
  available: boolean;
  description?: string;
  createBy: string;
  createByName?: string;
  createTime: string;
  updateBy: string;
  updateByName?: string;
  updateTime: string;
}

export interface CreateContractVo {
  code: string;
  name: string;
  contractType: number;
  customerId: string;
  partNumberId: string;
  repairTypeIds: string[];
  serialNumber?: string;
  otherRepairRequirements?: string;
  contractTime: string;
  storageTime?: string;
  plannedCompletionTime?: string;
  deliveryTime?: string;
  actualCompletionTime?: string;
  contractPrice?: number;
  replacementPartPrice?: number;
  description?: string;
  available?: boolean;
  // Resolved from the part number for display; the backend ignores this field.
  machineTypeId?: string;
}

export interface UpdateContractVo extends CreateContractVo {
  id: string;
  available: boolean;
}

export interface QueryContractVo {
  code?: string;
  name?: string;
  available?: boolean;
  contractType?: number;
  contractStatus?: number;
  taskStatus?: string;
  customerId?: string;
  partNumberId?: string;
  partNumberCode?: string;
  machineTypeId?: string;
  repairTypeIds?: string;
  serialNumber?: string;
  startContractTime?: string;
  endContractTime?: string;
  startStorageTime?: string;
  endStorageTime?: string;
  startPlannedCompletionTime?: string;
  endPlannedCompletionTime?: string;
  ids?: string;
  pageIndex?: number;
  pageSize?: number;
}
