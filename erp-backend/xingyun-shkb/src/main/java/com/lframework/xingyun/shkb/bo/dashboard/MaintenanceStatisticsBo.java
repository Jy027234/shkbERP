package com.lframework.xingyun.shkb.bo.dashboard;

import com.lframework.starter.web.core.bo.BaseBo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 维修统计BO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MaintenanceStatisticsBo extends BaseBo {

    /**
     * 维修类型编码
     */
    @ApiModelProperty("维修类型编码")
    private Integer type;

    /**
     * 维修类型名称
     */
    @ApiModelProperty("维修类型名称")
    private String typeName;

    /**
     * 数量
     */
    @ApiModelProperty("数量")
    private Integer count;
}
