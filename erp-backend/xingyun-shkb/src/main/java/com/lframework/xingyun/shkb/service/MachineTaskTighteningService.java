package com.lframework.xingyun.shkb.service;

import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.entity.MachineTaskTightening;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.xingyun.shkb.vo.machinetask.QueryMachineTaskTighteningVo;
import java.util.List;
import com.lframework.xingyun.shkb.vo.machinetask.ReportMachineTaskTighteningVo;
import com.lframework.xingyun.shkb.bo.machinetask.GetMachineTaskTighteningBo;

/**
* @author kison
* @description 针对表【shkb_machine_task_tightening(拧紧机任务表)】的数据库操作Service
* @createDate 2025-10-22 10:16:56
*/
public interface MachineTaskTighteningService extends BaseMpService<MachineTaskTightening> {

    PageResult<MachineTaskTightening> query(Integer pageIndex, Integer pageSize, QueryMachineTaskTighteningVo vo);

    List<MachineTaskTightening> queryPending();

    /**
     * 任务上报：根据 taskId 将任务状态置为已完成并写入上报数据与上报时间
     */
    void report(ReportMachineTaskTighteningVo vo);

    /**
     * 根据设备任务ID获取任务详情
     */
    MachineTaskTightening getByTaskId(String taskId);

    /**
     * 根据主键ID获取详情（包含解析后的 reportData）
     */
    GetMachineTaskTighteningBo getDetailById(String id);
}
