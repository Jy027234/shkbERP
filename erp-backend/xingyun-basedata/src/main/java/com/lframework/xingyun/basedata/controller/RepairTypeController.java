package com.lframework.xingyun.basedata.controller;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.xingyun.basedata.bo.repairType.GetRepairTypeBo;
import com.lframework.xingyun.basedata.bo.repairType.QueryRepairTypeBo;
import com.lframework.xingyun.basedata.bo.repairType.RepairTypeSelectorBo;
import com.lframework.xingyun.basedata.entity.RepairType;
import com.lframework.xingyun.basedata.service.repairType.RepairTypeService;
import com.lframework.xingyun.basedata.vo.repairType.CreateRepairTypeVo;
import com.lframework.xingyun.basedata.vo.repairType.QueryRepairTypeVo;
import com.lframework.xingyun.basedata.vo.repairType.RepairTypeSelectorVo;
import com.lframework.xingyun.basedata.vo.repairType.UpdateRepairTypeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 维修类型管理
 *
 * @author kison
 */
@Api(tags = "维修类型管理")
@Validated
@RestController
@RequestMapping("/basedata/repairtype")
public class RepairTypeController extends DefaultBaseController {

  @Autowired
  private RepairTypeService repairTypeService;

  /**
   * 维修类型列表
   */
  @ApiOperation("维修类型列表")
  @HasPermission({"base-data:repair-type:query", "base-data:repair-type:add",
      "base-data:repair-type:modify","maintenance:contract-task","maintenance:aviation","maintenance:factory-wb","maintenance:factory-l"})
  @GetMapping("/query")
  public InvokeResult<PageResult<QueryRepairTypeBo>> query(@Valid QueryRepairTypeVo vo) {

    PageResult<RepairType> pageResult = repairTypeService.query(getPageIndex(vo), getPageSize(vo), vo);

    List<RepairType> datas = pageResult.getDatas();
    List<QueryRepairTypeBo> results = null;

    if (!CollectionUtil.isEmpty(datas)) {
      results = datas.stream().map(QueryRepairTypeBo::new).collect(Collectors.toList());
    }

    return InvokeResultBuilder.success(PageResultUtil.rebuild(pageResult, results));
  }

  /**
   * 查询维修类型
   */
  @ApiOperation("查询维修类型")
  @ApiImplicitParam(value = "ID", name = "id", paramType = "query", required = true)
  @HasPermission({"base-data:repair-type:query", "base-data:repair-type:add",
      "base-data:repair-type:modify"})
  @GetMapping
  public InvokeResult<GetRepairTypeBo> get(@NotBlank(message = "ID不能为空！") String id) {

    RepairType data = repairTypeService.findById(id);
    if (data == null) {
      throw new DefaultClientException("维修类型不存在！");
    }

    GetRepairTypeBo result = new GetRepairTypeBo(data);

    return InvokeResultBuilder.success(result);
  }

  /**
   * 新增维修类型
   */
  @ApiOperation("新增维修类型")
  @HasPermission({"base-data:repair-type:add"})
  @PostMapping
  public InvokeResult<Void> create(@Valid CreateRepairTypeVo vo) {

    repairTypeService.create(vo);

    return InvokeResultBuilder.success();
  }

  /**
   * 修改维修类型
   */
  @ApiOperation("修改维修类型")
  @HasPermission({"base-data:repair-type:modify"})
  @PutMapping
  public InvokeResult<Void> update(@Valid UpdateRepairTypeVo vo) {

    repairTypeService.update(vo);

    repairTypeService.cleanCacheByKey(vo.getId());

    return InvokeResultBuilder.success();
  }
  
  /**
   * 维修类型选择器
   */
  @ApiOperation("维修类型选择器")
  @HasPermission({"base-data:repair-type:query", "base-data:repair-type:add",
      "base-data:repair-type:modify"})
  @GetMapping("/selector")
  public InvokeResult<PageResult<RepairTypeSelectorBo>> selector(@Valid RepairTypeSelectorVo vo) {

    PageResult<RepairType> pageResult = repairTypeService.selector(getPageIndex(vo), getPageSize(vo), vo);

    List<RepairType> datas = pageResult.getDatas();
    List<RepairTypeSelectorBo> results = null;

    if (!CollectionUtil.isEmpty(datas)) {
      results = datas.stream().map(RepairTypeSelectorBo::new).collect(Collectors.toList());
    }

    return InvokeResultBuilder.success(PageResultUtil.rebuild(pageResult, results));
  }
}
