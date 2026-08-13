package com.lframework.xingyun.basedata.bo.partNumber;

import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.xingyun.basedata.entity.PartNumber;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import org.springframework.stereotype.Component;

@Data
public class GetPartNumberBo extends BaseBo<PartNumber> implements BaseDto {

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

  public GetPartNumberBo() {

  }

  public GetPartNumberBo(PartNumber dto) {

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
