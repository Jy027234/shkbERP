package com.lframework.xingyun.shkb.impl.contract;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.xingyun.shkb.entity.ContractTask;
import com.lframework.xingyun.shkb.entity.ContractTaskWorkCard;
import com.lframework.xingyun.shkb.mappers.ContractTaskWorkCardMapper;
import com.lframework.xingyun.shkb.service.contract.ContractTaskService;
import com.lframework.xingyun.shkb.service.contract.ContractTaskWorkCardService;
import com.lframework.xingyun.shkb.vo.contract.task.ContractTaskWorkCardVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 合同任务工卡Service实现
 *
 * @author kison
 */
@Service
public class ContractTaskWorkCardServiceImpl extends BaseMpServiceImpl<ContractTaskWorkCardMapper, ContractTaskWorkCard>
    implements ContractTaskWorkCardService {

    @Autowired
    private ContractTaskService contractTaskService;

    @Override
    public List<ContractTaskWorkCard> getByTaskId(String taskId) {
        if (StringUtil.isBlank(taskId)) {
            return new ArrayList<>();
        }

        return getBaseMapper().selectList(Wrappers.lambdaQuery(ContractTaskWorkCard.class)
                .eq(ContractTaskWorkCard::getTaskId, taskId));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchAdd(ContractTaskWorkCardVo vo) {
        // 验证任务是否存在
        ContractTask task = contractTaskService.getById(vo.getTaskId());
        if (task == null) {
            throw new DefaultClientException("合同任务不存在！");
        }

        if (CollectionUtil.isEmpty(vo.getWorkCardIds())) {
            return;
        }

        // 查询已存在的工卡
        List<ContractTaskWorkCard> existWorkCards = getBaseMapper().selectList(
                Wrappers.lambdaQuery(ContractTaskWorkCard.class)
                        .eq(ContractTaskWorkCard::getTaskId, vo.getTaskId())
                        .in(ContractTaskWorkCard::getWorkCardId, vo.getWorkCardIds()));

        // 已存在的工卡ID列表
        List<String> existWorkCardIds = existWorkCards.stream()
                .map(ContractTaskWorkCard::getWorkCardId)
                .collect(Collectors.toList());

        // 过滤出需要添加的工卡ID
        List<String> workCardIdsToAdd = vo.getWorkCardIds().stream()
                .filter(workCardId -> !existWorkCardIds.contains(workCardId))
                .collect(Collectors.toList());

        if (CollectionUtil.isEmpty(workCardIdsToAdd)) {
            return;
        }

        // 批量添加工卡
        List<ContractTaskWorkCard> workCardsToAdd = new ArrayList<>();
        for (String workCardId : workCardIdsToAdd) {
            ContractTaskWorkCard workCard = new ContractTaskWorkCard();
            workCard.setId(IdUtil.getId());
            workCard.setTaskId(vo.getTaskId());
            workCard.setWorkCardId(workCardId);
            workCardsToAdd.add(workCard);
        }

        saveBatch(workCardsToAdd);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchDelete(ContractTaskWorkCardVo vo) {
        // 验证任务是否存在
        ContractTask task = contractTaskService.getById(vo.getTaskId());
        if (task == null) {
            throw new DefaultClientException("合同任务不存在！");
        }

        if (CollectionUtil.isEmpty(vo.getWorkCardIds())) {
            return;
        }

        // 根据任务ID和工卡ID列表删除
        LambdaQueryWrapper<ContractTaskWorkCard> queryWrapper = Wrappers.lambdaQuery(ContractTaskWorkCard.class)
                .eq(ContractTaskWorkCard::getTaskId, vo.getTaskId())
                .in(ContractTaskWorkCard::getWorkCardId, vo.getWorkCardIds());

        getBaseMapper().delete(queryWrapper);
    }
}
