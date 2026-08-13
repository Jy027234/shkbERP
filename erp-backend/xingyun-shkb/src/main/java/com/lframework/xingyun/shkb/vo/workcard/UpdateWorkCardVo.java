package com.lframework.xingyun.shkb.vo.workcard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 修改工卡 VO
 *
 * @author kison
 */
@Data
public class UpdateWorkCardVo {

    /**
     * ID
     */
    @ApiModelProperty(value = "ID", required = true)
    @NotBlank(message = "ID不能为空！")
    private String id;

    /**
     * 工卡号
     */
    @ApiModelProperty(value = "工卡号", required = true)
    @NotBlank(message = "工卡号不能为空！")
    private String code;

    /**
     * 工卡名称
     */
    @ApiModelProperty(value = "工卡名称", required = true)
    @NotBlank(message = "工卡名称不能为空！")
    private String name;

    /**
     * 件号ID
     */
    @ApiModelProperty(value = "件号ID", required = true)
    @NotBlank(message = "件号ID不能为空！")
    private String partNumberId;

    /**
     * 维修类型ID
     */
    @ApiModelProperty("维修类型ID")
    private String repairTypeId;

    /**
     * 客户ID
     */
    @ApiModelProperty("客户ID")
    private String customerId;

    /**
     * 批准日期
     */
    @ApiModelProperty("批准日期")
    private LocalDate approvalDate;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态", required = true)
    @NotNull(message = "状态不能为空！")
    private Boolean available;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String description;

    /**
     * 版本号
     */
    @ApiModelProperty("版本号")
    private String version;
}
