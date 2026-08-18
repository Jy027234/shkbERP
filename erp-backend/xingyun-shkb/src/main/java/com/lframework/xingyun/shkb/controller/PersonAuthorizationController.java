package com.lframework.xingyun.shkb.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.xingyun.shkb.entity.ShkbAuthorizationProject;
import com.lframework.xingyun.shkb.entity.ShkbPersonAuthorization;
import com.lframework.xingyun.shkb.entity.ShkbPersonAuthorizationFile;
import com.lframework.xingyun.shkb.entity.ShkbPersonAuthorizationProject;
import com.lframework.xingyun.shkb.service.ShkbAuthorizationProjectService;
import com.lframework.xingyun.shkb.service.ShkbPersonAuthorizationFileService;
import com.lframework.xingyun.shkb.service.ShkbPersonAuthorizationProjectService;
import com.lframework.xingyun.shkb.service.ShkbPersonAuthorizationService;
import com.lframework.xingyun.shkb.vo.authorization.CreateShkbPersonAuthorizationVo;
import com.lframework.xingyun.shkb.vo.authorization.PersonAuthorizationProjectVo;
import com.lframework.xingyun.shkb.vo.authorization.QueryShkbPersonAuthorizationVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
   tags = {"人员授权管理"}
)
@Validated
@RestController
@RequestMapping({"/shkb/person-authorization"})
public class PersonAuthorizationController extends DefaultBaseController {
   @Autowired
   private ShkbPersonAuthorizationService personAuthorizationService;
   @Autowired
   private ShkbPersonAuthorizationProjectService personAuthorizationProjectService;
   @Autowired
   private ShkbPersonAuthorizationFileService personAuthorizationFileService;
   @Autowired
   private ShkbAuthorizationProjectService authorizationProjectService;

   @Value("${jugg.upload.location}")
   private String uploadLocation;

   @ApiOperation("查询人员授权列表")
   @HasPermission({"hr:authorization:query"})
   @PostMapping({"/query"})
   public InvokeResult<PageResult<ShkbPersonAuthorization>> query(@Valid @RequestBody QueryShkbPersonAuthorizationVo vo) {
      PageResult<ShkbPersonAuthorization> pageResult = this.personAuthorizationService.query(this.getPageIndex(vo), this.getPageSize(vo), vo);
      return InvokeResultBuilder.success(pageResult);
   }

   @ApiOperation("根据员工ID查询授权列表")
   @ApiImplicitParam(
      value = "员工ID",
      name = "employeeId",
      paramType = "path",
      required = true
   )
   @HasPermission({"hr:authorization:query"})
   @GetMapping({"/employee/{employeeId}"})
   public InvokeResult<List<ShkbPersonAuthorization>> getByEmployeeId(@NotBlank(message = "员工ID不能为空") @PathVariable String employeeId) {
      List<ShkbPersonAuthorization> datas = this.personAuthorizationService.queryByEmployeeId(employeeId);
      return InvokeResultBuilder.success(datas);
   }

   @ApiOperation("查询人员授权详情")
   @ApiImplicitParam(
      value = "ID",
      name = "id",
      paramType = "path",
      required = true
   )
   @HasPermission({"hr:authorization:query"})
   @GetMapping({"/{id}"})
   public InvokeResult<ShkbPersonAuthorization> get(@NotBlank(message = "ID不能为空") @PathVariable String id) {
      ShkbPersonAuthorization data = this.personAuthorizationService.findById(id);
      if (data == null) {
         throw new DefaultClientException("人员授权不存在");
      } else {
         return InvokeResultBuilder.success(data);
      }
   }

   @ApiOperation("创建人员授权")
   @HasPermission({"hr:authorization:create"})
   @PostMapping
   public InvokeResult<String> create(
      @RequestParam("employeeId") String employeeId,
      @RequestParam(value = "description",required = false) String description,
      @RequestParam(value = "projects",required = false) String projectsJson,
      @RequestParam(value = "credentialFile",required = false) MultipartFile credentialFile
   ) {
      CreateShkbPersonAuthorizationVo vo = new CreateShkbPersonAuthorizationVo();
      vo.setEmployeeId(employeeId);
      vo.setDescription(description);
      if (StringUtil.isNotBlank(projectsJson)) {
         try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            List<PersonAuthorizationProjectVo> projects = (List<PersonAuthorizationProjectVo>)objectMapper.readValue(
               projectsJson, new TypeReference<List<PersonAuthorizationProjectVo>>() {
               }
            );
            vo.setProjects(projects);
         } catch (Exception var8) {
            throw new DefaultClientException("解析授权项目数据失败：" + var8.getMessage());
         }
      }

