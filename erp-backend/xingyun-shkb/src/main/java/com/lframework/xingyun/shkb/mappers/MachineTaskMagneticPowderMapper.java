package com.lframework.xingyun.shkb.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.shkb.entity.MachineTaskMagneticPowder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* @author kison
* @description 针对表【shkb_machine_task_magnetic_powder(磁粉机任务表)】的数据库操作Mapper
* @createDate 2025-11-04 08:38:31
* @Entity com.lframework.xingyun.shkb.entity.MachineTaskMagneticPowder
*/
@Mapper
public interface MachineTaskMagneticPowderMapper extends BaseMapper<MachineTaskMagneticPowder> {

    MachineTaskMagneticPowder selectByTaskIdForUpdate(@Param("taskId") String taskId);
}



