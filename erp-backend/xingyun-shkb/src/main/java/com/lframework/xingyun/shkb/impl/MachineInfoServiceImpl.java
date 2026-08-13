package com.lframework.xingyun.shkb.impl;

import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.xingyun.shkb.entity.MachineInfo;
import com.lframework.xingyun.shkb.service.MachineInfoService;
import com.lframework.xingyun.shkb.mappers.MachineInfoMapper;
import org.springframework.stereotype.Service;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.PageHelperUtil;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.github.pagehelper.PageInfo;
import com.lframework.xingyun.shkb.vo.machineinfo.QueryMachineInfoVo;
import com.lframework.starter.common.utils.StringUtil;
import java.util.List;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import java.time.LocalDateTime;

/**
* @author kison
* @description 针对表【shkb_machine_info(自动化设备表)】的数据库操作Service实现
* @createDate 2025-10-30 09:49:02
*/
@Service
public class MachineInfoServiceImpl extends BaseMpServiceImpl<MachineInfoMapper, MachineInfo>
    implements MachineInfoService{
    @Override
    public PageResult<MachineInfo> query(Integer pageIndex, Integer pageSize, QueryMachineInfoVo vo) {
        PageHelperUtil.startPage(pageIndex, pageSize);
        // 构造条件
        List<MachineInfo> datas = this.lambdaQuery()
                .like(StringUtil.isNotBlank(vo.getMachineId()), MachineInfo::getMachineId, vo.getMachineId())
                .like(StringUtil.isNotBlank(vo.getMachineName()), MachineInfo::getMachineName, vo.getMachineName())
                .eq(vo.getMachineType() != null, MachineInfo::getMachineType, vo.getMachineType())
                .list();
        return PageResultUtil.convert(new PageInfo<>(datas));
    }

    @Override
    public void updateNameAndIp(String id, String machineName, String ipAddress) {
        MachineInfo exist = this.getById(id);
        if (exist == null) {
            throw new DefaultClientException("设备不存在！");
        }
        MachineInfo toUpdate = new MachineInfo();
        toUpdate.setId(id);
        toUpdate.setMachineName(machineName);
        toUpdate.setIpAddress(ipAddress);
        this.updateById(toUpdate);
    }

    @Override
    public void updateVisitTimeByIp(String ipAddress) {
        if (StringUtil.isBlank(ipAddress)) {
            return;
        }
        this.lambdaUpdate()
                .set(MachineInfo::getVisitTime, LocalDateTime.now())
                .eq(MachineInfo::getMachineType, 1)
                .eq(MachineInfo::getIpAddress, ipAddress)
                .update();
    }

    @Override
    public void updateVisitTimeForTightening() {
        this.lambdaUpdate()
                .set(MachineInfo::getVisitTime, LocalDateTime.now())
                .eq(MachineInfo::getMachineType, 1)
                .last("limit 1")
                .update();
    }
}




