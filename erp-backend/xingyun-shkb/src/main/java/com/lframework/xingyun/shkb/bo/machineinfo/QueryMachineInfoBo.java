package com.lframework.xingyun.shkb.bo.machineinfo;

import com.lframework.xingyun.shkb.entity.MachineInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QueryMachineInfoBo {

    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("设备ID")
    private String machineId;

    @ApiModelProperty("设备类型 1-拧紧机 2-磁粉机")
    private Integer machineType;

    @ApiModelProperty("设备名称")
    private String machineName;

    @ApiModelProperty("最近访问时间")
    private LocalDateTime visitTime;

    @ApiModelProperty("IP地址")
    private String ipAddress;

    public QueryMachineInfoBo(MachineInfo d) {
        this.id = d.getId();
        this.machineId = d.getMachineId();
        this.machineType = d.getMachineType();
        this.machineName = d.getMachineName();
        this.visitTime = d.getVisitTime();
        this.ipAddress = d.getIpAddress();
    }
}
