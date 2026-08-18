package com.lframework.xingyun.shkb.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.shkb.entity.ShkbTrainingImplementation;
import com.lframework.xingyun.shkb.vo.training.QueryShkbTrainingImplementationVo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ShkbTrainingImplementationMapper
extends BaseMapper<ShkbTrainingImplementation> {
    public List<ShkbTrainingImplementation> queryWithCourse(@Param(value="vo") QueryShkbTrainingImplementationVo var1);

    public ShkbTrainingImplementation findByIdWithCourse(String var1);
}


