package com.lframework.xingyun.shkb.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.shkb.entity.ShkbPersonAuthorizationProject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ShkbPersonAuthorizationProjectMapper
extends BaseMapper<ShkbPersonAuthorizationProject> {
    @Delete(value={"DELETE FROM shkb_person_authorization_project WHERE authorization_id = #{authorizationId}"})
    public int physicallyDeleteByAuthorizationId(@Param(value="authorizationId") String var1);
}


