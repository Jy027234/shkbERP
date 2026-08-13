package com.lframework.xingyun.shkb.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.shkb.entity.ShkbEmployee;
import org.apache.ibatis.annotations.Mapper;

/**
* @author kison
* @description 针对表【shkb_employee(员工基本信息表)】的数据库操作Mapper
* @createDate 2026-03-16 10:47:57
* @Entity com.lframework.xingyun.shkb.entity.ShkbEmployee
*/
@Mapper
public interface ShkbEmployeeMapper extends BaseMapper<ShkbEmployee> {

}




