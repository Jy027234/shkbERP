package com.lframework.xingyun.shkb.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.Assert;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.starter.web.core.utils.PageHelperUtil;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.xingyun.shkb.entity.ShkbDevice;
import com.lframework.xingyun.shkb.mappers.ShkbDeviceMapper;
import com.lframework.xingyun.shkb.service.ShkbDeviceService;
import com.lframework.xingyun.shkb.vo.device.CreateShkbDeviceVo;
import com.lframework.xingyun.shkb.vo.device.QueryShkbDeviceVo;
import com.lframework.xingyun.shkb.vo.device.UpdateShkbDeviceVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
* @author kison
* @description 针对表【shkb_device(设备管理)】的数据库操作Service实现
* @createDate 2025-06-06 10:07:22
*/
@Service
public class ShkbDeviceServiceImpl extends BaseMpServiceImpl<ShkbDeviceMapper, ShkbDevice>
    implements ShkbDeviceService{

    @Autowired
    private ShkbDeviceMapper deviceMapper;

    @Override
    public PageResult<ShkbDevice> query(Integer pageIndex, Integer pageSize, QueryShkbDeviceVo vo) {
        Assert.greaterThanZero(pageIndex);
        Assert.greaterThanZero(pageSize);

        PageHelperUtil.startPage(pageIndex, pageSize);
        List<ShkbDevice> datas = this.query(vo);

        return PageResultUtil.convert(new PageInfo<>(datas));
    }

    @Override
    public ShkbDevice findById(String id) {
        return deviceMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String create(CreateShkbDeviceVo vo) {
        ShkbDevice data = new ShkbDevice();
        data.setId(IdUtil.getId());
        data.setCode(vo.getCode());
        data.setName(vo.getName());
        data.setManagementArea(vo.getManagementArea());
        data.setMaintenanceProject(vo.getMaintenanceProject());
        data.setMaintenanceInterval(vo.getMaintenanceInterval());
        data.setMaintenanceCard(vo.getMaintenanceCard());
        data.setLastMaintenanceTime(vo.getLastMaintenanceTime());
        // 根据上次维保时间与维保间隔（天）自动计算下一次维保时间
        if (vo.getLastMaintenanceTime() != null && vo.getMaintenanceInterval() != null
                && vo.getMaintenanceInterval() > 0) {
            data.setNextMaintenanceTime(vo.getLastMaintenanceTime().plusDays(vo.getMaintenanceInterval()));
        } else {
            data.setNextMaintenanceTime(null);
        }
        data.setAvailable(vo.getAvailable());
        data.setDescription(vo.getDescription());

        deviceMapper.insert(data);

        return data.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UpdateShkbDeviceVo vo) {
        ShkbDevice data = deviceMapper.selectById(vo.getId());
        if (data == null) {
            throw new DefaultClientException("设备不存在！");
        }

        // 计算下一次维保时间：优先使用本次传入值，未传则使用库内原值（单位：天）
        Integer effectiveInterval = vo.getMaintenanceInterval() != null ? vo.getMaintenanceInterval() : data.getMaintenanceInterval();
        LocalDate effectiveLastTime = vo.getLastMaintenanceTime() != null ? vo.getLastMaintenanceTime() : data.getLastMaintenanceTime();
        LocalDate computedNextTime = null;
        if (effectiveLastTime != null && effectiveInterval != null && effectiveInterval > 0) {
            computedNextTime = effectiveLastTime.plusDays(effectiveInterval);
        }

        LambdaUpdateWrapper<ShkbDevice> updateWrapper = Wrappers.lambdaUpdate(ShkbDevice.class)
                .set(ShkbDevice::getCode, vo.getCode())
                .set(ShkbDevice::getName, vo.getName())
                .set(ShkbDevice::getManagementArea, vo.getManagementArea())
                .set(ShkbDevice::getMaintenanceProject, vo.getMaintenanceProject())
                .set(ShkbDevice::getMaintenanceInterval, vo.getMaintenanceInterval())
                .set(ShkbDevice::getMaintenanceCard, vo.getMaintenanceCard())
                .set(ShkbDevice::getLastMaintenanceTime, vo.getLastMaintenanceTime())
                // 不使用前端传入的 nextMaintenanceTime，统一使用计算值
                .set(ShkbDevice::getNextMaintenanceTime, computedNextTime)
                .set(ShkbDevice::getAvailable, vo.getAvailable())
                .set(ShkbDevice::getDescription, vo.getDescription())
                .eq(ShkbDevice::getId, vo.getId());

        deviceMapper.update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(String id) {
        ShkbDevice data = deviceMapper.selectById(id);
        if (data == null) {
            throw new DefaultClientException("设备不存在！");
        }

        deviceMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(List<String> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return;
        }

        deviceMapper.deleteBatchIds(ids);
    }

    private List<ShkbDevice> query(QueryShkbDeviceVo vo) {
        return deviceMapper.query(vo);
    }
}
