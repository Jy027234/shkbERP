package com.lframework.xingyun.basedata.controller;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.xingyun.basedata.bo.partNumber.GetPartNumberBo;
import com.lframework.xingyun.basedata.bo.partNumber.QueryPartNumberBo;
import com.lframework.xingyun.basedata.entity.PartNumber;
import com.lframework.xingyun.basedata.service.partNumber.PartNumberService;
import com.lframework.xingyun.basedata.vo.partNumber.CreatePartNumberVo;
import com.lframework.xingyun.basedata.vo.partNumber.QueryPartNumberVo;
import com.lframework.xingyun.basedata.vo.partNumber.UpdatePartNumberVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
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
 * 件号管理
 *
 * @author kison
 */
@Api(tags = "件号管理")
@Validated
@RestController
@RequestMapping("/basedata/partnumber")
public class PartNumberController extends DefaultBaseController {

  @Autowired
  private PartNumberService partNumberService;

  /**
   * 件号列表
   */
  @ApiOperation("件号列表")
  @HasPermission({"base-data:part-number:query", "base-data:part-number:add",
      "base-data:part-number:modify"})
  @GetMapping("/query")
  public InvokeResult<PageResult<QueryPartNumberBo>> query(@Valid QueryPartNumberVo vo) {

    PageResult<QueryPartNumberBo> pageResult = partNumberService.query(getPageIndex(vo), getPageSize(vo), vo);

    return InvokeResultBuilder.success(pageResult);
  }

  /**
   * 查询件号
   */
  @ApiOperation("查询件号")
  @ApiImplicitParam(value = "ID", name = "id", paramType = "query", required = true)
  @HasPermission({"base-data:part-number:query", "base-data:part-number:add",
      "base-data:part-number:modify"})
  @GetMapping
  public InvokeResult<GetPartNumberBo> get(@NotBlank(message = "ID不能为空！") String id) {

    GetPartNumberBo result = partNumberService.getPartNumberDetail(id);

    return InvokeResultBuilder.success(result);
  }

  /**
   * 新增件号
   */
  @ApiOperation("新增件号")
  @HasPermission({"base-data:part-number:add"})
  @PostMapping
  public InvokeResult<Void> create(@Valid CreatePartNumberVo vo) {

    partNumberService.create(vo);

    return InvokeResultBuilder.success();
  }

  /**
   * 修改件号
   */
  @ApiOperation("修改件号")
  @HasPermission({"base-data:part-number:modify"})
  @PutMapping
  public InvokeResult<Void> update(@Valid UpdatePartNumberVo vo) {

    partNumberService.update(vo);

    partNumberService.cleanCacheByKey(vo.getId());

    return InvokeResultBuilder.success();
  }
}
