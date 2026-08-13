package com.lframework.xingyun.shkb.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.shkb.entity.MachineTaskTightening;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.lframework.xingyun.shkb.vo.machinetask.QueryMachineTaskTighteningVo;
import java.util.List;

/**
* @author kison
* @description 针对表【shkb_machine_task_tightening(拧紧机任务表)】的数据库操作Mapper
* @createDate 2025-10-22 10:16:56
* @Entity com.lframework.xingyun.shkb.entity.MachineTaskTightening
*/
@Mapper
public interface MachineTaskTighteningMapper extends BaseMapper<MachineTaskTightening> {
    List<MachineTaskTightening> query(@Param("vo") QueryMachineTaskTighteningVo vo);
    List<MachineTaskTightening> queryPending();
}




