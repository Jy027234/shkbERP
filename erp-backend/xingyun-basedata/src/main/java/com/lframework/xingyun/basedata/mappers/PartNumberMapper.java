package com.lframework.xingyun.basedata.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.basedata.bo.partNumber.GetPartNumberBo;
import com.lframework.xingyun.basedata.bo.partNumber.PartNumberSelectorBo;
import com.lframework.xingyun.basedata.bo.partNumber.QueryPartNumberBo;
import com.lframework.xingyun.basedata.entity.PartNumber;
import com.lframework.xingyun.basedata.vo.partNumber.PartNumberSelectorVo;
import com.lframework.xingyun.basedata.vo.partNumber.QueryPartNumberVo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* @author kison
* @description 针对表【base_data_part_number(件号)】的数据库操作Mapper
* @createDate 2025-04-23 10:10:53
* @Entity com.lframework.xingyun.basedata.entity.PartNumber
*/
@Mapper
public interface PartNumberMapper extends BaseMapper<PartNumber> {
  /**
   * 查询列表
   *
   * @param vo
   * @return
   */
  List<QueryPartNumberBo> query(@Param("vo") QueryPartNumberVo vo);

  /**
   * 选择器
   *
   * @param vo
   * @return
   */
  List<PartNumberSelectorBo> selector(@Param("vo") PartNumberSelectorVo vo);

  GetPartNumberBo getPartNumberDetail(@Param("id") String id);
}
