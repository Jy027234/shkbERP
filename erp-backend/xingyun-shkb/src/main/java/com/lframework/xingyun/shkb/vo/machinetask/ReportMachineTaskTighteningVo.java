package com.lframework.xingyun.shkb.vo.machinetask;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class ReportMachineTaskTighteningVo {

    @ApiModelProperty(value = "任务ID")
    private String taskId;

    @ApiModelProperty(value = "合同号")
    private String contractNo;

    @ApiModelProperty(value = "序列号")
    private String serialNo;

    @ApiModelProperty(value = "件号")
    private String partNo;

    @ApiModelProperty(value = "上报数据", required = true)
    @NotNull(message = "上报数据不能为空！")
    private ReportDataVo reportData;

    @Data
    public static class ReportDataVo {
        @ApiModelProperty("表头信息")
        @NotNull(message = "表头不能为空！")
        private Header header;

        @ApiModelProperty("数据行")
        @NotNull(message = "数据行不能为空！")
        private List<Row> data;
    }

    @Data
    public static class Header {
        @JsonProperty("TechnologyName")
        private String technologyName;
        @JsonProperty("AircraftType")
        private String aircraftType;
        @JsonProperty("AircraftWheelPos")
        private String aircraftWheelPos;
        @JsonProperty("AircraftWheelType")
        private String aircraftWheelType;
        @JsonProperty("ScrewsNo")
        private String screwsNo;
        @JsonProperty("PreTorque")
        private String preTorque;
        @JsonProperty("FinalTorque")
        private String finalTorque;
        @JsonProperty("Angel")
        private String angel;
        @JsonProperty("MaxTorque")
        private String maxTorque;
        @JsonProperty("MinTorque")
        private String minTorque;
        @JsonProperty("Date")
        private String date;
        @JsonProperty("WheelProductNo")
        private String wheelProductNo;
        @JsonProperty("Operator")
        private String operator;
        @JsonProperty("ContractNo")
        private String contractNo;
    }

    @Data
    public static class Row {
        @JsonProperty("ID")
        private String id;
        @JsonProperty("ScrewNo")
        private String screwNo;
        @JsonProperty("PreTorque")
        private String preTorque;
        @JsonProperty("PreAngel")
        private String preAngel;
        @JsonProperty("PreStatus")
        private String preStatus;
        @JsonProperty("PreTime")
        private String preTime;
        @JsonProperty("FinalTorque")
        private String finalTorque;
        @JsonProperty("FinalAngel")
        private String finalAngel;
        @JsonProperty("FinalStatus")
        private String finalStatus;
        @JsonProperty("FinalTime")
        private String finalTime;
    }
}
