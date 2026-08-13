package com.lframework.xingyun.shkb.service;

import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.entity.ShkbToolRecord;
import com.lframework.xingyun.shkb.vo.tool.CreateToolRecordVo;
import com.lframework.xingyun.shkb.vo.tool.QueryToolRecordVo;
import com.lframework.xingyun.shkb.vo.tool.UpdateToolRecordVo;

import java.util.List;

/**
* @author kison
* @description 针对表【shkb_tool_record(计量记录)】的数据库操作Service
* @createDate 2025-06-24 15:00:58
*/
public interface ShkbToolRecordService extends BaseMpService<ShkbToolRecord> {

    /**
     * 查询工具计量记录列表
     *
     * @param pageIndex 页码
     * @param pageSize  每页条数
     * @param vo        参数
     * @return
     */
    PageResult<ShkbToolRecord> query(Integer pageIndex, Integer pageSize, QueryToolRecordVo vo);

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    ShkbToolRecord findById(String id);

    /**
     * 创建
     *
     * @param vo
     * @return
     */
    String create(CreateToolRecordVo vo);

    /**
     * 修改
     *
     * @param vo
     */
    void update(UpdateToolRecordVo vo);

    /**
     * 根据ID删除
     *
     * @param id
     */
    void deleteById(String id);

    /**
     * 根据IDs批量删除
     *
     * @param ids
     */
    void deleteByIds(List<String> ids);
    
    /**
     * 检查是否是最新的一条计量记录
     *
     * @param recordId 计量记录ID
     * @return 是否是最新的一条计量记录
     */
    boolean isLatestRecord(String recordId);
}
