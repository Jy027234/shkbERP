package com.lframework.xingyun.shkb.controller;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.starter.web.core.components.excel.ExcelMultipartWriterSheetBuilder;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.ExcelUtil;
import com.lframework.xingyun.shkb.entity.ShkbAuthorizationProject;
import com.lframework.xingyun.shkb.entity.ShkbAuthorizationRequiredCourse;
import com.lframework.xingyun.shkb.excel.authorization.AuthorizationProjectExportModel;
import com.lframework.xingyun.shkb.service.ShkbAuthorizationProjectService;
import com.lframework.xingyun.shkb.service.ShkbAuthorizationRequiredCourseService;
import com.lframework.xingyun.shkb.vo.authorization.CreateShkbAuthorizationProjectVo;
import com.lframework.xingyun.shkb.vo.authorization.QueryShkbAuthorizationProjectVo;
import com.lframework.xingyun.shkb.vo.authorization.UpdateShkbAuthorizationProjectVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(
   tags = {"授权项目管理"}
)
@Validated
@RestController
@RequestMapping({"/shkb/authorization-project"})
public class AuthorizationProjectController extends DefaultBaseController {
   @Autowired
   private ShkbAuthorizationProjectService authorizationProjectService;
   @Autowired
   private ShkbAuthorizationRequiredCourseService authorizationRequiredCourseService;

   @ApiOperation("查询授权项目列表")
   @HasPermission({"hr:authorization:query"})
   @PostMapping({"/query"})
   public InvokeResult<PageResult<ShkbAuthorizationProject>> query(@Valid @RequestBody QueryShkbAuthorizationProjectVo vo) {
      PageResult<ShkbAuthorizationProject> pageResult = this.authorizationProjectService.query(this.getPageIndex(vo), this.getPageSize(vo), vo);
      return InvokeResultBuilder.success(pageResult);
   }

   @ApiOperation("查询所有启用的授权项目")
   @HasPermission({"hr:authorization:query"})
   @GetMapping({"/list/enabled"})
   public InvokeResult<List<ShkbAuthorizationProject>> queryEnabled() {
      List<ShkbAuthorizationProject> datas = this.authorizationProjectService.queryByStatus(1);
      return InvokeResultBuilder.success(datas);
   }

   @ApiOperation("获取所有有效授权项目")
   @HasPermission({"hr:authorization:query"})
   @GetMapping({"/all-valid"})
   public InvokeResult<List<ShkbAuthorizationProject>> getAllValid() {
      List<ShkbAuthorizationProject> datas = this.authorizationProjectService.queryByStatus(1);
      return InvokeResultBuilder.success(datas);
   }

   @ApiOperation("查询授权项目详情")
   @ApiImplicitParam(
      value = "ID",
      name = "id",
      paramType = "path",
      required = true
   )
   @HasPermission({"hr:authorization:query"})
   @GetMapping({"/{id}"})
   public InvokeResult<ShkbAuthorizationProject> get(@NotBlank(message = "ID不能为空") @PathVariable String id) {
      ShkbAuthorizationProject data = this.authorizationProjectService.findById(id);
      if (data == null) {
         throw new DefaultClientException("授权项目不存在");
      } else {
         return InvokeResultBuilder.success(data);
      }
   }

   @ApiOperation("根据ID列表加载授权项目")
   @HasPermission({"hr:authorization:query"})
   @PostMapping({"/load"})
   public InvokeResult<List<ShkbAuthorizationProject>> loadByIds(@RequestBody List<String> ids) {
      List<ShkbAuthorizationProject> datas = this.authorizationProjectService.findByIds(ids);
      return InvokeResultBuilder.success(datas);
   }

