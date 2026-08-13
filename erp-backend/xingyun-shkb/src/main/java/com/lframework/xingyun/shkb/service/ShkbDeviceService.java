package com.lframework.xingyun.shkb.service;

import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.entity.ShkbDevice;
import com.lframework.xingyun.shkb.vo.device.CreateShkbDeviceVo;
import com.lframework.xingyun.shkb.vo.device.QueryShkbDeviceVo;
import com.lframework.xingyun.shkb.vo.device.UpdateShkbDeviceVo;

import java.util.List;

/**
* @author kison
* @description 针对表【shkb_device(设备管理)】的数据库操作Service
* @createDate 2025-06-06 10:07:22
*/
public interface ShkbDeviceService extends BaseMpService<ShkbDevice> {

    /**
     * 查询设备列表
     *
     * @param pageIndex
     * @param pageSize
     * @param vo
     * @return
     */
    PageResult<ShkbDevice> query(Integer pageIndex, Integer pageSize, QueryShkbDeviceVo vo);

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    ShkbDevice findById(String id);

    /**
     * 创建设备
     *
     * @param vo
     * @return
     */
    String create(CreateShkbDeviceVo vo);

    /**
     * 修改设备
     *
     * @param vo
     */
    void update(UpdateShkbDeviceVo vo);

    /**
     * 根据ID删除
     *
     * @param id
     */
    void deleteById(String id);

    /**
     * 批量删除
     *
     * @param ids
     */
    void deleteByIds(List<String> ids);
}
