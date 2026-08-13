package com.lframework.xingyun.basedata.service.repairType;

import com.lframework.starter.web.core.components.resp.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.basedata.entity.RepairType;
import com.lframework.xingyun.basedata.vo.repairType.CreateRepairTypeVo;
import com.lframework.xingyun.basedata.vo.repairType.QueryRepairTypeVo;
import com.lframework.xingyun.basedata.vo.repairType.RepairTypeSelectorVo;
import com.lframework.xingyun.basedata.vo.repairType.UpdateRepairTypeVo;
import java.util.List;

/**
* @author kison
* @description 针对表【base_data_repair_type(维修类型)】的数据库操作Service
* @createDate 2025-04-23 09:08:37
*/
public interface RepairTypeService extends BaseMpService<RepairType> {

  /**
   * 查询列表
   *
   * @return
   */
  PageResult<RepairType> query(Integer pageIndex, Integer pageSize, QueryRepairTypeVo vo);

  /**
   * 查询列表
   *
   * @param vo
   * @return
   */
  List<RepairType> query(QueryRepairTypeVo vo);

  /**
   * 选择器
   *
   * @param vo
   * @return
   */
  PageResult<RepairType> selector(Integer pageIndex, Integer pageSize,
      RepairTypeSelectorVo vo);

  /**
   * 根据ID查询
   *
   * @param id
   * @return
   */
  RepairType findById(String id);

  /**
   * 创建
   *
   * @param vo
   * @return
   */
  String create(CreateRepairTypeVo vo);

  /**
   * 修改
   *
   * @param vo
   */
  void update(UpdateRepairTypeVo vo);

  /**
   * 根据ID清除缓存
   * 
   * @param id
   */
  void cleanCacheByKey(String id);
}
