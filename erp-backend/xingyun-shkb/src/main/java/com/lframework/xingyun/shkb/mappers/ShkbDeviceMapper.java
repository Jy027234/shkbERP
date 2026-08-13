package com.lframework.xingyun.shkb.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.shkb.entity.ShkbDevice;
import com.lframework.xingyun.shkb.vo.device.QueryShkbDeviceVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author kison
* @description 针对表【shkb_device(设备管理)】的数据库操作Mapper
* @createDate 2025-06-06 10:07:22
* @Entity com.lframework.xingyun.shkb.entity.ShkbDevice
*/
@Mapper
public interface ShkbDeviceMapper extends BaseMapper<ShkbDevice> {

    /**
     * 查询设备列表
     * 
     * @param vo 查询条件
     * @return 设备列表
     */
    List<ShkbDevice> query(QueryShkbDeviceVo vo);
}
