package com.lframework.xingyun.shkb.controller;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.starter.web.core.components.excel.ExcelMultipartWriterSheetBuilder;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.ExcelUtil;
import com.lframework.xingyun.shkb.bo.employee.GetShkbEmployeeBo;
import com.lframework.xingyun.shkb.bo.employee.QueryShkbEmployeeBo;
import com.lframework.xingyun.shkb.entity.ShkbEmployeeFile;
import com.lframework.xingyun.shkb.excel.employee.EmployeeExportModel;
import com.lframework.xingyun.shkb.service.ShkbEmployeeFileService;
import com.lframework.xingyun.shkb.service.ShkbEmployeeService;
import com.lframework.xingyun.shkb.vo.employee.BatchLeaveStatusShkbEmployeeVo;
import com.lframework.xingyun.shkb.vo.employee.CreateShkbEmployeeVo;
import com.lframework.xingyun.shkb.vo.employee.LeaveShkbEmployeeVo;
import com.lframework.xingyun.shkb.vo.employee.LeaveStatusShkbEmployeeVo;
import com.lframework.xingyun.shkb.vo.employee.QueryShkbEmployeeVo;
import com.lframework.xingyun.shkb.vo.employee.UpdateShkbEmployeeVo;
import com.lframework.xingyun.template.inner.entity.SysDept;
import com.lframework.xingyun.template.inner.service.system.SysDeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.multipart.MultipartFile;

@Api(
   tags = {"员工管理"}
)
@Validated
@RestController
@RequestMapping({"/shkb/employee"})
public class ShkbEmployeeController extends DefaultBaseController {
   @Autowired
   private ShkbEmployeeService employeeService;
   @Autowired
   private ShkbEmployeeFileService employeeFileService;
   @Autowired
   private SysDeptService sysDeptService;

   @ApiOperation("查询员工列表")
   @HasPermission({"hr:employee:query"})
   @GetMapping({"/query"})
   public InvokeResult<PageResult<QueryShkbEmployeeBo>> query(@Valid QueryShkbEmployeeVo vo) {
      PageResult<QueryShkbEmployeeBo> pageResult = this.employeeService.query(this.getPageIndex(vo), this.getPageSize(vo), vo);
      return InvokeResultBuilder.success(pageResult);
   }

