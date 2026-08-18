package com.lframework.xingyun.shkb.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.shkb.entity.ShkbPersonAuthorization;
import com.lframework.xingyun.shkb.vo.authorization.QueryShkbPersonAuthorizationVo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ShkbPersonAuthorizationMapper
extends BaseMapper<ShkbPersonAuthorization> {
    @Select(value={"<script>SELECT DISTINCT spa.* FROM shkb_person_authorization spa LEFT JOIN shkb_employee se ON spa.employee_id = se.id LEFT JOIN shkb_person_authorization_project spaspa ON spa.id = spaspa.authorization_id LEFT JOIN shkb_authorization_project sap ON spaspa.project_id = sap.id <where> spa.deleted = 0 <if test='vo.employeeName != null and vo.employeeName != \"\"'>AND se.name LIKE CONCAT('%', #{vo.employeeName}, '%') </if><if test='vo.projectName != null and vo.projectName != \"\"'>AND sap.project_name LIKE CONCAT('%', #{vo.projectName}, '%') </if><if test='vo.status != null'>AND spa.status = #{vo.status} </if></where>ORDER BY spa.create_time DESC</script>"})
    public List<ShkbPersonAuthorization> queryByCondition(@Param(value="vo") QueryShkbPersonAuthorizationVo var1);
}


