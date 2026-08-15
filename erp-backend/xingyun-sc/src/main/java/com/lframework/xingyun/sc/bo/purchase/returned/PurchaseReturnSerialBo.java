package com.lframework.xingyun.sc.bo.purchase.returned;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lframework.starter.common.constants.StringPool;
import com.lframework.xingyun.sc.entity.ProductStockSerial;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import lombok.Data;

/**
 * 采购退货可选序列号。
 */
@Data
public class PurchaseReturnSerialBo {

  @ApiModelProperty("序列号库存ID")
  private String id;

  @ApiModelProperty("序列号")
  private String serialNumber;

  @ApiModelProperty("批次库存ID")
  private String batchId;

  @ApiModelProperty("生产日期")
  @JsonFormat(pattern = StringPool.DATE_PATTERN)
  private LocalDate productionDate;

  @ApiModelProperty("失效日期")
  @JsonFormat(pattern = StringPool.DATE_PATTERN)
  private LocalDate expiryDate;

  public PurchaseReturnSerialBo(ProductStockSerial serial) {
    this.id = serial.getId();
    this.serialNumber = serial.getSerialNumber();
    this.batchId = serial.getBatchId();
    this.productionDate = serial.getProductionDate();
    this.expiryDate = serial.getExpiryDate();
  }
}