   @ApiOperation("导出员工")
   @HasPermission({"hr:employee:query"})
   @PostMapping({"/export"})
   public void export(@Valid @RequestBody QueryShkbEmployeeVo vo) {
      ExcelMultipartWriterSheetBuilder builder = ExcelUtil.multipartExportXls("员工信息", EmployeeExportModel.class);

      try {
         if (vo.getIds() != null && !vo.getIds().trim().isEmpty()) {
            List<String> idList = Arrays.stream(vo.getIds().split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
            if (!idList.isEmpty()) {
               List<EmployeeExportModel> models = idList.stream()
                  .map(id -> this.employeeService.getDetail(id))
                  .filter(Objects::nonNull)
                  .map(EmployeeExportModel::new)
                  .collect(Collectors.toList());
               builder.doWrite(models);
               return;
            }
         }

         int pageIndex = 1;

         while (true) {
            PageResult<QueryShkbEmployeeBo> pageResult = this.employeeService.query(pageIndex, com.lframework.starter.web.core.controller.ExportSizeSupport.DEFAULT_EXPORT_SIZE, vo);
            List<QueryShkbEmployeeBo> datas = pageResult.getDatas();
            List<EmployeeExportModel> models = datas.stream().map(EmployeeExportModel::new).collect(Collectors.toList());
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

   @ApiOperation("查询员工详情")
   @ApiImplicitParam(
      value = "ID",
      name = "id",
      paramType = "path",
      required = true
   )
   @HasPermission({"hr:employee:query"})
   @GetMapping({"/{id}"})
   public InvokeResult<GetShkbEmployeeBo> get(@NotBlank(message = "ID不能为空") @PathVariable String id) {
      GetShkbEmployeeBo data = this.employeeService.getDetail(id);
      if (data == null) {
         throw new DefaultClientException("员工不存在");
      } else {
         return InvokeResultBuilder.success(data);
      }
   }

   @ApiOperation("创建员工")
   @HasPermission({"hr:employee:create"})
   @PostMapping
   public InvokeResult<Void> create(@Valid @RequestBody CreateShkbEmployeeVo vo) {
      this.employeeService.create(vo);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("修改员工")
   @HasPermission({"hr:employee:update"})
   @PutMapping
   public InvokeResult<Void> update(@Valid @RequestBody UpdateShkbEmployeeVo vo) {
      this.employeeService.update(vo);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("删除员工")
   @ApiImplicitParam(
      value = "ID",
      name = "id",
      paramType = "path",
      required = true
   )
   @HasPermission({"hr:employee:delete"})
   @DeleteMapping({"/{id}"})
   public InvokeResult<Void> delete(@NotBlank(message = "ID不能为空") @PathVariable String id) {
      this.employeeService.deleteById(id);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("批量删除员工")
   @HasPermission({"hr:employee:delete"})
   @DeleteMapping({"/batch"})
   public InvokeResult<Void> batchDelete(@ApiParam(value = "ID",required = true) @NotEmpty(message = "请选择需要删除的员工") @RequestParam List<String> ids) {
      this.employeeService.deleteByIds(ids);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("员工离职")
   @HasPermission({"hr:employee:update"})
   @PutMapping({"/leave"})
   public InvokeResult<Void> leave(@Valid @RequestBody LeaveShkbEmployeeVo vo) {
      this.employeeService.leave(vo);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("离职登记")
   @HasPermission({"hr:employee:update"})
   @PutMapping({"/leave-info"})
   public InvokeResult<Void> updateLeaveInfo(@Valid @RequestBody LeaveShkbEmployeeVo vo) {
      this.employeeService.updateLeaveInfo(vo);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("更新员工离职状态")
   @HasPermission({"hr:employee:update"})
   @PutMapping({"/leave-status"})
   public InvokeResult<Void> leaveStatus(@Valid @RequestBody LeaveStatusShkbEmployeeVo vo) {
      this.employeeService.leaveStatus(vo);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("批量更新员工离职状态")
   @HasPermission({"hr:employee:update"})
   @PutMapping({"/batch-leave-status"})
   public InvokeResult<Void> batchLeaveStatus(@Valid @RequestBody BatchLeaveStatusShkbEmployeeVo vo) {
      this.employeeService.batchLeaveStatus(vo);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("上传员工附件")
   @ApiImplicitParam(
      value = "员工ID",
      name = "employeeId",
      paramType = "query",
      required = true
   )
   @HasPermission({"hr:employee:update"})
   @PostMapping({"/file/upload"})
   public InvokeResult<List<String>> uploadEmployeeFiles(
      @NotBlank(message = "员工ID不能为空！") @RequestParam("employeeId") String employeeId, @RequestParam(value = "files",required = false) List<MultipartFile> files
   ) {
      List<String> fileIds = this.employeeFileService.uploadEmployeeFiles(employeeId, files);
      return InvokeResultBuilder.success(fileIds);
   }

   @ApiOperation("获取员工统计数据")
   @HasPermission({"hr:employee:query"})
   @GetMapping({"/statistics"})
   public InvokeResult<Map<String, Long>> getStatistics() {
      Map<String, Long> statistics = this.employeeService.getStatistics();
      return InvokeResultBuilder.success(statistics);
   }

   @ApiOperation("获取员工附件列表")
   @ApiImplicitParam(
      value = "员工ID",
      name = "employeeId",
      paramType = "query",
      required = true
   )
   @HasPermission({"hr:employee:query"})
   @GetMapping({"/file/list"})
   public InvokeResult<List<ShkbEmployeeFile>> getEmployeeFiles(@NotBlank(message = "员工ID不能为空！") @RequestParam("employeeId") String employeeId) {
      List<ShkbEmployeeFile> files = this.employeeFileService.queryByEmployeeId(employeeId);
      return InvokeResultBuilder.success(files);
   }

   @ApiOperation("删除员工附件")
   @ApiImplicitParam(
      value = "附件ID",
      name = "id",
      paramType = "path",
      required = true
   )
   @HasPermission({"hr:employee:update"})
   @DeleteMapping({"/file/{id}"})
   public InvokeResult<Void> deleteEmployeeFile(@NotBlank(message = "附件ID不能为空！") @PathVariable("id") String id) {
      boolean success = this.employeeFileService.deleteEmployeeFile(id);
      if (!success) {
         throw new DefaultClientException("附件不存在或删除失败！");
      } else {
         return InvokeResultBuilder.success();
      }
   }

   @ApiOperation("上传员工照片")
   @ApiImplicitParam(
      value = "员工ID",
      name = "id",
      paramType = "path",
      required = true
   )
   @HasPermission({"hr:employee:update"})
   @PostMapping({"/{id}/photo"})
   public InvokeResult<String> uploadEmployeePhoto(@NotBlank(message = "员工ID不能为空！") @PathVariable("id") String id, @RequestParam("file") MultipartFile file) {
      String photoUrl = this.employeeService.uploadEmployeePhoto(id, file);
      return InvokeResultBuilder.success(photoUrl);
   }

   @ApiOperation("获取部门列表")
   @GetMapping({"/depts"})
   public InvokeResult<List<ShkbEmployeeController.DeptSimpleVo>> getDeptList() {
      List<SysDept> depts = this.sysDeptService.selector();
      List<ShkbEmployeeController.DeptSimpleVo> result = depts.stream()
         .map(dept -> new ShkbEmployeeController.DeptSimpleVo(dept.getId(), dept.getName()))
         .collect(Collectors.toList());
      return InvokeResultBuilder.success(result);
   }

   public static class DeptSimpleVo {
      private String id;
      private String name;

      public DeptSimpleVo() {
      }

      public DeptSimpleVo(String id, String name) {
         this.id = id;
         this.name = name;
      }

      public String getId() {
         return this.id;
      }

      public void setId(String id) {
         this.id = id;
      }

      public String getName() {
         return this.name;
      }

      public void setName(String name) {
         this.name = name;
      }
   }
}
