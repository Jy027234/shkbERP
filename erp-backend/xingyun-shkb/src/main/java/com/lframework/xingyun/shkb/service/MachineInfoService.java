package com.lframework.xingyun.shkb.service;

import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.entity.MachineInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.xingyun.shkb.vo.machineinfo.QueryMachineInfoVo;

/**
* @author kison
* @description 针对表【shkb_machine_info(自动化设备表)】的数据库操作Service
* @createDate 2025-10-30 09:49:02
*/
public interface MachineInfoService extends BaseMpService<MachineInfo> {

    PageResult<MachineInfo> query(Integer pageIndex, Integer pageSize, QueryMachineInfoVo vo);

    void updateNameAndIp(String id, String machineName, String ipAddress);

    /**
     * 根据 IP 更新拧紧机最近访问时间
     */
    void updateVisitTimeByIp(String ipAddress);

    /**
     * 更新拧紧机最近访问时间（当前仅一台设备场景，不根据IP）
     */
    void updateVisitTimeForTightening();
}
