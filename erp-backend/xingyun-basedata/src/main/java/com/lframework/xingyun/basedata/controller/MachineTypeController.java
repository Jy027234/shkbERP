package com.lframework.xingyun.basedata.controller;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.xingyun.basedata.bo.machineType.GetMachineTypeBo;
import com.lframework.xingyun.basedata.bo.machineType.QueryMachineTypeBo;
import com.lframework.xingyun.basedata.entity.MachineType;
import com.lframework.xingyun.basedata.service.machineType.MachineTypeService;
import com.lframework.xingyun.basedata.vo.machineType.CreateMachineTypeVo;
import com.lframework.xingyun.basedata.vo.machineType.QueryMachineTypeVo;
import com.lframework.xingyun.basedata.vo.machineType.UpdateMachineTypeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 机型管理
 *
 * @author kison
 */
@Api(tags = "机型管理")
@Validated
@RestController
@RequestMapping("/basedata/machinetype")
public class MachineTypeController extends DefaultBaseController {

  @Autowired
  private MachineTypeService machineTypeService;

  /**
   * 机型列表
   */
  @ApiOperation("机型列表")
  @HasPermission({"base-data:machine-type:query", "base-data:machine-type:add",
      "base-data:machine-type:modify","maintenance:contract-task","maintenance:aviation","maintenance:factory-wb","maintenance:factory-l"})
  @GetMapping("/query")
  public InvokeResult<PageResult<QueryMachineTypeBo>> query(@Valid QueryMachineTypeVo vo) {

    PageResult<MachineType> pageResult = machineTypeService.query(getPageIndex(vo), getPageSize(vo), vo);

    List<MachineType> datas = pageResult.getDatas();
    List<QueryMachineTypeBo> results = null;

    if (!CollectionUtil.isEmpty(datas)) {
      results = datas.stream().map(QueryMachineTypeBo::new).collect(Collectors.toList());
    }

    return InvokeResultBuilder.success(PageResultUtil.rebuild(pageResult, results));
  }

  /**
   * 查询机型
   */
  @ApiOperation("查询机型")
  @ApiImplicitParam(value = "ID", name = "id", paramType = "query", required = true)
  @HasPermission({"base-data:machine-type:query", "base-data:machine-type:add",
      "base-data:machine-type:modify","maintenance:contract-task","maintenance:aviation","maintenance:factory-wb","maintenance:factory-l"})
  @GetMapping
  public InvokeResult<GetMachineTypeBo> get(@NotBlank(message = "ID不能为空！") String id) {

    MachineType data = machineTypeService.findById(id);
    if (data == null) {
      throw new DefaultClientException("机型不存在！");
    }

    GetMachineTypeBo result = new GetMachineTypeBo(data);

    return InvokeResultBuilder.success(result);
  }

  /**
   * 新增机型
   */
  @ApiOperation("新增机型")
  @HasPermission({"base-data:machine-type:add"})
  @PostMapping
  public InvokeResult<Void> create(@Valid CreateMachineTypeVo vo) {

    machineTypeService.create(vo);

    return InvokeResultBuilder.success();
  }

  /**
   * 修改机型
   */
  @ApiOperation("修改机型")
  @HasPermission({"base-data:machine-type:modify"})
  @PutMapping
  public InvokeResult<Void> update(@Valid UpdateMachineTypeVo vo) {

    machineTypeService.update(vo);

    machineTypeService.cleanCacheByKey(vo.getId());

    return InvokeResultBuilder.success();
  }

  /**
   * 删除机型
   */
  @ApiOperation("删除机型")
  @ApiImplicitParam(value = "ID", name = "id", paramType = "query", required = true)
  @HasPermission({"base-data:machine-type:delete"})
  @DeleteMapping("/delete/{id}")
  public InvokeResult<Void> delete(@NotBlank(message = "ID不能为空") @PathVariable String id) {

    machineTypeService.deleteById(id);

    return InvokeResultBuilder.success();
  }
}
