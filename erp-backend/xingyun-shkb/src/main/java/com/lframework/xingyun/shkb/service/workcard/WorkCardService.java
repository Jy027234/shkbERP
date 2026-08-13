package com.lframework.xingyun.shkb.service.workcard;

import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.bo.workcard.GetWorkCardBo;
import com.lframework.xingyun.shkb.bo.workcard.QueryWorkCardBo;
import com.lframework.xingyun.shkb.entity.WorkCard;
import com.lframework.xingyun.shkb.vo.workcard.CreateWorkCardVo;
import com.lframework.xingyun.shkb.vo.workcard.QueryWorkCardVo;
import com.lframework.xingyun.shkb.vo.workcard.UpdateWorkCardVo;

/**
* @author kison
* @description 针对表【shkb_work_card(工卡表)】的数据库操作Service
* @createDate 2025-05-15 15:52:38
*/
public interface WorkCardService extends BaseMpService<WorkCard> {

    /**
     * 查询工卡列表
     *
     * @param pageIndex 页码
     * @param pageSize  每页条数
     * @param vo        参数
     * @return
     */
    PageResult<QueryWorkCardBo> query(Integer pageIndex, Integer pageSize, QueryWorkCardVo vo);

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    GetWorkCardBo getDetail(String id);

    /**
     * 创建工卡
     *
     * @param vo 参数
     * @return 工卡ID
     */
    String create(CreateWorkCardVo vo);

    /**
     * 修改工卡
     *
     * @param vo 参数
     */
    void update(UpdateWorkCardVo vo);

    /**
     * 根据ID删除
     *
     * @param id ID
     */
    void deleteById(String id);
}
