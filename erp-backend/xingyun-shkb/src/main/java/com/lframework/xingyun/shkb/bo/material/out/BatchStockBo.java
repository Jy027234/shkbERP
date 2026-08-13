package com.lframework.xingyun.shkb.bo.material.out;

import java.time.LocalDate;
import lombok.Data;

/**
 * 批次库存查询结果
 */
@Data
public class BatchStockBo {

  /** 批次库存ID */
  private String id;

  /** 仓库ID */
  private String scId;

  /** 仓库名称 */
  private String scName;

  /** 商品ID */
  private String productId;

  /** 商品名称 */
  private String productName;

  /** 件号（Part Number）编码 */
  private String partNumberCode;

  /** 机型 */
  private String machineType;

  /** 批次号 */
  private String batchNumber;

  /** 批次库存数量 */
  private Integer quantity;

  /** 生产日期 */
  private LocalDate productionDate;

  /** 失效日期 */
  private LocalDate expiryDate;
}
