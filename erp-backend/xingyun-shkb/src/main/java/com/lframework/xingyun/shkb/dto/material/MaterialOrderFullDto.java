package com.lframework.xingyun.shkb.dto.material;

import com.lframework.starter.web.core.dto.BaseDto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 发料单完整信息Dto
 *
 * @author kison
 */
@Data
public class MaterialOrderFullDto implements BaseDto {

    /**
     * ID
     */
    private String id;

    /**
     * 单号
     */
    private String code;

    /**
     * 仓库ID
     */
    private String scId;

    /**
     * 仓库名称
     */
    private String scName;

    /**
     * 发料数量
     */
    private Integer totalNum;

    /**
     * 发料金额
     */
    private BigDecimal totalAmount;

    /**
     * 备注
     */
    private String description;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建人ID
     */
    private String createById;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 发料申请单ID
     */
    private String materialApplyId;

    /**
     * 发料申请单号
     */
    private String materialApplyCode;

    /**
     * 发料单明细
     */
    private List<MaterialOrderDetailDto> details;

    /**
     * 是否已全部出库完毕
     */
    private Boolean isOutFinish;
}
