package com.lframework.xingyun.sc.vo.stock.batch;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 修改批次库存信息的参数
 */
@Data
public class UpdateProductStockBatchVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ApiModelProperty(value = "ID", required = true)
    @NotBlank(message = "ID不能为空！")
    private String id;

    /**
     * 生产日期
     */
    @ApiModelProperty(value = "生产日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate productionDate;

    /**
     * 失效日期
     */
    @ApiModelProperty(value = "失效日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    /**
     * 供应商ID
     */
    @ApiModelProperty(value = "供应商ID")
    private String supplierId;

    /**
     * 架位（货架/库位信息）
     */
    @ApiModelProperty(value = "架位")
    private String shelfLocation;
}
