package com.lframework.xingyun.shkb.vo.tool;

import com.lframework.starter.web.core.vo.PageVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class QueryToolRecordVo extends PageVo {

    /**
     * 工具ID
     */
    @ApiModelProperty("工具ID")
    private String toolId;

    /**
     * 维保人
     */
    @ApiModelProperty("维保人")
    private String maintenancenUser;

    /**
     * 计量开始时间
     */
    @ApiModelProperty("计量开始时间")
    private LocalDate startTime;

    /**
     * 计量结束时间
     */
    @ApiModelProperty("计量结束时间")
    private LocalDate endTime;
}
