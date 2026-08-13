package com.lframework.xingyun.sc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@TableName(value = "stock_init_import_serials")
@Data
public class StockInitImportSerial implements Serializable {
  @TableId(type = IdType.AUTO)
  private Long id;
  private String importBatchId;
  private String importLineId;
  private String serialNumber;
  private String shelfLocation;
  private LocalDateTime createdAt;
  @TableField(exist = false)
  private static final long serialVersionUID = 1L;
}
