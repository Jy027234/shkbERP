package com.lframework.xingyun.shkb.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.Assert;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.PageHelperUtil;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.xingyun.shkb.entity.DeviceRecord;
import com.lframework.xingyun.shkb.mappers.DeviceRecordMapper;
import com.lframework.xingyun.shkb.service.DeviceRecordService;
import com.lframework.xingyun.shkb.vo.device.CreateDeviceRecordVo;
import com.lframework.xingyun.shkb.vo.device.QueryDeviceRecordVo;
import com.lframework.xingyun.shkb.vo.device.UpdateDeviceRecordVo;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
* @author kison
* @description 针对表【shkb_device_record(设备维修记录)】的数据库操作Service实现
* @createDate 2025-06-06 10:07:22
*/
@Service
public class DeviceRecordServiceImpl extends BaseMpServiceImpl<DeviceRecordMapper, DeviceRecord>
    implements DeviceRecordService {

    @Override
    public PageResult<DeviceRecord> query(Integer pageIndex, Integer pageSize, QueryDeviceRecordVo vo) {
        Assert.greaterThanZero(pageIndex);
        Assert.greaterThanZero(pageSize);

        PageHelperUtil.startPage(pageIndex, pageSize);
        List<DeviceRecord> datas = this.query(vo);

        return PageResultUtil.convert(new PageInfo<>(datas));
    }

    private List<DeviceRecord> query(QueryDeviceRecordVo vo) {
        LambdaQueryWrapper<DeviceRecord> wrapper = Wrappers.lambdaQuery(DeviceRecord.class);
        
        // 设备ID
        if (StringUtil.isNotBlank(vo.getDeviceId())) {
            wrapper.eq(DeviceRecord::getDeviceId, vo.getDeviceId());
        }
        
        // 维保人
        if (StringUtil.isNotBlank(vo.getMaintenancenUser())) {
            wrapper.like(DeviceRecord::getMaintenancenUser, vo.getMaintenancenUser());
        }
        
        // 维保时间范围
        if (vo.getMaintenanceTimeStart() != null) {
            wrapper.ge(DeviceRecord::getMaintenanceTime, vo.getMaintenanceTimeStart());
        }
        
        if (vo.getMaintenanceTimeEnd() != null) {
            wrapper.le(DeviceRecord::getMaintenanceTime, vo.getMaintenanceTimeEnd());
        }
        
        // 创建时间范围
        if (vo.getCreateTimeStart() != null) {
            wrapper.ge(DeviceRecord::getCreateTime, vo.getCreateTimeStart());
        }
        
        if (vo.getCreateTimeEnd() != null) {
            wrapper.le(DeviceRecord::getCreateTime, vo.getCreateTimeEnd());
        }
        
        return getBaseMapper().selectList(wrapper);
    }

    @Override
    public DeviceRecord findById(String id) {
        return getBaseMapper().selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String create(CreateDeviceRecordVo vo) {
        DeviceRecord record = new DeviceRecord();
        record.setId(IdUtil.getId());
        record.setDeviceId(vo.getDeviceId());
        record.setMaintenancenUser(vo.getMaintenancenUser());
        record.setMaintenanceTime(vo.getMaintenanceTime());
        record.setDescription(vo.getDescription());
        
        getBaseMapper().insert(record);
        
        return record.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UpdateDeviceRecordVo vo) {
        DeviceRecord record = getBaseMapper().selectById(vo.getId());
        if (record == null) {
            throw new DefaultClientException("设备维修记录不存在");
        }
        
        record.setDeviceId(vo.getDeviceId());
        record.setMaintenancenUser(vo.getMaintenancenUser());
        record.setMaintenanceTime(vo.getMaintenanceTime());
        record.setDescription(vo.getDescription());
        record.setUpdateTime(LocalDateTime.now());
        
        getBaseMapper().updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(String id) {
        getBaseMapper().deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(List<String> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return;
        }
        
        getBaseMapper().deleteBatchIds(ids);
    }
}
