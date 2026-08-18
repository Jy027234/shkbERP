package com.lframework.xingyun.shkb.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.shkb.bo.employee.GetShkbEmployeeBo;
import com.lframework.xingyun.shkb.bo.employee.QueryShkbEmployeeBo;
import com.lframework.xingyun.shkb.entity.ShkbEmployee;
import com.lframework.xingyun.shkb.vo.employee.QueryShkbEmployeeVo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShkbEmployeeMapper
extends BaseMapper<ShkbEmployee> {
    public List<QueryShkbEmployeeBo> query(QueryShkbEmployeeVo var1);

    public GetShkbEmployeeBo getDetail(String var1);
}


