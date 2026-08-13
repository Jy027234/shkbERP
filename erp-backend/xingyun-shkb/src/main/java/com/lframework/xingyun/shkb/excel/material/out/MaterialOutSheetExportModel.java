package com.lframework.xingyun.shkb.excel.material.out;

import com.lframework.starter.web.core.components.excel.ExcelModel;
import com.lframework.xingyun.shkb.entity.MaterialOutSheet;
import com.lframework.xingyun.shkb.bo.material.out.QueryMaterialOutSheetBo;
import com.lframework.xingyun.shkb.enums.MaterialOutSheetStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 发料出库单Excel导出模型
 */
@Data
public class MaterialOutSheetExportModel implements ExcelModel {

    /**
     * 单号
     */
    private String code;

    /**
     * 仓库名称
     */
    private String scName;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 发料员姓名
     */
    private String materialUserName;

    /**
     * 发料单号
     */
    private String materialOrderCode;

    /**
     * 商品数量
     */
    private Integer totalNum;

    /**
     * 发料金额
     */
    private BigDecimal totalAmount;

    /**
     * 状态
     */
    private String statusText;

    /**
     * 备注
     */
    private String description;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 审核人
     */
    private String approveBy;

    /**
     * 审核时间
     */
    private LocalDateTime approveTime;

    public MaterialOutSheetExportModel() {
    }

    public MaterialOutSheetExportModel(MaterialOutSheet entity) {
        this.code = entity.getCode();
        this.totalNum = entity.getTotalNum();
        this.totalAmount = entity.getTotalAmount();
        this.description = entity.getDescription();
        this.createBy = entity.getCreateBy();
        this.createTime = entity.getCreateTime();
        this.approveBy = entity.getApproveBy();
        this.approveTime = entity.getApproveTime();
        
        // 状态文本转换
        if (entity.getStatus() != null) {
            switch (entity.getStatus()) {
                case 0:
                    this.statusText = "待审核";
                    break;
                case 1:
                    this.statusText = "审核通过";
                    break;
                case 2:
                    this.statusText = "审核拒绝";
                    break;
                default:
                    this.statusText = "未知";
                    break;
            }
        }
    }

    public MaterialOutSheetExportModel(QueryMaterialOutSheetBo bo) {
        this.code = bo.getCode();
        this.scName = bo.getScName();
        this.supplierName = bo.getSupplierName();
        this.materialUserName = bo.getMaterialUserName();
        this.materialOrderCode = bo.getMaterialOrderCode();
        this.totalNum = bo.getTotalNum();
        this.totalAmount = bo.getTotalAmount();
        this.description = bo.getDescription();
        this.createBy = bo.getCreateBy();
        this.createTime = bo.getCreateTime();
        this.approveBy = bo.getApproveBy();
        this.approveTime = bo.getApproveTime();
        if (bo.getStatus() != null) {
            for (MaterialOutSheetStatus status : MaterialOutSheetStatus.values()) {
                if (status.getCode().equals(bo.getStatus())) {
                    this.statusText = status.getDesc();
                    break;
                }
            }
        }
    }
}
