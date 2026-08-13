package com.lframework.xingyun.shkb.vo.dashboard;

import com.lframework.starter.web.core.vo.BaseVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDate;

/**
 * 查询看板数据的VO
 */
@Data
public class QueryDashboardVo implements BaseVo {
    
    @ApiModelProperty("合同类型")
    private Integer contractType;
    
    @ApiModelProperty("任务状态")
    private Integer taskStatus;
    
    @ApiModelProperty("维修状态")
    private Integer repairStatus;
    
    @ApiModelProperty("件号ID")
    private String partNumberId;
    
    @ApiModelProperty("机型ID")
    private String machineTypeId;
    
    @ApiModelProperty("开始日期")
    private LocalDate startDate;
    
    @ApiModelProperty("结束日期")
    private LocalDate endDate;
    
    @ApiModelProperty("查询类型，1-维修统计，2-合同任务，3-库存统计，4-工具设备")
    private Integer queryType;
}