   @ApiOperation("创建授权项目")
   @HasPermission({"hr:authorization:create"})
   @PostMapping
   public InvokeResult<Void> create(@Valid @RequestBody CreateShkbAuthorizationProjectVo vo) {
      this.authorizationProjectService.create(vo);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("修改授权项目")
   @HasPermission({"hr:authorization:update"})
   @PutMapping
   public InvokeResult<Void> update(@Valid @RequestBody UpdateShkbAuthorizationProjectVo vo) {
      this.authorizationProjectService.update(vo);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("启用/禁用授权项目")
   @HasPermission({"hr:authorization:update"})
   @PutMapping({"/status"})
   public InvokeResult<Void> changeStatus(@NotBlank(message = "ID不能为空") @RequestParam String id, @RequestParam Integer status) {
      this.authorizationProjectService.updateStatus(id, status);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("删除授权项目")
   @ApiImplicitParam(
      value = "ID",
      name = "id",
      paramType = "path",
      required = true
   )
   @HasPermission({"hr:authorization:delete"})
   @DeleteMapping({"/{id}"})
   public InvokeResult<Void> delete(@NotBlank(message = "ID不能为空") @PathVariable String id) {
      this.authorizationProjectService.deleteById(id);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("批量删除授权项目")
   @HasPermission({"hr:authorization:delete"})
   @DeleteMapping({"/batch"})
   public InvokeResult<Void> batchDelete(@ApiParam(value = "ID",required = true) @RequestBody @NotEmpty(message = "请选择需要删除的授权项目") List<String> ids) {
      this.authorizationProjectService.deleteByIds(ids);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("导出授权项目")
   @HasPermission({"hr:authorization:query"})
   @PostMapping({"/export"})
   public void export(@Valid @RequestBody QueryShkbAuthorizationProjectVo vo) {
      ExcelMultipartWriterSheetBuilder builder = ExcelUtil.multipartExportXls("授权项目信息", AuthorizationProjectExportModel.class);

      try {
         if (vo.getIds() != null && !vo.getIds().trim().isEmpty()) {
            List<String> idList = Arrays.stream(vo.getIds().split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
            if (!idList.isEmpty()) {
               List<AuthorizationProjectExportModel> models = idList.stream()
                  .map(id -> this.authorizationProjectService.findById(id))
                  .filter(Objects::nonNull)
                  .map(AuthorizationProjectExportModel::new)
                  .collect(Collectors.toList());
               builder.doWrite(models);
               return;
            }
         }

         int pageIndex = 1;

         while (true) {
            PageResult<ShkbAuthorizationProject> pageResult = this.authorizationProjectService.query(pageIndex, com.lframework.starter.web.core.controller.ExportSizeSupport.DEFAULT_EXPORT_SIZE, vo);
            List<ShkbAuthorizationProject> datas = pageResult.getDatas();
            List<AuthorizationProjectExportModel> models = datas.stream().map(AuthorizationProjectExportModel::new).collect(Collectors.toList());
            builder.doWrite(models);
            if (!pageResult.isHasNext()) {
               return;
            }

            pageIndex++;
         }
      } finally {
         builder.finish();
      }
   }

   @ApiOperation("获取授权项目的必修课程")
   @HasPermission({"hr:authorization:query"})
   @GetMapping({"/required-courses/{projectId}"})
   public InvokeResult<List<String>> getRequiredCourses(@NotBlank(message = "授权项目ID不能为空") @PathVariable String projectId) {
      List<ShkbAuthorizationRequiredCourse> courses = this.authorizationRequiredCourseService.queryByProjectId(projectId);
      List<String> courseIds = courses.stream().map(ShkbAuthorizationRequiredCourse::getCourseId).collect(Collectors.toList());
      return InvokeResultBuilder.success(courseIds);
   }

   @ApiOperation("保存授权项目的必修课程")
   @HasPermission({"hr:authorization:update"})
   @PostMapping({"/required-courses/{projectId}"})
   public InvokeResult<Void> saveRequiredCourses(
      @NotBlank(message = "授权项目ID不能为空") @PathVariable String projectId, @ApiParam(value = "课程ID列表",required = true) @RequestBody List<String> courseIds
   ) {
      this.authorizationRequiredCourseService.saveRequiredCourses(projectId, courseIds);
      return InvokeResultBuilder.success();
   }
}
