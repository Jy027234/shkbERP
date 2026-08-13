package com.lframework.xingyun.shkb.bo.contract.task;

import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.starter.web.core.utils.ApplicationUtil;
import com.lframework.xingyun.basedata.entity.MachineType;
import com.lframework.xingyun.basedata.entity.PartNumber;
import com.lframework.xingyun.basedata.entity.Product;
import com.lframework.xingyun.basedata.entity.RepairType;
import com.lframework.xingyun.basedata.service.machineType.MachineTypeService;
import com.lframework.xingyun.basedata.service.partNumber.PartNumberService;
import com.lframework.xingyun.basedata.service.product.ProductService;
import com.lframework.xingyun.basedata.service.repairType.RepairTypeService;
import com.lframework.xingyun.shkb.entity.ContractTaskWorkCard;
import com.lframework.xingyun.shkb.entity.WorkCard;
import com.lframework.xingyun.shkb.service.workcard.WorkCardService;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 合同任务工卡Bo
 *
 * @author kison
 */
@Data
public class ContractTaskWorkCardBo extends BaseBo<ContractTaskWorkCard> {

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private String id;

    /**
     * 合同任务ID
     */
    @ApiModelProperty("合同任务ID")
    private String taskId;

    /**
     * 工卡ID
     */
    @ApiModelProperty("工卡ID")
    private String workCardId;

    /**
     * 工卡号
     */
    @ApiModelProperty("工卡号")
    private String workCardCode;

    /**
     * 工卡名称
     */
    @ApiModelProperty("工卡名称")
    private String workCardName;

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
     * 维修类型ID
     */
    @ApiModelProperty("维修类型ID")
    private String repairTypeId;

    /**
     * 维修类型名称
     */
    @ApiModelProperty("维修类型名称")
    private String repairTypeName;

    public ContractTaskWorkCardBo() {
    }

    public ContractTaskWorkCardBo(ContractTaskWorkCard dto) {
        super(dto);
    }

    @Override
    protected void afterInit(ContractTaskWorkCard dto) {
        // 查询工卡信息
        WorkCardService workCardService = ApplicationUtil.getBean(WorkCardService.class);
        WorkCard workCard = workCardService.getById(dto.getWorkCardId());
        if (workCard != null) {
            this.workCardCode = workCard.getCode();
            this.workCardName = workCard.getName();
            this.partNumberId = workCard.getPartNumberId();
            // 机型ID在WorkCard中不存在，需要从其他地方获取
            this.repairTypeId = workCard.getRepairTypeId();

            // 查询件号对应的机型信息
            if (this.partNumberId != null) {
                ProductService partNumberService = ApplicationUtil.getBean(ProductService.class);
                Product partNumber = partNumberService.getById(this.partNumberId);
                if (partNumber != null && partNumber.getMachineTypeId() != null) {
                    this.machineTypeId = partNumber.getMachineTypeId();
                    
                    // 查询机型信息
                    MachineTypeService machineTypeService = ApplicationUtil.getBean(MachineTypeService.class);
                    MachineType machineType = machineTypeService.getById(this.machineTypeId);
                    if (machineType != null) {
                        this.machineTypeName = machineType.getName();
                    }
                    this.partNumberName = partNumber.getCode();
                }
            }

            // 查询维修类型信息
            if (this.repairTypeId != null) {
                RepairTypeService repairTypeService = ApplicationUtil.getBean(RepairTypeService.class);
                RepairType repairType = repairTypeService.getById(this.repairTypeId);
                if (repairType != null) {
                    this.repairTypeName = repairType.getName();
                }
            }
        }
    }
}
