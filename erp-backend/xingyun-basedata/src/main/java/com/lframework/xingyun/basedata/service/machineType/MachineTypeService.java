package com.lframework.xingyun.basedata.service.machineType;

import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.basedata.entity.MachineType;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lframework.xingyun.basedata.vo.machineType.CreateMachineTypeVo;
import com.lframework.xingyun.basedata.vo.machineType.MachineTypeSelectorVo;
import com.lframework.xingyun.basedata.vo.machineType.QueryMachineTypeVo;
import com.lframework.xingyun.basedata.vo.machineType.UpdateMachineTypeVo;

/**
* @author kison
* @description 针对表【base_data_machine_type(机型)】的数据库操作Service
* @createDate 2025-04-22 14:54:44
*/
public interface MachineTypeService extends BaseMpService<MachineType> {

  /**
   * 查询机型列表
   *
   * @param pageIndex 页码
   * @param pageSize  每页条数
   * @param vo        参数
   * @return 机型列表
   */
  PageResult<MachineType> query(Integer pageIndex, Integer pageSize, QueryMachineTypeVo vo);

  /**
   * 根据ID查询
   *
   * @param id ID
   * @return 机型
   */
  MachineType findById(String id);

  /**
   * 创建机型
   *
   * @param vo 参数
   * @return 机型ID
   */
  String create(CreateMachineTypeVo vo);

  /**
   * 修改机型
   *
   * @param vo 参数
   */
  void update(UpdateMachineTypeVo vo);

  /**
   * 根据ID清除缓存
   *
   * @param id ID
   */
  void cleanCacheByKey(String id);
  
  /**
   * 选择器
   *
   * @param vo 参数
   * @return 机型列表
   */
  PageResult<MachineType> selector(Integer pageIndex, Integer pageSize, MachineTypeSelectorVo vo);

  /**
   * 删除机型，同时清理商品(Product)上与该机型的关联（machineTypeId 置空）
   *
   * @param id 机型ID
   */
  void deleteById(String id);
}
