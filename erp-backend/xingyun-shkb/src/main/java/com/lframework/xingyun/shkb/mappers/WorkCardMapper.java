package com.lframework.xingyun.shkb.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.shkb.bo.workcard.GetWorkCardBo;
import com.lframework.xingyun.shkb.bo.workcard.QueryWorkCardBo;
import com.lframework.xingyun.shkb.entity.WorkCard;
import com.lframework.xingyun.shkb.vo.workcard.QueryWorkCardVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author kison
* @description 针对表【shkb_work_card(工卡表)】的数据库操作Mapper
* @createDate 2025-05-15 15:52:38
* @Entity com.lframework.xingyun.shkb.entity.WorkCard
*/
@Mapper
public interface WorkCardMapper extends BaseMapper<WorkCard> {

    /**
     * 查询工卡列表
     *
     * @param vo 查询参数
     * @return 工卡列表
     */
    List<QueryWorkCardBo> query(QueryWorkCardVo vo);
    
    /**
     * 根据ID获取工卡详情
     *
     * @param id 工卡ID
     * @return 工卡详情
     */
    GetWorkCardBo getDetail(String id);
}
