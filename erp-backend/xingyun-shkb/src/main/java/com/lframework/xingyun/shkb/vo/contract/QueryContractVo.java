package com.lframework.xingyun.shkb.vo.contract;

import com.lframework.starter.web.core.components.validation.IsEnum;
import com.lframework.starter.web.core.vo.BaseVo;
import com.lframework.starter.web.core.vo.SortPageVo;
import com.lframework.xingyun.shkb.enums.ContractStatus;
import com.lframework.xingyun.shkb.enums.ContractType;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 * 查询合同的参数
 *
 * @author kison
 */
@Data
public class QueryContractVo extends SortPageVo implements BaseVo, Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 合同编号
     */
    @ApiModelProperty("合同编号")
    private String code;

    /**
     * 合同名称
     */
    @ApiModelProperty("合同名称")
    private String name;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private Boolean available;

    /**
     * 合同类型
     */
    @ApiModelProperty("合同类型")
    @IsEnum(message = "合同类型格式不正确！", enumClass = ContractType.class)
    private Integer contractType;
    
    /**
     * 合同状态
     */
    @ApiModelProperty("合同状态")
    @IsEnum(message = "合同状态格式不正确！", enumClass = ContractStatus.class)
    private Integer contractStatus;

    /**
     * 任务状态（合同任务）
     */
    @ApiModelProperty("任务状态")
    private String taskStatus;

    /**
     * 客户标识
     */
    @ApiModelProperty("客户标识")
    private String customerId;

    /**
     * 件号ID
     */
    @ApiModelProperty("件号ID")
    private String partNumberId;

    /**
     * 件号
     */
    @ApiModelProperty("件号")
    private String partNumberCode;
    
    /**
     * 机型ID
     */
    @ApiModelProperty("机型ID")
    private String machineTypeId;

    /**
     * 维修类型ID列表，多个用逗号分隔
     */
    @ApiModelProperty("维修类型ID列表，多个用逗号分隔")
    private String repairTypeIds;

    /**
     * 产品序号
     */
    @ApiModelProperty("产品序号")
    private String serialNumber;

    /**
     * 合同时间起始时间
     */
    @ApiModelProperty("合同时间起始时间")
    private LocalDateTime startContractTime;

    /**
     * 合同时间截止时间
     */
    @ApiModelProperty("合同时间截止时间")
    private LocalDateTime endContractTime;

    /**
     * 入库时间起始时间
     */
    @ApiModelProperty("入库时间起始时间")
    private LocalDateTime startStorageTime;

    /**
     * 入库时间截止时间
     */
    @ApiModelProperty("入库时间截止时间")
    private LocalDateTime endStorageTime;

    /**
     * 计划完工时间起始时间
     */
    @ApiModelProperty("计划完工时间起始时间")
    private LocalDateTime startPlannedCompletionTime;

    /**
     * 计划完工时间截止时间
     */
    @ApiModelProperty("计划完工时间截止时间")
    private LocalDateTime endPlannedCompletionTime;

    /**
     * 导出时按指定合同ID列表过滤，多个ID用逗号分隔
     */
    @ApiModelProperty("合同ID列表，多个用逗号分隔")
    private String ids;
}
