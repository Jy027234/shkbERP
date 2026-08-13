package com.lframework.xingyun.basedata.bo.partNumber;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lframework.starter.common.constants.StringPool;
import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.xingyun.basedata.entity.PartNumber;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class QueryPartNumberBo extends BaseBo<PartNumber> implements BaseDto {

  /**
   * ID
   */
  @ApiModelProperty("ID")
  private String id;

  /**
   * 编号
   */
  @ApiModelProperty("编号")
  private String code;

  /**
   * 名称
   */
  @ApiModelProperty("名称")
  private String name;

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
   * 创建人ID
   */
  @ApiModelProperty("创建人ID")
  private String createBy;

  /**
   * 创建时间
   */
  @ApiModelProperty("创建时间")
  @JsonFormat(pattern = StringPool.DATE_TIME_PATTERN)
  private LocalDateTime createTime;

  /**
   * 修改人ID
   */
  @ApiModelProperty("修改人ID")
  private String updateBy;

  /**
   * 修改时间
   */
  @ApiModelProperty("修改时间")
  @JsonFormat(pattern = StringPool.DATE_TIME_PATTERN)
  private LocalDateTime updateTime;

  public QueryPartNumberBo() {

  }

  public QueryPartNumberBo(PartNumber dto) {

    super(dto);
  }

  @Override
  public BaseBo<PartNumber> convert(PartNumber dto) {

    return super.convert(dto);
  }

  @Override
  protected void afterInit(PartNumber dto) {
    // 机型名称将通过SQL关联查询获取，不需要在这里处理
  }
}
