package com.lframework.xingyun.shkb.vo.workcard;

import com.lframework.starter.web.core.vo.PageVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 查询工卡 VO
 *
 * @author kison
 */
@Data
public class QueryWorkCardVo extends PageVo {

    /**
     * 工卡号
     */
    @ApiModelProperty("工卡号")
    private String code;

    /**
     * 工卡名称
     */
    @ApiModelProperty("工卡名称")
    private String name;

    /**
     * 机型ID
     */
    @ApiModelProperty("机型ID")
    private String machineTypeId;

    /**
     * 件号
     */
    @ApiModelProperty("件号")
    private String partNumberCode;


    /**
     * 客户ID
     */
    @ApiModelProperty("客户ID")
    private String customerId;
    
    /**
     * 维修类型ID
     */
    @ApiModelProperty("维修类型ID")
    private String repairTypeId;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private Boolean available;

    /**
     * 创建时间起始
     */
    @ApiModelProperty("创建时间起始")
    private LocalDateTime createTimeStart;

    /**
     * 创建时间截止
     */
    @ApiModelProperty("创建时间截止")
    private LocalDateTime createTimeEnd;
}
