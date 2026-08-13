package com.lframework.xingyun.basedata.service.partNumber;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.basedata.bo.partNumber.GetPartNumberBo;
import com.lframework.xingyun.basedata.bo.partNumber.PartNumberSelectorBo;
import com.lframework.xingyun.basedata.bo.partNumber.QueryPartNumberBo;
import com.lframework.xingyun.basedata.entity.PartNumber;
import com.lframework.xingyun.basedata.vo.partNumber.CreatePartNumberVo;
import com.lframework.xingyun.basedata.vo.partNumber.PartNumberSelectorVo;
import com.lframework.xingyun.basedata.vo.partNumber.QueryPartNumberVo;
import com.lframework.xingyun.basedata.vo.partNumber.UpdatePartNumberVo;
import java.util.List;

/**
* @author kison
* @description 针对表【base_data_part_number(件号)】的数据库操作Service
* @createDate 2025-04-23 10:10:53
*/
public interface PartNumberService extends BaseMpService<PartNumber> {

  /**
   * 查询列表
   *
   * @return
   */
  PageResult<QueryPartNumberBo> query(Integer pageIndex, Integer pageSize, QueryPartNumberVo vo);

  /**
   * 查询列表
   *
   * @param vo
   * @return
   */
  List<QueryPartNumberBo> query(QueryPartNumberVo vo);

  /**
   * 选择器
   *
   * @param vo
   * @return
   */
  PageResult<PartNumberSelectorBo> selector(Integer pageIndex, Integer pageSize,
      PartNumberSelectorVo vo);

  /**
   * 根据ID查询
   *
   * @param id
   * @return
   */
  PartNumber findById(String id);

  /**
   * 创建
   *
   * @param vo
   * @return
   */
  String create(CreatePartNumberVo vo);

  /**
   * 修改
   *
   * @param vo
   */
  void update(UpdatePartNumberVo vo);
  
  /**
   * 清除指定键的缓存
   *
   * @param key
   */
  void cleanCacheByKey(String key);

  GetPartNumberBo getPartNumberDetail(String id);
}
