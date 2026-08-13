package com.lframework.xingyun.shkb.vo.machineinfo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.lframework.starter.web.core.vo.PageVo;

@Data
@EqualsAndHashCode(callSuper = true)
public class QueryMachineInfoVo extends PageVo {

    @ApiModelProperty("设备ID")
    private String machineId;

    @ApiModelProperty("设备名称")
    private String machineName;

    @ApiModelProperty("设备类型 1-拧紧机 2-磁粉机")
    private Integer machineType;
}
