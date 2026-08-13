package com.lframework.xingyun.shkb.impl.workcard;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.Assert;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.starter.web.core.utils.PageHelperUtil;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.xingyun.shkb.bo.workcard.GetWorkCardBo;
import com.lframework.xingyun.shkb.bo.workcard.QueryWorkCardBo;
import com.lframework.xingyun.shkb.entity.WorkCard;
import com.lframework.xingyun.shkb.entity.WorkCardFile;
import com.lframework.xingyun.shkb.entity.WorkCardProduct;
import com.lframework.xingyun.shkb.entity.ContractTaskWorkCard;
import com.lframework.xingyun.shkb.mappers.WorkCardMapper;
import com.lframework.xingyun.shkb.mappers.WorkCardFileMapper;
import com.lframework.xingyun.shkb.mappers.WorkCardProductMapper;
import com.lframework.xingyun.shkb.mappers.ContractTaskWorkCardMapper;
import com.lframework.xingyun.shkb.service.workcard.WorkCardService;
import com.lframework.xingyun.shkb.vo.workcard.CreateWorkCardVo;
import com.lframework.xingyun.shkb.vo.workcard.QueryWorkCardVo;
import com.lframework.xingyun.shkb.vo.workcard.UpdateWorkCardVo;
import lombok.extern.slf4j.Slf4j;
import com.lframework.xingyun.basedata.mappers.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.lframework.xingyun.shkb.utils.DateTimeUtils;

/**
* @author kison
* @description 针对表【shkb_work_card(工卡表)】的数据库操作Service实现
* @createDate 2025-05-15 15:52:38
*/
@Service
@Slf4j
public class WorkCardServiceImpl extends BaseMpServiceImpl<WorkCardMapper, WorkCard>
    implements WorkCardService {

    @Autowired
    private ContractTaskWorkCardMapper contractTaskWorkCardMapper;

    @Autowired
    private WorkCardFileMapper workCardFileMapper;

    @Autowired
    private WorkCardProductMapper workCardProductMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public PageResult<QueryWorkCardBo> query(Integer pageIndex, Integer pageSize, QueryWorkCardVo vo) {
        Assert.greaterThanZero(pageIndex);
        Assert.greaterThanZero(pageSize);

        PageHelperUtil.startPage(pageIndex, pageSize);

        // 直接使用Mapper查询并映射到BO类
        List<QueryWorkCardBo> datas = getBaseMapper().query(vo);

        PageInfo<QueryWorkCardBo> pageInfo = new PageInfo<>(datas);

        return PageResultUtil.convert(pageInfo);
    }

    @Override
    public GetWorkCardBo getDetail(String id) {
        // 直接使用Mapper查询并映射到BO类
        return getBaseMapper().getDetail(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String create(CreateWorkCardVo vo) {
        // 校验件号对应航材是否存在
        Assert.notBlank(vo.getPartNumberId(), "件号不能为空！");
        if (productMapper.selectById(vo.getPartNumberId()) == null) {
            throw new DefaultClientException("件号对应的航材不存在！");
        }
        // 校验工卡号是否重复
        LambdaQueryWrapper<WorkCard> checkWrapper = Wrappers.lambdaQuery(WorkCard.class)
                .eq(WorkCard::getCode, vo.getCode());
        if (this.count(checkWrapper) > 0) {
            throw new DefaultClientException("工卡号重复，请重新输入！");
        }
        WorkCard data = new WorkCard();
        data.setId(IdUtil.getId());
        data.setCode(vo.getCode());
        data.setName(vo.getName());
        data.setPartNumberId(vo.getPartNumberId());
        data.setRepairTypeId(vo.getRepairTypeId());
        data.setCustomerId(vo.getCustomerId());
        data.setApprovalDate(vo.getApprovalDate());
        data.setAvailable(vo.getAvailable());
        data.setDescription(vo.getDescription());
        data.setVersion(vo.getVersion());

        this.save(data);

        return data.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UpdateWorkCardVo vo) {
        WorkCard data = this.getById(vo.getId());
        if (data == null) {
            throw new DefaultClientException("工卡不存在！");
        }

        LambdaQueryWrapper<WorkCard> checkWrapper = Wrappers.lambdaQuery(WorkCard.class)
                .eq(WorkCard::getCode, vo.getCode())
                .ne(WorkCard::getId, vo.getId());
        if (this.count(checkWrapper) > 0) {
            throw new DefaultClientException("工卡号重复，请重新输入！");
        }

        data.setCode(vo.getCode());
        data.setName(vo.getName());
        data.setPartNumberId(vo.getPartNumberId());
        data.setRepairTypeId(vo.getRepairTypeId());
        // 当前端未传 customerId 时，表示需要清空客户信息
        if (vo.getCustomerId() != null) {
            data.setCustomerId(vo.getCustomerId());
        } else {
            // 先置空，部分全局策略可能忽略 null 更新，后续再强制更新
            data.setCustomerId(null);
        }
        data.setApprovalDate(vo.getApprovalDate());
        data.setAvailable(vo.getAvailable());
        data.setDescription(vo.getDescription());
        data.setVersion(vo.getVersion());

        this.updateById(data);

        // 如果 customerId 未传递（为 null），MyBatis-Plus 可能默认忽略空值更新，需强制置空
        if (vo.getCustomerId() == null) {
            this.lambdaUpdate()
                .set(WorkCard::getCustomerId, null)
                .eq(WorkCard::getId, vo.getId())
                .update();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(String id) {
        WorkCard data = this.getById(id);
        if (data == null) {
            throw new DefaultClientException("工卡不存在！");
        }

        // 校验是否被合同任务使用
        LambdaQueryWrapper<ContractTaskWorkCard> useCheckWrapper = Wrappers.lambdaQuery(ContractTaskWorkCard.class)
                .eq(ContractTaskWorkCard::getWorkCardId, id);
        Long usingCount = contractTaskWorkCardMapper.selectCount(useCheckWrapper);
        if (usingCount != null && usingCount > 0) {
            throw new DefaultClientException("该工卡正在任务中使用，无法删除！");
        }

        // 先删除工卡附件
        LambdaQueryWrapper<WorkCardFile> fileWrapper = Wrappers.lambdaQuery(WorkCardFile.class)
                .eq(WorkCardFile::getWorkCardId, id);
        workCardFileMapper.delete(fileWrapper);

        // 再删除工卡必换件
        LambdaQueryWrapper<WorkCardProduct> productWrapper = Wrappers.lambdaQuery(WorkCardProduct.class)
                .eq(WorkCardProduct::getWorkCardId, id);
        workCardProductMapper.delete(productWrapper);

        // 最后删除工卡
        this.removeById(id);
    }


}
