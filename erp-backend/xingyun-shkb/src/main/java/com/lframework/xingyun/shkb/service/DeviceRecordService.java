package com.lframework.xingyun.shkb.service;

import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.entity.DeviceRecord;
import com.lframework.xingyun.shkb.vo.device.CreateDeviceRecordVo;
import com.lframework.xingyun.shkb.vo.device.QueryDeviceRecordVo;
import com.lframework.xingyun.shkb.vo.device.UpdateDeviceRecordVo;

import java.util.List;

/**
* @author kison
* @description 针对表【shkb_device_record(设备管理)】的数据库操作Service
* @createDate 2025-06-06 10:07:22
*/
public interface DeviceRecordService extends BaseMpService<DeviceRecord> {

    /**
     * 查询设备维修记录列表
     *
     * @param pageIndex 页码
     * @param pageSize  每页条数
     * @param vo        查询条件
     * @return 设备维修记录列表
     */
    PageResult<DeviceRecord> query(Integer pageIndex, Integer pageSize, QueryDeviceRecordVo vo);

    /**
     * 根据ID查询设备维修记录
     *
     * @param id ID
     * @return 设备维修记录
     */
    DeviceRecord findById(String id);

    /**
     * 创建设备维修记录
     *
     * @param vo 创建设备维修记录VO
     * @return 设备维修记录ID
     */
    String create(CreateDeviceRecordVo vo);

    /**
     * 修改设备维修记录
     *
     * @param vo 修改设备维修记录VO
     */
    void update(UpdateDeviceRecordVo vo);

    /**
     * 根据ID删除设备维修记录
     *
     * @param id ID
     */
    void deleteById(String id);

    /**
     * 批量删除设备维修记录
     *
     * @param ids ID列表
     */
    void deleteByIds(List<String> ids);
}
