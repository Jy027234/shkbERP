package com.lframework.xingyun.shkb.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.shkb.dto.contract.task.ContractTaskProductDto;
import com.lframework.xingyun.shkb.entity.WorkCardProduct;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* @author kison
* @description 针对表【shkb_work_card_product(工卡必换件表)】的数据库操作Mapper
* @createDate 2025-05-15 15:52:38
* @Entity com.lframework.xingyun.shkb.entity.ShkbWorkCardProduct
*/
@Mapper
public interface WorkCardProductMapper extends BaseMapper<WorkCardProduct> {

    /**
     * 根据任务ID获取必换件列表
     *
     * @param taskId 任务ID
     * @return 必换件列表
     */
    List<ContractTaskProductDto> getTaskReplacementParts(@Param("taskId") String taskId);
}

