package com.lframework.xingyun.shkb.vo.tool;

import com.lframework.starter.web.core.vo.BaseVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class CreateToolRecordVo implements BaseVo {

    /**
     * 工具ID
     */
    @ApiModelProperty(value = "工具ID", required = true)
    @NotBlank(message = "工具ID不能为空！")
    private String toolId;

    /**
     * 维保人
     */
    @ApiModelProperty(value = "维保人", required = true)
    @NotBlank(message = "维保人不能为空！")
    private String maintenancenUser;

    /**
     * 计量时间
     */
    @ApiModelProperty(value = "计量时间", required = true)
    @NotNull(message = "计量时间不能为空！")
    private LocalDate maintenanceTime;

    /**
     * 计量证书编号
     */
    @ApiModelProperty(value = "计量证书编号", required = true)
    @NotBlank(message = "计量证书编号不能为空！")
    private String certificateNumber;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String description;
}
