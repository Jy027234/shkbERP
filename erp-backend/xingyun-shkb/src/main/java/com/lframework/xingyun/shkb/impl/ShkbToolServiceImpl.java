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
import com.lframework.xingyun.shkb.entity.ShkbTool;
import com.lframework.xingyun.shkb.entity.ShkbToolRecord;
import com.lframework.xingyun.shkb.mappers.ShkbToolMapper;
import com.lframework.xingyun.shkb.service.ShkbToolRecordService;
import com.lframework.xingyun.shkb.service.ShkbToolService;
import com.lframework.xingyun.shkb.service.ToolRecordFileService;
import com.lframework.xingyun.shkb.vo.tool.CreateShkbToolVo;
import com.lframework.xingyun.shkb.vo.tool.CreateToolRecordVo;
import com.lframework.xingyun.shkb.vo.tool.QueryShkbToolVo;
import com.lframework.xingyun.shkb.vo.tool.UpdateShkbToolVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
* @author kison
* @description 针对表【shkb_tool(工具管理)】的数据库操作Service实现
* @createDate 2025-06-06 10:07:22
*/
@Service
public class ShkbToolServiceImpl extends BaseMpServiceImpl<ShkbToolMapper, ShkbTool>
    implements ShkbToolService {

    @Autowired
    private ShkbToolRecordService shkbToolRecordService;
    
    @Autowired
    private ToolRecordFileService toolRecordFileService;

    @Override
    public PageResult<ShkbTool> query(Integer pageIndex, Integer pageSize, QueryShkbToolVo vo) {
        Assert.greaterThanZero(pageIndex);
        Assert.greaterThanZero(pageSize);

        PageHelperUtil.startPage(pageIndex, pageSize);
        List<ShkbTool> datas = getBaseMapper().query(vo);

        return PageResultUtil.convert(new PageInfo<>(datas));
    }

    @Override
    public ShkbTool findById(String id) {
        return getBaseMapper().selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UpdateShkbToolVo vo) {
        ShkbTool tool = getBaseMapper().selectById(vo.getId());
        if (tool == null) {
            throw new DefaultClientException("工具不存在");
        }
        
        // 检查管理编号是否已存在
        if (!tool.getCode().equals(vo.getCode())) {
            checkCodeExist(vo.getCode(), vo.getId());
        }
        
        tool.setCode(vo.getCode());
        tool.setName(vo.getName());
        tool.setManagementArea(vo.getManagementArea());
        tool.setCertificateNumber(vo.getCertificateNumber());
        tool.setModel(vo.getModel());
        tool.setSpecification(vo.getSpecification());
        tool.setStandard(vo.getStandard());
        tool.setPrecision(vo.getPrecision());
        tool.setStorageLocation(vo.getStorageLocation());
        tool.setLastMaintenanceTime(vo.getLastMaintenanceTime());
        tool.setNextMaintenanceTime(vo.getNextMaintenanceTime());
        tool.setCalibrationPeriod(vo.getCalibrationPeriod());
        tool.setLastMaintenanceUnit(vo.getLastMaintenanceUnit());
        tool.setAvailable(vo.getAvailable());
        tool.setDescription(vo.getDescription());
        // 计算维保到期日期 = 上次计量日期 + 计量周期(天)
        tool.setExpirationTime(calcExpiration(vo.getLastMaintenanceTime(), vo.getCalibrationPeriod()));
        tool.setUpdateTime(LocalDateTime.now());
        
        getBaseMapper().updateById(tool);
    }

    private void checkCodeExist(String code, String id) {
        LambdaQueryWrapper<ShkbTool> wrapper = Wrappers.lambdaQuery(ShkbTool.class)
                .eq(ShkbTool::getCode, code);
        if (StringUtil.isNotBlank(id)) {
            wrapper.ne(ShkbTool::getId, id);
        }
        
        if (getBaseMapper().selectCount(wrapper) > 0) {
            throw new DefaultClientException("管理编号已存在！");
        }
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
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String create(CreateShkbToolVo vo) {
        // 检查管理编号是否已存在
        checkCodeExist(vo.getCode(), null);
        
        ShkbTool tool = new ShkbTool();
        tool.setCode(vo.getCode());
        tool.setName(vo.getName());
        tool.setManagementArea(vo.getManagementArea());
        tool.setCertificateNumber(vo.getCertificateNumber());
        tool.setModel(vo.getModel());
        tool.setSpecification(vo.getSpecification());
        tool.setStandard(vo.getStandard());
        tool.setPrecision(vo.getPrecision());
        tool.setStorageLocation(vo.getStorageLocation());
        tool.setLastMaintenanceTime(vo.getLastMaintenanceTime());
        tool.setNextMaintenanceTime(vo.getNextMaintenanceTime());
        tool.setCalibrationPeriod(vo.getCalibrationPeriod());
        tool.setLastMaintenanceUnit(vo.getLastMaintenanceUnit());
        tool.setAvailable(vo.getAvailable());
        tool.setDescription(vo.getDescription());
        // 计算维保到期日期 = 上次计量日期 + 计量周期(天)
        tool.setExpirationTime(calcExpiration(vo.getLastMaintenanceTime(), vo.getCalibrationPeriod()));
        
        LocalDateTime now = LocalDateTime.now();
        tool.setCreateTime(now);
        tool.setUpdateTime(now);
        
        getBaseMapper().insert(tool);
        
        return tool.getId();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String create(CreateShkbToolVo vo, List<MultipartFile> files) {
        // 检查管理编号是否已存在
        checkCodeExist(vo.getCode(), null);
        
        ShkbTool tool = new ShkbTool();
        tool.setCode(vo.getCode());
        tool.setName(vo.getName());
        tool.setManagementArea(vo.getManagementArea());
        tool.setCertificateNumber(vo.getCertificateNumber());
        tool.setModel(vo.getModel());
        tool.setSpecification(vo.getSpecification());
        tool.setStandard(vo.getStandard());
        tool.setPrecision(vo.getPrecision());
        tool.setStorageLocation(vo.getStorageLocation());
        tool.setLastMaintenanceTime(vo.getLastMaintenanceTime());
        tool.setNextMaintenanceTime(vo.getNextMaintenanceTime());
        tool.setCalibrationPeriod(vo.getCalibrationPeriod());
        tool.setLastMaintenanceUnit(vo.getLastMaintenanceUnit());
        tool.setAvailable(vo.getAvailable());
        tool.setDescription(vo.getDescription());
        // 计算维保到期日期 = 上次计量日期 + 计量周期(天)
        tool.setExpirationTime(calcExpiration(vo.getLastMaintenanceTime(), vo.getCalibrationPeriod()));
        
        LocalDateTime now = LocalDateTime.now();
        tool.setCreateTime(now);
        tool.setUpdateTime(now);
        
        getBaseMapper().insert(tool);
        
        // 初始化计量记录
        CreateToolRecordVo recordVo = new CreateToolRecordVo();
        recordVo.setToolId(tool.getId());
        recordVo.setMaintenancenUser(vo.getMaintenancenUser());
        recordVo.setMaintenanceTime(vo.getLastMaintenanceTime());
        recordVo.setCertificateNumber(vo.getRecordCertificateNumber());
        recordVo.setDescription(vo.getDescription());
        
        String recordId = shkbToolRecordService.create(recordVo);
        
        // 上传附件（如果有）
        if (!CollectionUtil.isEmpty(files)) {
            toolRecordFileService.uploadToolRecordFiles(recordId, files);
        }
        
        return tool.getId();
    }

    /**
     * 计算维保到期日期
     * 规则：expirationTime = lastMaintenanceTime + calibrationPeriod(天)
     * 若任一参数为空或周期不是正整数，则返回 null
     */
    private LocalDate calcExpiration(LocalDate lastMaintenanceTime, Integer calibrationPeriod) {
        if (lastMaintenanceTime == null) {
            return null;
        }
        if (calibrationPeriod == null || calibrationPeriod <= 0) {
            return null;
        }
        return lastMaintenanceTime.plusDays(calibrationPeriod);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCertificateNumber(String toolId, String certificateNumber) {
        ShkbTool tool = getBaseMapper().selectById(toolId);
        if (tool == null) {
            throw new DefaultClientException("工具不存在");
        }
        
        tool.setCertificateNumber(certificateNumber);
        tool.setUpdateTime(LocalDateTime.now());
        
        getBaseMapper().updateById(tool);
    }
}
