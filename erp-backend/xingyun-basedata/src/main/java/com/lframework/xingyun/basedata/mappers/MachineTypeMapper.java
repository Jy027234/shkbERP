package com.lframework.xingyun.basedata.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.basedata.entity.MachineType;
import com.lframework.xingyun.basedata.vo.machineType.MachineTypeSelectorVo;
import com.lframework.xingyun.basedata.vo.machineType.QueryMachineTypeVo;
import com.lframework.xingyun.core.annotations.sort.Sort;
import com.lframework.xingyun.core.annotations.sort.Sorts;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* @author kison
* @description 针对表【base_data_machine_type(机型)】的数据库操作Mapper
* @createDate 2025-04-22 14:54:44
* @Entity com.lframework.xingyun.basedata.entity.MachineType
*/
@Mapper
public interface MachineTypeMapper extends BaseMapper<MachineType> {

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
  List<MachineType> query(@Param("vo") QueryMachineTypeVo vo);

  /**
   * 选择器
   *
   * @param vo
   * @return
   */
  List<MachineType> selector(@Param("vo") MachineTypeSelectorVo vo);
}