      String id = this.personAuthorizationService.create(vo, credentialFile);
      return InvokeResultBuilder.success(id);
   }

   @ApiOperation("修改人员授权")
   @HasPermission({"hr:authorization:update"})
   @PutMapping
   public InvokeResult<Void> update(@NotBlank(message = "ID不能为空") @RequestParam String id, @RequestParam(required = false) String description) {
      this.personAuthorizationService.update(id, description);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("修改人员授权项目")
   @HasPermission({"hr:authorization:update"})
   @PutMapping({"/projects"})
   public InvokeResult<Void> updateProjects(
      @NotBlank(message = "ID不能为空") @RequestParam String id, @Valid @RequestBody List<PersonAuthorizationProjectVo> projects
   ) {
      this.personAuthorizationService.updateProjects(id, projects);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("延期授权项目")
   @HasPermission({"hr:authorization:update"})
   @PostMapping({"/extend/{id}"})
   public InvokeResult<Void> extend(
      @NotBlank(message = "ID不能为空") @PathVariable String id,
      @RequestParam String projectId,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate expiryDate
   ) {
      this.personAuthorizationService.extend(id, projectId, expiryDate);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("吊销授权")
   @ApiImplicitParam(
      value = "ID",
      name = "id",
      paramType = "path",
      required = true
   )
   @HasPermission({"hr:authorization:update"})
   @PostMapping({"/revoke/{id}"})
   public InvokeResult<Void> revoke(@NotBlank(message = "ID不能为空") @PathVariable String id) {
      this.personAuthorizationService.revoke(id);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("删除人员授权")
   @ApiImplicitParam(
      value = "ID",
      name = "id",
      paramType = "path",
      required = true
   )
   @HasPermission({"hr:authorization:delete"})
   @DeleteMapping({"/{id}"})
   public InvokeResult<Void> delete(@NotBlank(message = "ID不能为空") @PathVariable String id) {
      this.personAuthorizationService.deleteById(id);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("批量删除人员授权")
   @HasPermission({"hr:authorization:delete"})
   @DeleteMapping({"/batch"})
   public InvokeResult<Void> batchDelete(@ApiParam(value = "ID",required = true) @RequestBody @NotEmpty(message = "请选择需要删除的人员授权") List<String> ids) {
      this.personAuthorizationService.deleteByIds(ids);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("上传人员授权附件")
   @ApiImplicitParam(
      value = "授权ID",
      name = "authorizationId",
      paramType = "query",
      required = true
   )
   @HasPermission({"hr:authorization:update"})
   @PostMapping({"/file/upload"})
   public InvokeResult<List<String>> uploadFile(
      @NotBlank(message = "授权ID不能为空！") @RequestParam("authorizationId") String authorizationId,
      @RequestParam(value = "files",required = false) List<MultipartFile> files
   ) {
      List<String> fileIds = this.personAuthorizationFileService.uploadPersonAuthorizationFiles(authorizationId, files);
      return InvokeResultBuilder.success(fileIds);
   }

   @ApiOperation("获取人员授权附件列表")
   @ApiImplicitParam(
      value = "授权ID",
      name = "authorizationId",
      paramType = "query",
      required = true
   )
   @HasPermission({"hr:authorization:query"})
   @GetMapping({"/file/list"})
   public InvokeResult<List<ShkbPersonAuthorizationFile>> getFileList(@NotBlank(message = "授权ID不能为空！") @RequestParam("authorizationId") String authorizationId) {
      List<ShkbPersonAuthorizationFile> files = this.personAuthorizationFileService.queryByAuthorizationId(authorizationId);
      return InvokeResultBuilder.success(files);
   }

   @ApiOperation("删除人员授权附件")
   @ApiImplicitParam(
      value = "附件ID",
      name = "id",
      paramType = "path",
      required = true
   )
   @HasPermission({"hr:authorization:update"})
   @DeleteMapping({"/file/{id}"})
   public InvokeResult<Void> deleteFile(@NotBlank(message = "附件ID不能为空！") @PathVariable("id") String id) {
      boolean success = this.personAuthorizationFileService.deletePersonAuthorizationFile(id);
      if (!success) {
         throw new DefaultClientException("附件不存在或删除失败！");
      } else {
         return InvokeResultBuilder.success();
      }
   }

   @ApiOperation("下载人员授权附件")
   @ApiImplicitParam(
      value = "附件ID",
      name = "id",
      paramType = "path",
      required = true
   )
    @HasPermission({"hr:authorization:query"})
    @GetMapping({"/file/download/{id}"})
    public ResponseEntity<Resource> downloadFile(@NotBlank(message = "附件ID不能为空！") @PathVariable("id") String id) throws MalformedURLException {
       ShkbPersonAuthorizationFile file = this.personAuthorizationFileService.getFileById(id);
       if (file == null || StringUtil.isBlank(file.getFileUrl())) {
          throw new DefaultClientException("附件不存在！");
       }

       String fileUrl = file.getFileUrl();
       String relativePath;
       try {
          relativePath = fileUrl.contains("://")
             ? new URI(fileUrl).getPath()
             : fileUrl;
       } catch (URISyntaxException var13) {
          throw new DefaultClientException("不支持的附件路径！");
       }
       if (relativePath.startsWith("/oss/")) {
          relativePath = relativePath.substring("/oss".length());
       } else if (relativePath.startsWith("/uploads/")) {
          relativePath = relativePath.substring("/uploads".length());
       }
       Path uploadsRoot = Path.of(this.uploadLocation).toAbsolutePath().normalize();
       Path filePath = uploadsRoot.resolve(relativePath.startsWith("/") ? relativePath.substring(1) : relativePath).normalize();
       if (!filePath.startsWith(uploadsRoot)) {
          throw new DefaultClientException("不支持的附件路径！");
       }

       Resource resource = new UrlResource(filePath.toUri());
       if (!resource.isReadable()) {
          throw new DefaultClientException("文件不存在！");
       }

       MediaType contentType = MediaType.APPLICATION_OCTET_STREAM;
       if (StringUtil.isNotBlank(file.getFileType())) {
          try {
             contentType = MediaType.parseMediaType(file.getFileType());
          } catch (IllegalArgumentException ignored) {
          }
       }
       String fileName = StringUtil.isBlank(file.getFileName()) ? "attachment" : file.getFileName();
       return ResponseEntity.ok()
          .contentType(contentType)
          .header("Content-Disposition", ContentDisposition.attachment().filename(fileName, StandardCharsets.UTF_8).build().toString())
          .body(resource);
    }

   @ApiOperation("检查授权有效性")
   @ApiImplicitParam(
      value = "授权ID",
      name = "id",
      paramType = "path",
      required = true
   )
   @HasPermission({"hr:authorization:query"})
   @GetMapping({"/check-validity/{id}"})
   public InvokeResult<Map<String, Object>> checkValidity(@NotBlank(message = "授权ID不能为空！") @PathVariable("id") String id) {
      ShkbPersonAuthorization authorization = this.personAuthorizationService.findById(id);
      if (authorization == null) {
         throw new DefaultClientException("人员授权不存在！");
      } else {
         Map<String, Object> result = new HashMap<>();
         result.put("isValid", authorization.getStatus() == 1);
         LambdaQueryWrapper<ShkbPersonAuthorizationProject> projectWrapper = (LambdaQueryWrapper<ShkbPersonAuthorizationProject>)Wrappers.lambdaQuery(
               ShkbPersonAuthorizationProject.class
            )
            .eq(ShkbPersonAuthorizationProject::getAuthorizationId, id);
         List<ShkbPersonAuthorizationProject> projects = this.personAuthorizationProjectService.list(projectWrapper);
         List<Map<String, Object>> projectValidities = new ArrayList<>();
         LocalDate now = LocalDate.now();

         for (ShkbPersonAuthorizationProject project : projects) {
            Map<String, Object> projectValidity = new HashMap<>();
            projectValidity.put("projectId", project.getProjectId());
            projectValidity.put("authorizationDate", project.getAuthorizationDate());
            projectValidity.put("expiryDate", project.getExpiryDate());
            ShkbAuthorizationProject authProject = this.authorizationProjectService.findById(project.getProjectId());
            if (authProject != null) {
               projectValidity.put("projectName", authProject.getProjectName());
            }

            if (project.getExpiryDate() != null) {
               if (now.isAfter(project.getExpiryDate())) {
                  projectValidity.put("expiryStatus", "expired");
                  projectValidity.put("daysUntilExpiry", now.toEpochDay() - project.getExpiryDate().toEpochDay());
               } else if (now.plusDays(30L).isAfter(project.getExpiryDate())) {
                  projectValidity.put("expiryStatus", "expiring");
                  projectValidity.put("daysUntilExpiry", project.getExpiryDate().toEpochDay() - now.toEpochDay());
               } else {
                  projectValidity.put("expiryStatus", "valid");
                  projectValidity.put("daysUntilExpiry", project.getExpiryDate().toEpochDay() - now.toEpochDay());
               }
            } else {
               projectValidity.put("expiryStatus", "valid");
               projectValidity.put("daysUntilExpiry", null);
            }

            projectValidities.add(projectValidity);
         }

         result.put("projects", projectValidities);
         result.put("courseCompletionStatus", "completed");
         result.put("incompleteCourses", new ArrayList());
         return InvokeResultBuilder.success(result);
      }
   }
}
