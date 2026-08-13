package com.lframework.xingyun.shkb.bo.material.out;

import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.utils.ApplicationUtil;
import com.lframework.xingyun.shkb.dto.material.out.MaterialOutSheetFullDto;
import com.lframework.xingyun.template.inner.service.system.SysUserService;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Data;

/**
 * 发料出库单详情Bo
 */
@Data
public class GetMaterialOutSheetBo extends BaseBo<BaseDto> {

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private String id;

    /**
     * 单号
     */
    @ApiModelProperty("单号")
    private String code;

    /**
     * 仓库ID
     */
    @ApiModelProperty("仓库ID")
    private String scId;

    /**
     * 仓库名称
     */
    @ApiModelProperty("仓库名称")
    private String scName;

    /**
     * 供应商ID
     */
    @ApiModelProperty("供应商ID")
    private String supplierId;

    /**
     * 供应商名称
     */
    @ApiModelProperty("供应商名称")
    private String supplierName;

    /**
     * 发料员ID
     */
    @ApiModelProperty("发料员ID")
    private String materialUserId;

    /**
     * 发料员姓名
     */
    @ApiModelProperty("发料员姓名")
    private String materialUserName;

    /**
     * 发料日期
     */
    @ApiModelProperty("发料日期")
    private Date materialDate;

    /**
     * 发料单ID
     */
    @ApiModelProperty("发料单ID")
    private String materialOrderId;

    /**
     * 发料单号
     */
    @ApiModelProperty("发料单号")
    private String materialOrderCode;

    /**
     * 商品数量
     */
    @ApiModelProperty("商品数量")
    private Integer totalNum;

    /**
     * 发料金额
     */
    @ApiModelProperty("发料金额")
    private BigDecimal totalAmount;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private Integer status;

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
    private LocalDateTime createTime;

    /**
     * 修改人
     */
    @ApiModelProperty("修改人")
    private String updateBy;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    private LocalDateTime updateTime;

    /**
     * 审核人
     */
    @ApiModelProperty("审核人")
    private String approveBy;

    /**
     * 审核时间
     */
    @ApiModelProperty("审核时间")
    private LocalDateTime approveTime;

    /**
     * 拒绝原因
     */
    @ApiModelProperty("拒绝原因")
    private String refuseReason;

    /**
     * 明细
     */
    @ApiModelProperty("明细")
    private List<GetMaterialOutSheetDetailBo> details;

    public GetMaterialOutSheetBo() {
    }

    public GetMaterialOutSheetBo(MaterialOutSheetFullDto dto) {
        if (dto == null) {
            return;
        }
        this.id = dto.getId();
        this.code = dto.getCode();
        this.scId = dto.getScId();
        this.scName = dto.getScName();
        this.supplierId = dto.getSupplierId();
        this.supplierName = dto.getSupplierName();
        this.materialUserId = dto.getMaterialUserId();
        this.materialUserName = dto.getMaterialUserName();
        this.materialDate = dto.getMaterialDate();
        this.materialOrderId = dto.getMaterialOrderId();
        this.materialOrderCode = dto.getMaterialOrderCode();
        this.totalNum = dto.getTotalNum();
        this.totalAmount = dto.getTotalAmount();
        this.status = dto.getStatus();
        this.description = dto.getDescription();
        this.createBy = dto.getCreateBy();
        this.createTime = dto.getCreateTime();
        this.updateBy = dto.getUpdateBy();
        this.updateTime = dto.getUpdateTime();
        this.approveBy = dto.getApproveBy();
        this.approveTime = dto.getApproveTime();
        this.refuseReason = dto.getRefuseReason();

        // 将审核人ID转换为姓名，返回给前端显示
        if (!StringUtil.isBlank(this.approveBy)) {
            SysUserService userService = ApplicationUtil.getBean(SysUserService.class);
            this.approveBy = userService.findById(this.approveBy).getName();
        }

        if (dto.getDetails() != null) {
            this.details = dto.getDetails().stream()
                    .filter(Objects::nonNull)
                    .map(GetMaterialOutSheetDetailBo::new)
                    .collect(Collectors.toList());
        }
    }
}
