package com.lframework.xingyun.basedata.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.basedata.entity.RepairType;
import com.lframework.xingyun.basedata.vo.repairType.QueryRepairTypeVo;
import com.lframework.xingyun.basedata.vo.repairType.RepairTypeSelectorVo;
import com.lframework.xingyun.core.annotations.sort.Sort;
import com.lframework.xingyun.core.annotations.sort.Sorts;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* @author kison
* @description 针对表【base_data_repair_type(维修类型)】的数据库操作Mapper
* @createDate 2025-04-23 09:08:37
* @Entity com.lframework.xingyun.basedata.entity.RepairType
*/
@Mapper
public interface RepairTypeMapper extends BaseMapper<RepairType> {

  /**
   * 查询列表
   *
   * @param vo
   * @return
   */
  @Sorts({
      @Sort(value = "code", alias = "tb", autoParse = true),
      @Sort(value = "name", alias = "tb", autoParse = true),
  })
  List<RepairType> query(@Param("vo") QueryRepairTypeVo vo);

  /**
   * 选择器
   *
   * @param vo
   * @return
   */
  List<RepairType> selector(@Param("vo") RepairTypeSelectorVo vo);
}
