package com.lframework.xingyun.shkb.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.Assert;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.starter.web.core.utils.PageHelperUtil;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.xingyun.shkb.entity.ShkbTool;
import com.lframework.xingyun.shkb.entity.ShkbToolRecord;
import com.lframework.xingyun.shkb.mappers.ShkbToolMapper;
import com.lframework.xingyun.shkb.mappers.ShkbToolRecordMapper;
import com.lframework.xingyun.shkb.service.ShkbToolRecordService;
import com.lframework.xingyun.shkb.vo.tool.CreateToolRecordVo;
import com.lframework.xingyun.shkb.vo.tool.QueryToolRecordVo;
import com.lframework.xingyun.shkb.vo.tool.UpdateToolRecordVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
* @author kison
* @description 针对表【shkb_tool_record(计量记录)】的数据库操作Service实现
* @createDate 2025-06-24 15:00:58
*/
@Service
public class ShkbToolRecordServiceImpl extends BaseMpServiceImpl<ShkbToolRecordMapper, ShkbToolRecord>
    implements ShkbToolRecordService {

    @Autowired
    private ShkbToolMapper toolMapper;

    @Override
    public PageResult<ShkbToolRecord> query(Integer pageIndex, Integer pageSize, QueryToolRecordVo vo) {
        Assert.greaterThanZero(pageIndex);
        Assert.greaterThanZero(pageSize);

        PageHelperUtil.startPage(pageIndex, pageSize);
        List<ShkbToolRecord> datas = this.query(vo);

        return PageResultUtil.convert(new PageInfo<>(datas));
    }

    private List<ShkbToolRecord> query(QueryToolRecordVo vo) {
        LambdaQueryWrapper<ShkbToolRecord> wrapper = Wrappers.lambdaQuery(ShkbToolRecord.class);
        
        // 工具ID
        if (StringUtil.isNotBlank(vo.getToolId())) {
            wrapper.eq(ShkbToolRecord::getToolId, vo.getToolId());
        }
        
        // 维保人
        if (StringUtil.isNotBlank(vo.getMaintenancenUser())) {
            wrapper.like(ShkbToolRecord::getMaintenancenUser, vo.getMaintenancenUser());
        }
        
        // 计量时间范围
        if (vo.getStartTime() != null) {
            wrapper.ge(ShkbToolRecord::getMaintenanceTime, vo.getStartTime());
        }
        
        if (vo.getEndTime() != null) {
            wrapper.le(ShkbToolRecord::getMaintenanceTime, vo.getEndTime());
        }
        
        wrapper.orderByDesc(ShkbToolRecord::getCreateTime);
        
        return getBaseMapper().selectList(wrapper);
    }

    @Override
    public ShkbToolRecord findById(String id) {
        return getBaseMapper().selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String create(CreateToolRecordVo vo) {
        ShkbTool tool = toolMapper.selectById(vo.getToolId());
        if (tool == null) {
            throw new DefaultClientException("计量工具不存在");
        }

        ShkbToolRecord record = new ShkbToolRecord();
        record.setId(IdUtil.getId());
        record.setToolId(vo.getToolId());
        record.setMaintenancenUser(vo.getMaintenancenUser());
        record.setMaintenanceTime(vo.getMaintenanceTime());
        record.setCertificateNumber(vo.getCertificateNumber());
        record.setDescription(vo.getDescription());
        
        getBaseMapper().insert(record);
        
        // 计量记录落表后，更新计量工具：上次计量时间与有效期
        // 更新上次计量时间
        tool.setLastMaintenanceTime(vo.getMaintenanceTime());
        
        // 根据计量周期（天）计算下次计量日期与有效期（到期日期，格式yyyy-MM-dd）
        Integer period = tool.getCalibrationPeriod();
        if (period != null && period > 0 && vo.getMaintenanceTime() != null) {
            LocalDate nextDate = vo.getMaintenanceTime().plusDays(period);
            // 下次计量日期
            tool.setNextMaintenanceTime(nextDate);
            // 维保到期日期（日期型）
            tool.setExpirationTime(nextDate);
        }
        toolMapper.updateById(tool);
        
        return record.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UpdateToolRecordVo vo) {
        ShkbToolRecord record = getBaseMapper().selectById(vo.getId());
        if (record == null) {
            throw new DefaultClientException("工具计量记录不存在");
        }
        if (!record.getToolId().equals(vo.getToolId())) {
            throw new DefaultClientException("工具计量记录不允许变更所属工具");
        }
        if (toolMapper.selectById(vo.getToolId()) == null) {
            throw new DefaultClientException("计量工具不存在");
        }
        
        record.setToolId(vo.getToolId());
        record.setMaintenancenUser(vo.getMaintenancenUser());
        record.setMaintenanceTime(vo.getMaintenanceTime());
        record.setCertificateNumber(vo.getCertificateNumber());
        record.setDescription(vo.getDescription());
        
        getBaseMapper().updateById(record);

        // 仅当该记录为该工具最新一条时，同步更新工具信息
        ShkbToolRecord latest = getBaseMapper().selectOne(
                Wrappers.lambdaQuery(ShkbToolRecord.class)
                        .eq(ShkbToolRecord::getToolId, record.getToolId())
                        .orderByDesc(ShkbToolRecord::getMaintenanceTime, ShkbToolRecord::getCreateTime)
                        .last("limit 1")
        );
        if (latest != null && latest.getId().equals(record.getId())) {
            ShkbTool tool = toolMapper.selectById(record.getToolId());
            if (tool == null) {
                throw new DefaultClientException("计量工具不存在");
            }
            // 更新上次计量时间
            tool.setLastMaintenanceTime(record.getMaintenanceTime());

            // 根据计量周期（天）计算下次计量日期与有效期
            Integer period = tool.getCalibrationPeriod();
            if (period != null && period > 0 && record.getMaintenanceTime() != null) {
                LocalDate nextDate = record.getMaintenanceTime().plusDays(period);
                tool.setNextMaintenanceTime(nextDate);
                tool.setExpirationTime(nextDate);
            }
            toolMapper.updateById(tool);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(String id) {
        ShkbToolRecord record = getBaseMapper().selectById(id);
        if (record == null) {
            throw new DefaultClientException("工具计量记录不存在");
        }
        
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
    
    @Override
    public boolean isLatestRecord(String recordId) {
        ShkbToolRecord record = getBaseMapper().selectById(recordId);
        if (record == null) {
            throw new DefaultClientException("工具计量记录不存在");
        }
        
        // 获取该工具最新的一条计量记录
        ShkbToolRecord latest = getBaseMapper().selectOne(
                Wrappers.lambdaQuery(ShkbToolRecord.class)
                        .eq(ShkbToolRecord::getToolId, record.getToolId())
                        .orderByDesc(ShkbToolRecord::getMaintenanceTime, ShkbToolRecord::getCreateTime)
                        .last("limit 1")
        );
        
        // 检查当前记录是否是最新的一条
        return latest != null && latest.getId().equals(recordId);
    }
}
