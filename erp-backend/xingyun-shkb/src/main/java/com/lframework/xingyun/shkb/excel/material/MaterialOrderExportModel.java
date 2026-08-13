package com.lframework.xingyun.shkb.excel.material;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.starter.web.core.components.excel.ExcelModel;
import com.lframework.xingyun.shkb.bo.material.QueryMaterialOrderBo;
import com.lframework.xingyun.shkb.entity.MaterialOrder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 发料单导出模型
 *
 * @author kison
 */
@Data
public class MaterialOrderExportModel extends BaseBo<MaterialOrder> implements ExcelModel {

    /**
     * 单号
     */
    @ExcelProperty("单号")
    private String code;

    /**
     * 仓库名称
     */
    @ExcelProperty("仓库")
    private String scName;

    /**
     * 发料数量
     */
    @ExcelProperty("发料数量")
    private Integer totalNum;

    /**
     * 发料金额
     */
    @ExcelProperty("发料金额")
    private BigDecimal totalAmount;

    /**
     * 备注
     */
    @ExcelProperty("备注")
    private String description;

    /**
     * 创建人
     */
    @ExcelProperty("创建人")
    private String createBy;

    /**
     * 创建时间
     */
    @ExcelProperty("创建时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    /**
     * 是否已全部出库完毕
     */
    @ExcelProperty("是否已出库完毕")
    private String isOutFinish;

    public MaterialOrderExportModel() {
    }

    public MaterialOrderExportModel(MaterialOrder dto) {
        super(dto);
    }

    public MaterialOrderExportModel(QueryMaterialOrderBo bo) {
        this.code = bo.getCode();
        this.scName = bo.getScName();
        this.totalNum = bo.getTotalNum();
        this.totalAmount = bo.getTotalAmount();
        this.description = bo.getDescription();
        this.createBy = bo.getCreateBy();
        this.createTime = bo.getCreateTime();
        this.isOutFinish = bo.getIsOutFinish() != null && bo.getIsOutFinish() ? "是" : "否";
    }
}
