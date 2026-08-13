package com.lframework.xingyun.shkb.bo.material.out;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 序列号库存查询BO
 */
public class SerialStockBo implements Serializable {

  @ApiModelProperty("序列号库存ID")
  private String id;

  @ApiModelProperty("序列号")
  private String serialNumber;

  @ApiModelProperty("批次号")
  private String batchNumber;

  @ApiModelProperty("仓库ID")
  private String scId;

  @ApiModelProperty("仓库名称")
  private String scName;

  @ApiModelProperty("商品ID")
  private String productId;

  @ApiModelProperty("商品名称")
  private String productName;

  @ApiModelProperty("件号")
  private String partNumberCode;

  @ApiModelProperty("机型")
  private String machineType;

  @ApiModelProperty("在库状态（1在库，0已出库）")
  private Integer stockStatus;

  @ApiModelProperty("生产日期")
  private LocalDate productionDate;

  @ApiModelProperty("失效日期")
  private LocalDate expiryDate;

  public SerialStockBo() {}

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public String getSerialNumber() { return serialNumber; }
  public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

  public String getBatchNumber() { return batchNumber; }
  public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

  public String getScId() { return scId; }
  public void setScId(String scId) { this.scId = scId; }

  public String getScName() { return scName; }
  public void setScName(String scName) { this.scName = scName; }

  public String getProductId() { return productId; }
  public void setProductId(String productId) { this.productId = productId; }

  public String getProductName() { return productName; }
  public void setProductName(String productName) { this.productName = productName; }

  public String getPartNumberCode() { return partNumberCode; }
  public void setPartNumberCode(String partNumberCode) { this.partNumberCode = partNumberCode; }

  public String getMachineType() { return machineType; }
  public void setMachineType(String machineType) { this.machineType = machineType; }

  public Integer getStockStatus() { return stockStatus; }
  public void setStockStatus(Integer stockStatus) { this.stockStatus = stockStatus; }

  public LocalDate getProductionDate() { return productionDate; }
  public void setProductionDate(LocalDate productionDate) { this.productionDate = productionDate; }

  public LocalDate getExpiryDate() { return expiryDate; }
  public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
}
