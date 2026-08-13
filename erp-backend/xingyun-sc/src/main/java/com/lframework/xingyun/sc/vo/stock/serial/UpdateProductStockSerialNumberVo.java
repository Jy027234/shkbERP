package com.lframework.xingyun.sc.vo.stock.serial;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 修改序列号的参数
 */
@Data
public class UpdateProductStockSerialNumberVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ApiModelProperty(value = "ID", required = true)
    @NotBlank(message = "ID不能为空！")
    private String id;

    /**
     * 序列号
     */
    @ApiModelProperty(value = "序列号", required = true)
    @NotBlank(message = "序列号不能为空！")
    private String serialNumber;
}
