package com.lframework.xingyun.shkb.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.shkb.entity.ShkbTool;
import com.lframework.xingyun.shkb.vo.tool.QueryShkbToolVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author kison
* @description 针对表【shkb_tool(工具管理)】的数据库操作Mapper
* @createDate 2025-06-06 10:07:22
* @Entity com.lframework.xingyun.shkb.entity.ShkbTool
*/
@Mapper
public interface ShkbToolMapper extends BaseMapper<ShkbTool> {

    /**
     * 查询工具列表
     *
     * @param vo 查询条件
     * @return 工具列表
     */
    List<ShkbTool> query(QueryShkbToolVo vo);
}
