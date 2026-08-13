package com.lframework.xingyun.shkb.bo.workcard;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.starter.web.core.utils.ApplicationUtil;
import com.lframework.xingyun.basedata.entity.Customer;
import com.lframework.xingyun.basedata.entity.MachineType;
import com.lframework.xingyun.basedata.entity.PartNumber;
import com.lframework.xingyun.basedata.entity.RepairType;
import com.lframework.xingyun.basedata.service.customer.CustomerService;
import com.lframework.xingyun.basedata.service.machineType.MachineTypeService;
import com.lframework.xingyun.basedata.service.partNumber.PartNumberService;
import com.lframework.xingyun.basedata.service.repairType.RepairTypeService;
import com.lframework.xingyun.shkb.entity.WorkCard;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工卡列表 Bo
 *
 * @author kison
 */
@Data
public class QueryWorkCardBo extends BaseBo<WorkCard> {

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private String id;

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
     * 机型名称
     */
    @ApiModelProperty("机型名称")
    private String machineTypeName;

    /**
     * 件号ID
     */
    @ApiModelProperty("件号ID")
    private String partNumberId;

    /**
     * 件号名称
     */
    @ApiModelProperty("件号名称")
    private String partNumberName;

    /**
     * 件号
     */
    @ApiModelProperty("件号")
    private String partNumber;

    /**
     * 客户ID
     */
    @ApiModelProperty("客户ID")
    private String customerId;

    /**
     * 客户名称
     */
    @ApiModelProperty("客户名称")
    private String customerName;
    
    /**
     * 维修类型ID
     */
    @ApiModelProperty("维修类型ID")
    private String repairTypeId;
    
    /**
     * 维修类型名称
     */
    @ApiModelProperty("维修类型名称")
    private String repairTypeName;

    /**
     * 批准日期
     */
    @ApiModelProperty("批准日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvalDate;

    /**
     * 版本号
     */
    @ApiModelProperty("版本号")
    private String version;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private Boolean available;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String description;

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    private String createBy;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    public QueryWorkCardBo() {
    }

    public QueryWorkCardBo(WorkCard dto) {
        super(dto);
    }

    @Override
    protected void afterInit(WorkCard dto) {
        // 不需要在这里查询数据库
        // 因为在 WorkCardMapper.xml 中已经通过关联查询获取了这些字段
        // 如果没有这些字段，说明是直接使用 WorkCard 实体创建的 BO，需要手动设置
        if (this.partNumberName == null && dto.getPartNumberId() != null) {
            PartNumberService partNumberService = ApplicationUtil.getBean(PartNumberService.class);
            PartNumber partNumber = partNumberService.findById(dto.getPartNumberId());
            if (partNumber != null) {
                this.partNumberName = partNumber.getName();
                this.partNumber = partNumber.getCode();
            }
        }

        if (this.machineTypeName == null && this.machineTypeId != null) {
            MachineTypeService machineTypeService = ApplicationUtil.getBean(MachineTypeService.class);
            MachineType machineType = machineTypeService.findById(this.machineTypeId);
            if (machineType != null) {
                this.machineTypeName = machineType.getName();
            }
        }

        if (this.customerName == null && dto.getCustomerId() != null) {
            CustomerService customerService = ApplicationUtil.getBean(CustomerService.class);
            Customer customer = customerService.findById(dto.getCustomerId());
            if (customer != null) {
                this.customerName = customer.getName();
            }
        }
        
        // 设置维修类型名称
        if (this.repairTypeName == null && dto.getRepairTypeId() != null) {
            RepairTypeService repairTypeService = ApplicationUtil.getBean(RepairTypeService.class);
            RepairType repairType = repairTypeService.findById(dto.getRepairTypeId());
            if (repairType != null) {
                this.repairTypeName = repairType.getName();
            }
        }
    }
}
