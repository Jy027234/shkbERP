package com.lframework.xingyun.shkb.impl.contract;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageInfo;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.starter.web.core.utils.PageHelperUtil;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.xingyun.shkb.bo.contract.task.QueryContractTaskMaterialApplyBo;
import com.lframework.xingyun.shkb.entity.ContractTask;
import com.lframework.xingyun.shkb.entity.ContractTaskMaterialApply;
import com.lframework.xingyun.shkb.mappers.ContractTaskMaterialApplyMapper;
import com.lframework.xingyun.shkb.service.MaterialOrderService;
import com.lframework.xingyun.shkb.service.contract.ContractTaskMaterialApplyService;
import com.lframework.xingyun.shkb.service.contract.ContractTaskService;
import com.lframework.xingyun.shkb.vo.contract.task.ApproveContractTaskMaterialApplyVo;
import com.lframework.xingyun.shkb.vo.contract.task.CreateContractTaskMaterialApplyVo;
import com.lframework.xingyun.shkb.vo.contract.task.QueryContractTaskMaterialApplyVo;
import com.lframework.xingyun.shkb.vo.material.CreateMaterialOrderFromApplyVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



/**
* @author kison
* @description 针对表【shkb_contract_task_material_apply(领料申请)】的数据库操作Service实现
* @createDate 2025-06-04 17:14:59
*/
@Service
@Slf4j
public class ContractTaskMaterialApplyServiceImpl extends BaseMpServiceImpl<ContractTaskMaterialApplyMapper, ContractTaskMaterialApply>
    implements ContractTaskMaterialApplyService {
    
    @Autowired
    private ContractTaskService contractTaskService;

    @Autowired
    private MaterialOrderService materialOrderService;
    
    /**
     * 创建领料申请
     *
     * @param vo 创建领料申请VO
     * @return 领料申请ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String create(CreateContractTaskMaterialApplyVo vo) {
        // 检查任务是否存在
        ContractTask task = contractTaskService.getById(vo.getTaskId());
        if (task == null) {
            throw new DefaultClientException("合同任务不存在！");
        }
        
        // 检查是否已存在该任务的申请记录
        LambdaQueryWrapper<ContractTaskMaterialApply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ContractTaskMaterialApply::getTaskId, vo.getTaskId());
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new DefaultClientException("该任务已存在领料申请记录，不能重复申请！");
        }
        
        // 创建领料申请
        ContractTaskMaterialApply apply = new ContractTaskMaterialApply();
        apply.setId(IdUtil.getId());
        apply.setTaskId(vo.getTaskId());
        apply.setApplyCode(generateApplyCode());
        apply.setRemark(vo.getRemark());
        apply.setApprovalStatus(0); // 0表示待审批状态
        
        // 保存领料申请
        this.save(apply);
        
        log.info("创建领料申请成功，任务ID：{}，申请ID：{}", vo.getTaskId(), apply.getId());
        
        return apply.getId();
    }

    /**
     * 按任务ID将已审核通过的领料申请重置为待审状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetToPendingByTaskId(String taskId) {
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new DefaultClientException("任务ID不能为空！");
        }

        LambdaUpdateWrapper<ContractTaskMaterialApply> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ContractTaskMaterialApply::getTaskId, taskId)
                .eq(ContractTaskMaterialApply::getApprovalStatus, 1)
                .set(ContractTaskMaterialApply::getApprovalStatus, 0)
                .set(ContractTaskMaterialApply::getApprovalTime, null);

        this.update(null, updateWrapper);
    }

    /**
     * 按申请编号将已审核通过的领料申请重置为待审状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetToPendingByApplyCode(String applyCode) {
        if (applyCode == null || applyCode.trim().isEmpty()) {
            throw new DefaultClientException("申请编号不能为空！");
        }

        LambdaUpdateWrapper<ContractTaskMaterialApply> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ContractTaskMaterialApply::getApplyCode, applyCode)
                .eq(ContractTaskMaterialApply::getApprovalStatus, 1)
                .set(ContractTaskMaterialApply::getApprovalStatus, 0)
                .set(ContractTaskMaterialApply::getApprovalTime, null);

        this.update(null, updateWrapper);
    }
    
    /**
     * 生成申请编号
     *
     * @return 申请编号
     */
    private String generateApplyCode() {
        // 生成格式为 LL + 年月日 + 6位随机数的编号
        return "LL" + IdUtil.getId().substring(0, 14);
    }
    
    /**
     * 查询领料申请列表
     *
     * @param pageIndex 页码
     * @param pageSize  每页条数
     * @param vo        查询条件
     * @return 查询结果
     */
    @Override
    public PageResult<QueryContractTaskMaterialApplyBo> query(Integer pageIndex, Integer pageSize,
            QueryContractTaskMaterialApplyVo vo) {
        // 开启分页
        PageHelperUtil.startPage(pageIndex, pageSize);
        
        // 查询数据 - 直接映射到BO对象
        List<QueryContractTaskMaterialApplyBo> datas = getBaseMapper().query(vo);
        // 如果没有数据，直接返回空结果
        if (CollectionUtil.isEmpty(datas)) {
            List<QueryContractTaskMaterialApplyBo> emptyList = new ArrayList<>();
            PageInfo<QueryContractTaskMaterialApplyBo> pageInfo = new PageInfo<>(emptyList);
            return PageResultUtil.convert(pageInfo);
        }
        
        // 处理必换件单号和非必换件单号
        for (QueryContractTaskMaterialApplyBo bo : datas) {
            // 设置审批状态文本
            bo.setApprovalStatusText(bo.getApprovalStatus() != null ? 
                    bo.getApprovalStatus() == 0 ? "待审批" :
                    bo.getApprovalStatus() == 1 ? "审批通过" : "审批拒绝" : "");
            
            // 生成必换件单号和非必换件单号
            if (bo.getContractCode() != null && !bo.getContractCode().isEmpty()) {
                bo.setReplacementPartCode(bo.getContractCode() + "BHJ");
                bo.setNonReplacementPartCode(bo.getContractCode() + "FBH");
            }
        }
        
        // 创建 PageInfo 对象并返回分页结果
        PageInfo<QueryContractTaskMaterialApplyBo> pageInfo = new PageInfo<>(datas);
        return PageResultUtil.convert(pageInfo);
    }
    
    /**
     * 审批领料申请
     *
     * @param vo 审批领料申请VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(ApproveContractTaskMaterialApplyVo vo) {
        if (CollectionUtil.isEmpty(vo.getIds())) {
            throw new DefaultClientException("领料申请ID列表不能为空！");
        }
        
        // 审批状态：1-通过，2-拒绝
        int approvalStatus = vo.getApproved() ? 1 : 2;
        
        // 批量更新审批状态
        for (String id : vo.getIds()) {
            // 查询领料申请
            ContractTaskMaterialApply apply = this.getById(id);
            if (apply == null) {
                throw new DefaultClientException("领料申请不存在！ID: " + id);
            }
            
            // 检查是否已审批
            if (apply.getApprovalStatus() == 1) {
                throw new DefaultClientException("领料申请已审批，不能重复审批！申请编号: " + apply.getApplyCode());
            }
            
            // 更新审批状态
            LambdaUpdateWrapper<ContractTaskMaterialApply> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ContractTaskMaterialApply::getId, id);
            updateWrapper.set(ContractTaskMaterialApply::getApprovalStatus, approvalStatus);
            updateWrapper.set(ContractTaskMaterialApply::getApprovalTime, LocalDateTime.now());
            updateWrapper.set(ContractTaskMaterialApply::getRemark, 
                    vo.getComment() != null && !vo.getComment().isEmpty() ? 
                    (apply.getRemark() != null ? apply.getRemark() + "; " : "") + vo.getComment() : 
                    apply.getRemark());
            
            this.update(null, updateWrapper);

            log.info("领料申请审批成功，申请ID：{}，审批结果：{}", id, vo.getApproved() ? "通过" : "拒绝");
        }
    }
}
