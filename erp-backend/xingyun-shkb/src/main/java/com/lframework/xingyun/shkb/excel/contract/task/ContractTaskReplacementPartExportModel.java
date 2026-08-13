package com.lframework.xingyun.shkb.excel.contract.task;

import com.alibaba.excel.annotation.ExcelProperty;
import com.lframework.starter.web.core.components.excel.ExcelModel;
import com.lframework.xingyun.shkb.bo.contract.task.ContractTaskProductBo;
import com.lframework.xingyun.shkb.entity.Contract;
import com.lframework.xingyun.shkb.entity.ContractTask;
import lombok.Data;

/**
 * 合同任务必换件清单导出模型
 */
@Data
public class ContractTaskReplacementPartExportModel implements ExcelModel {

  /** 合同号 */
  @ExcelProperty("合同号")
  private String contractCode;

  /** 必换件单号（合同号+BHJ） */
  @ExcelProperty("必换件单号")
  private String replacementPartCode;

  /** 名称（合同名称或任务名称） */
  @ExcelProperty("名称")
  private String contractName;

  /** 机型 */
  @ExcelProperty("机型")
  private String machineTypeName;

  /** 产品序号 */
  @ExcelProperty("产品序号")
  private String serialNumber;

  /** 工卡号 */
  @ExcelProperty("工卡号")
  private String workCardCode;

  /** 序号 */
  @ExcelProperty("序号")
  private Integer index;

  /** 名称（零件名称） */
  @ExcelProperty("名称")
  private String productName;

  /** 件号 */
  @ExcelProperty("件号")
  private String partNumberCode;

  /** 数量 */
  @ExcelProperty("数量")
  private Integer quantity;

  /** 换件原因 */
  @ExcelProperty("换件原因")
  private String replacementReason;

  /** 单位 */
  @ExcelProperty("单位")
  private String unit;

  /** 价格 */
  @ExcelProperty("价格")
  private String price;

  /** 备注 */
  @ExcelProperty("备注")
  private String remark;

  /** 说明 */
  @ExcelProperty("说明")
  private String note;

  public ContractTaskReplacementPartExportModel() {
  }

  public ContractTaskReplacementPartExportModel(ContractTask task,
      Contract contract,
      ContractTaskProductBo part,
      int index) {
    // 头部信息：合同号、名称、序号
    if (contract != null) {
      this.contractCode = contract.getCode();
      this.contractName = contract.getName();
      this.serialNumber = contract.getSerialNumber();
    } else if (task != null) {
      this.contractCode = task.getContractCode();
      this.contractName = task.getContractName();
      this.serialNumber = task.getSerialNumber();
    }

    // 必换件单号 = 合同号 + "BHJ"
    if (this.contractCode != null) {
      this.replacementPartCode = this.contractCode + "BHJ";
    }

    // 机型优先从必换件记录中取，保持与列表接口一致
    if (part != null) {
      if (part.getMachineTypeName() != null && !part.getMachineTypeName().isEmpty()) {
        this.machineTypeName = part.getMachineTypeName();
      } else if (part.getProductMachineTypeName() != null && !part.getProductMachineTypeName().isEmpty()) {
        this.machineTypeName = part.getProductMachineTypeName();
      }
    }
    // 如果记录中没有机型，再尝试从任务上补充
    if (this.machineTypeName == null && task != null) {
      this.machineTypeName = task.getMachineTypeName();
    }

    if (part != null) {
      this.workCardCode = part.getWorkCardCode();
      this.index = index;
      this.productName = part.getProductName();
      this.partNumberCode = part.getPartNumberCode();

      // 数量、单位等字段避免为 null，防止 EasyExcel 在模板填充时 NPE
      Integer qty = part.getQuantity();
      this.quantity = qty != null ? qty : 0;

      String unitVal = part.getProductUnit();
      this.unit = unitVal != null ? unitVal : "";
    } else {
      this.index = index;
      this.quantity = 0;
      this.unit = "";
    }

    // 换件原因、价格、备注、说明目前没有明确字段，按需求留空，由人工填写
    this.replacementReason = "";
    this.price = "";
    this.remark = "";
    this.note = "";
  }
}
