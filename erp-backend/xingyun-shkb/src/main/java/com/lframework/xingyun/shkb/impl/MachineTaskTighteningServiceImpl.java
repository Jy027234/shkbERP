package com.lframework.xingyun.shkb.impl;

import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.PageHelperUtil;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.xingyun.shkb.entity.MachineTaskTightening;
import com.lframework.xingyun.shkb.service.MachineTaskTighteningService;
import com.lframework.xingyun.shkb.mappers.MachineTaskTighteningMapper;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageInfo;
import com.lframework.starter.common.utils.Assert;
import java.util.List;
import com.lframework.xingyun.shkb.vo.machinetask.ReportMachineTaskTighteningVo;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import java.time.LocalDateTime;
import com.lframework.starter.web.core.utils.JsonUtil;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.xingyun.shkb.bo.machinetask.GetMachineTaskTighteningBo;
import org.springframework.transaction.annotation.Transactional;

/**
* @author kison
* @description 针对表【shkb_machine_task_tightening(拧紧机任务表)】的数据库操作Service实现
* @createDate 2025-10-22 10:16:56
*/
@Service
public class MachineTaskTighteningServiceImpl extends BaseMpServiceImpl<MachineTaskTighteningMapper, MachineTaskTightening>
    implements MachineTaskTighteningService{

    @Override
    public PageResult<MachineTaskTightening> query(Integer pageIndex, Integer pageSize, com.lframework.xingyun.shkb.vo.machinetask.QueryMachineTaskTighteningVo vo) {
        Assert.greaterThanZero(pageIndex);
        Assert.greaterThanZero(pageSize);
        PageHelperUtil.startPage(pageIndex, pageSize);
        List<MachineTaskTightening> datas = this.getBaseMapper().query(vo);
        return PageResultUtil.convert(new PageInfo<>(datas));
    }

    @Override
    public List<MachineTaskTightening> queryPending() {
        return this.getBaseMapper().queryPending();
    }

    @Override
    public GetMachineTaskTighteningBo getDetailById(String id) {
        MachineTaskTightening data = this.getById(id);
        if (data == null) {
            throw new DefaultClientException("任务不存在！");
        }
        GetMachineTaskTighteningBo bo = new GetMachineTaskTighteningBo();
        bo.setId(data.getId());
        bo.setTaskId(data.getTaskId());
        bo.setMachineTaskStatus(data.getMachineTaskStatus());
        bo.setTaskType(data.getTaskType());
        bo.setContractNo(data.getContractNo());
        bo.setSerialNo(data.getSerialNo());
        bo.setPartNo(data.getPartNo());
        bo.setCreateTime(data.getCreateTime());
        bo.setReportTime(data.getReportTime());
        if (StringUtil.isNotBlank(data.getReportData())) {
            ReportMachineTaskTighteningVo.ReportDataVo report = JsonUtil.parseObject(
                data.getReportData(), ReportMachineTaskTighteningVo.ReportDataVo.class);
            bo.setReportData(report);
        }
        return bo;
    }

    @Override
    public MachineTaskTightening getByTaskId(String taskId) {
        return this.lambdaQuery()
                .eq(MachineTaskTightening::getTaskId, taskId)
                .last("limit 1")
                .one();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void report(ReportMachineTaskTighteningVo vo) {
        String json = JsonUtil.toJsonString(vo.getReportData());

        // 1) 有 taskId：按 taskId 更新（平台任务 taskType=0）
        if (StringUtil.isNotBlank(vo.getTaskId())) {
            MachineTaskTightening current = getByTaskId(vo.getTaskId());
            if (current == null) {
                throw new DefaultClientException("任务不存在，无法上报！");
            }
            if (MachineTaskRules.isIdempotentReport(
                    current.getMachineTaskStatus(), current.getReportData(), json)) {
                return;
            }

            boolean updated = this.lambdaUpdate()
                    .set(MachineTaskTightening::getMachineTaskStatus, 1)
                    .set(MachineTaskTightening::getReportData, json)
                    .set(MachineTaskTightening::getReportTime, LocalDateTime.now())
                    .set(MachineTaskTightening::getTaskType, 0)
                    // 可选：如果传了合同/序列/件号则一并覆盖
                    .set(StringUtil.isNotBlank(vo.getContractNo()), MachineTaskTightening::getContractNo, vo.getContractNo())
                    .set(StringUtil.isNotBlank(vo.getSerialNo()), MachineTaskTightening::getSerialNo, vo.getSerialNo())
                    .set(StringUtil.isNotBlank(vo.getPartNo()), MachineTaskTightening::getPartNo, vo.getPartNo())
                    .eq(MachineTaskTightening::getTaskId, vo.getTaskId())
                    .eq(MachineTaskTightening::getMachineTaskStatus, 0)
                    .update();

            if (!updated) {
                MachineTaskTightening latest = getByTaskId(vo.getTaskId());
                if (latest != null && MachineTaskRules.isIdempotentReport(
                        latest.getMachineTaskStatus(), latest.getReportData(), json)) {
                    return;
                }
                throw new DefaultClientException("任务已被处理，无法上报！");
            }
            return;
        }

        // 2) 无 taskId：按 合同号+序列号+件号 匹配且 task_id 为空/空串 的记录更新，否则新增（线下任务 taskType=1）
        if (StringUtil.isBlank(vo.getContractNo()) || StringUtil.isBlank(vo.getSerialNo()) || StringUtil.isBlank(vo.getPartNo())) {
            throw new DefaultClientException("当 taskId 为空时，合同号、序列号、件号均不能为空！");
        }

        MachineTaskTightening exist = this.lambdaQuery()
                .and(w -> w.isNull(MachineTaskTightening::getTaskId).or().eq(MachineTaskTightening::getTaskId, ""))
                .eq(MachineTaskTightening::getContractNo, vo.getContractNo())
                .eq(MachineTaskTightening::getSerialNo, vo.getSerialNo())
                .eq(MachineTaskTightening::getPartNo, vo.getPartNo())
                .last("limit 1")
                .one();

        if (exist != null) {
            if (MachineTaskRules.isIdempotentReport(
                    exist.getMachineTaskStatus(), exist.getReportData(), json)) {
                return;
            }
            boolean updated = this.lambdaUpdate()
                    .set(MachineTaskTightening::getMachineTaskStatus, 1)
                    .set(MachineTaskTightening::getReportData, json)
                    .set(MachineTaskTightening::getReportTime, LocalDateTime.now())
                    .set(MachineTaskTightening::getTaskType, 1)
                    .eq(MachineTaskTightening::getId, exist.getId())
                    .eq(MachineTaskTightening::getMachineTaskStatus, 0)
                    .update();
            if (!updated) {
                MachineTaskTightening latest = this.getById(exist.getId());
                if (latest != null && MachineTaskRules.isIdempotentReport(
                        latest.getMachineTaskStatus(), latest.getReportData(), json)) {
                    return;
                }
                throw new DefaultClientException("任务已被处理，无法上报！");
            }
            return;
        }

        // 不存在则新增一条上报记录
        MachineTaskTightening data = new MachineTaskTightening();
        data.setId(IdUtil.getId());
        data.setTaskId(null);
        data.setMachineTaskStatus(1);
        data.setContractNo(vo.getContractNo());
        data.setSerialNo(vo.getSerialNo());
        data.setPartNo(vo.getPartNo());
        data.setTaskType(1);
        data.setCreateTime(LocalDateTime.now());
        data.setReportTime(LocalDateTime.now());
        data.setReportData(json);
        this.save(data);
    }
}



