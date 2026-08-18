package com.lframework.xingyun.shkb.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.shkb.bo.training.QueryShkbEmployeeTrainingBo;
import com.lframework.xingyun.shkb.entity.ShkbEmployeeTraining;
import com.lframework.xingyun.shkb.vo.employee.QueryShkbEmployeeTrainingVo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ShkbEmployeeTrainingMapper
extends BaseMapper<ShkbEmployeeTraining> {
    public List<QueryShkbEmployeeTrainingBo> query(@Param(value="vo") QueryShkbEmployeeTrainingVo var1);
}


