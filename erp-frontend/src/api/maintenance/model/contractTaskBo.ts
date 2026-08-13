export interface ContractTaskBo {
    /**
     * ID
     */
    id: string;
  
    /**
     * 合同编号
     */
    code: string;
  
    /**
     * 合同类型
     */
    contractType: string;
  
    /**
     * 客户信息
     */
    customerCode: string;
  
    /**
     * 机型
     */
    aircraftType: string;
  
    /**
     * 件号
     */
    partNumber: string;
  
    /**
     * 序号
     */
    serialNumber: string;
  
    /**
     * 维修类型
     */
    repairType: string;
  
    /**
     * 其他维修需求
     */
    otherRepairRequirements: string;
  
    /**
     * 合同时间
     */
    contractTime: string;
  
    /**
     * 入库时间
     */
    storageTime: string;
  
    /**
     * 计划完工时间
     */
    plannedCompletionTime: string;
  
    /**
     * 状态
     */
    available: boolean;
  
    /**
     * 备注
     */
    description: string;
  
    /**
     * 派发至
     */
    dispatch: string;
  
    /**
     * 创建时间
     */
    createTime: string;
  }
  