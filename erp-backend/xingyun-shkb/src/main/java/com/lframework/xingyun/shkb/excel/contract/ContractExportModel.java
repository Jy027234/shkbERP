package com.lframework.xingyun.shkb.excel.contract;

import com.alibaba.excel.annotation.ExcelProperty;
import com.lframework.starter.web.core.components.excel.ExcelModel;
import com.lframework.xingyun.shkb.bo.contract.QueryContractBo;
import com.lframework.xingyun.shkb.bo.contract.GetContractBo;
import java.time.format.DateTimeFormatter;
import lombok.Data;

/**
 * 合同导出模型
 */
@Data
public class ContractExportModel implements ExcelModel {

  /** 合同进度 */
  @ExcelProperty("合同进度")
  private String contractStatusName;

  /** 维修状态 */
  @ExcelProperty("维修状态")
  private String repairStatusName;

  /** 合同编号 */
  @ExcelProperty("合同编号")
  private String code;

  /** 合同名称 */
  @ExcelProperty("合同名称")
  private String name;

  /** 客户名称 */
  @ExcelProperty("客户")
  private String customerName;

  /** 机型 */
  @ExcelProperty("机型")
  private String machineTypeName;

  /** 件号 */
  @ExcelProperty("件号")
  private String partNumberCode;

  /** 产品序号 */
  @ExcelProperty("产品序号")
  private String serialNumber;

  /** 维修类型（名称列表） */
  @ExcelProperty("维修类型")
  private String repairTypes;

  /** 其他维修需求 */
  @ExcelProperty("其他维修需求")
  private String otherRepairRequirements;

  /** 合同时间 */
  @ExcelProperty("合同时间")
  private String contractTime;

  /** 入库时间 */
  @ExcelProperty("入库时间")
  private String storageTime;

  /** 计划完工时间 */
  @ExcelProperty("计划完工时间")
  private String plannedCompletionTime;

  /** 实际完工时间 */
  @ExcelProperty("实际完工时间")
  private String actualCompletionTime;

  /** 发货时间 */
  @ExcelProperty("发货时间")
  private String deliveryTime;

  /** 合同报价 */
  @ExcelProperty("合同报价")
  private String contractPrice;

  /** 更换件价格 */
  @ExcelProperty("更换件价格")
  private String replacementPartPrice;

  /** 是否启用 */
  @ExcelProperty("是否启用")
  private String availableText;

  /** 创建人 */
  @ExcelProperty("创建人")
  private String createBy;

  /** 创建时间 */
  @ExcelProperty("创建时间")
  private String createTime;

  /** 备注 */
  @ExcelProperty("备注")
  private String description;

  public ContractExportModel() {}

  public ContractExportModel(QueryContractBo bo) {
    this.contractStatusName = bo.getContractStatusName();
    this.repairStatusName = bo.getRepairStatusName();
    this.code = bo.getCode();
    this.name = bo.getName();
    this.customerName = bo.getCustomerName();
    this.machineTypeName = bo.getMachineTypeName();
    this.partNumberCode = bo.getPartNumberCode();
    this.serialNumber = bo.getSerialNumber();

    // 维修类型名称拼接
    if (bo.getRepairTypes() != null && !bo.getRepairTypes().isEmpty()) {
      this.repairTypes = String.join(",", bo.getRepairTypes().stream()
          .map(r -> r.getName())
          .toArray(String[]::new));
    }
    this.otherRepairRequirements = bo.getOtherRepairRequirements();

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    if (bo.getContractTime() != null) {
      this.contractTime = bo.getContractTime().format(dtf);
    }
    if (bo.getStorageTime() != null) {
      this.storageTime = bo.getStorageTime().format(dtf);
    }
    if (bo.getPlannedCompletionTime() != null) {
      this.plannedCompletionTime = bo.getPlannedCompletionTime().format(dtf);
    }
    if (bo.getActualCompletionTime() != null) {
      this.actualCompletionTime = bo.getActualCompletionTime().format(dtf);
    }
    if (bo.getDeliveryTime() != null) {
      this.deliveryTime = bo.getDeliveryTime().format(dtf);
    }

    if (bo.getContractPrice() != null) {
      this.contractPrice = bo.getContractPrice().toPlainString();
    }
    if (bo.getReplacementPartPrice() != null) {
      this.replacementPartPrice = bo.getReplacementPartPrice().toPlainString();
    }
    this.availableText = bo.getAvailable() != null && bo.getAvailable() ? "是" : "否";
    this.createBy = bo.getCreateBy();
    if (bo.getCreateTime() != null) {
      this.createTime = bo.getCreateTime().format(dtf);
    }
    this.description = bo.getDescription();
  }

  /**
   * 使用合同详情BO构建导出模型（用于按勾选ID导出）
   */
  public ContractExportModel(GetContractBo bo) {
    this.contractStatusName = bo.getContractStatusName();
    this.repairStatusName = bo.getRepairStatusName();
    this.code = bo.getCode();
    this.name = bo.getName();
    this.customerName = bo.getCustomerName();
    this.machineTypeName = bo.getMachineTypeName();
    this.partNumberCode = bo.getPartNumberCode();
    this.serialNumber = bo.getSerialNumber();

    // 维修类型名称拼接
    if (bo.getRepairTypes() != null && !bo.getRepairTypes().isEmpty()) {
      this.repairTypes = String.join(",", bo.getRepairTypes().stream()
          .map(r -> r.getName())
          .toArray(String[]::new));
    }
    this.otherRepairRequirements = bo.getOtherRepairRequirements();

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    if (bo.getContractTime() != null) {
      this.contractTime = bo.getContractTime().format(dtf);
    }
    if (bo.getStorageTime() != null) {
      this.storageTime = bo.getStorageTime().format(dtf);
    }
    if (bo.getPlannedCompletionTime() != null) {
      this.plannedCompletionTime = bo.getPlannedCompletionTime().format(dtf);
    }
    if (bo.getActualCompletionTime() != null) {
      this.actualCompletionTime = bo.getActualCompletionTime().format(dtf);
    }
    if (bo.getDeliveryTime() != null) {
      this.deliveryTime = bo.getDeliveryTime().format(dtf);
    }

    if (bo.getContractPrice() != null) {
      this.contractPrice = bo.getContractPrice().toPlainString();
    }
    if (bo.getReplacementPartPrice() != null) {
      this.replacementPartPrice = bo.getReplacementPartPrice().toPlainString();
    }
    this.availableText = bo.getAvailable() != null && bo.getAvailable() ? "是" : "否";
    this.createBy = bo.getCreateBy();
    if (bo.getCreateTime() != null) {
      this.createTime = bo.getCreateTime().format(dtf);
    }
    this.description = bo.getDescription();
  }
}