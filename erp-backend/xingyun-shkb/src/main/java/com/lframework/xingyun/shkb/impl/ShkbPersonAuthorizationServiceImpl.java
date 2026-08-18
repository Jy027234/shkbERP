package com.lframework.xingyun.shkb.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.Assert;
import com.lframework.starter.common.utils.ObjectUtil;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.core.components.security.SecurityUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.starter.web.core.utils.PageHelperUtil;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.starter.web.core.utils.UploadUtil;
import com.lframework.xingyun.core.annotations.OpLog;
import com.lframework.xingyun.core.utils.OpLogUtil;
import com.lframework.xingyun.shkb.entity.ShkbAuthorizationProject;
import com.lframework.xingyun.shkb.entity.ShkbEmployee;
import com.lframework.xingyun.shkb.entity.ShkbPersonAuthorization;
import com.lframework.xingyun.shkb.entity.ShkbPersonAuthorizationProject;
import com.lframework.xingyun.shkb.mappers.ShkbPersonAuthorizationMapper;
import com.lframework.xingyun.shkb.mappers.ShkbPersonAuthorizationProjectMapper;
import com.lframework.xingyun.shkb.service.ShkbAuthorizationProjectService;
import com.lframework.xingyun.shkb.service.ShkbEmployeeService;
import com.lframework.xingyun.shkb.service.ShkbPersonAuthorizationProjectService;
import com.lframework.xingyun.shkb.service.ShkbPersonAuthorizationService;
import com.lframework.xingyun.shkb.vo.authorization.CreateShkbPersonAuthorizationVo;
import com.lframework.xingyun.shkb.vo.authorization.PersonAuthorizationProjectVo;
import com.lframework.xingyun.shkb.vo.authorization.QueryShkbPersonAuthorizationVo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ShkbPersonAuthorizationServiceImpl
   extends BaseMpServiceImpl<ShkbPersonAuthorizationMapper, ShkbPersonAuthorization>
   implements ShkbPersonAuthorizationService {
   @Autowired
   private ShkbPersonAuthorizationProjectService personAuthorizationProjectService;
   @Autowired
   private ShkbEmployeeService employeeService;
   @Autowired
   private ShkbAuthorizationProjectService authorizationProjectService;

   @Override
   public ShkbPersonAuthorization findById(String id) {
      ShkbPersonAuthorization authorization = (ShkbPersonAuthorization)((ShkbPersonAuthorizationMapper)this.getBaseMapper()).selectById(id);
      if (authorization != null) {
         ShkbEmployee employee = this.employeeService.findById(authorization.getEmployeeId());
         if (employee != null) {
            authorization.setEmployeeName(employee.getName());
            authorization.setEmployeeCode(employee.getCode());
         }

         LambdaQueryWrapper<ShkbPersonAuthorizationProject> projectWrapper = Wrappers.lambdaQuery(
               ShkbPersonAuthorizationProject.class
            )
            .eq(ShkbPersonAuthorizationProject::getAuthorizationId, authorization.getId());
         List<ShkbPersonAuthorizationProject> projectRelations = this.personAuthorizationProjectService.list(projectWrapper);
         if (!projectRelations.isEmpty()) {
            for (ShkbPersonAuthorizationProject relation : projectRelations) {
               ShkbAuthorizationProject project = this.authorizationProjectService.findById(relation.getProjectId());
               if (project != null) {
                  relation.setProjectName(project.getProjectName());
               }
            }
         }

         authorization.setProjectRelations(projectRelations);
      }

      return authorization;
   }

   @Override
   public PageResult<ShkbPersonAuthorization> query(Integer pageIndex, Integer pageSize, QueryShkbPersonAuthorizationVo vo) {
      Assert.greaterThanZero(pageIndex);
      Assert.greaterThanZero(pageSize);
      PageHelperUtil.startPage(pageIndex, pageSize);
      List<ShkbPersonAuthorization> datas = ((ShkbPersonAuthorizationMapper)this.getBaseMapper()).queryByCondition(vo);

      for (ShkbPersonAuthorization authorization : datas) {
         ShkbEmployee employee = this.employeeService.findById(authorization.getEmployeeId());
         if (employee != null) {
            authorization.setEmployeeName(employee.getName());
            authorization.setEmployeeCode(employee.getCode());
         }

         LambdaQueryWrapper<ShkbPersonAuthorizationProject> projectWrapper = Wrappers.lambdaQuery(
               ShkbPersonAuthorizationProject.class
            )
            .eq(ShkbPersonAuthorizationProject::getAuthorizationId, authorization.getId());
         List<ShkbPersonAuthorizationProject> projectRelations = this.personAuthorizationProjectService.list(projectWrapper);
         if (!projectRelations.isEmpty()) {
            List<ShkbAuthorizationProject> projects = new ArrayList<>();

            for (ShkbPersonAuthorizationProject relation : projectRelations) {
               ShkbAuthorizationProject project = this.authorizationProjectService.findById(relation.getProjectId());
               if (project != null) {
                  projects.add(project);
               }
            }

            authorization.setProjects(projects);
         }
      }

      return PageResultUtil.convert(new PageInfo<>(datas));
   }

   @Override
   public List<ShkbPersonAuthorization> queryByEmployeeId(String employeeId) {
      LambdaQueryWrapper<ShkbPersonAuthorization> wrapper = (Wrappers.lambdaQuery(
               ShkbPersonAuthorization.class
            )
            .eq(ShkbPersonAuthorization::getEmployeeId, employeeId))
         .orderByDesc(ShkbPersonAuthorization::getCreateTime);
      return ((ShkbPersonAuthorizationMapper)this.getBaseMapper()).selectList(wrapper);
   }

   @OpLog(
      type = 99,
      name = "新增人员授权"
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public String create(CreateShkbPersonAuthorizationVo vo, MultipartFile credentialFile) {
      ShkbPersonAuthorization data = new ShkbPersonAuthorization();
      String id = IdUtil.getId();
      data.setId(id);
      data.setEmployeeId(vo.getEmployeeId());
      data.setDescription(StringUtil.isBlank(vo.getDescription()) ? "" : vo.getDescription());
      data.setStatus(1);
      data.setCreateBy(SecurityUtil.getCurrentUser().getName());
      data.setUpdateBy(SecurityUtil.getCurrentUser().getName());
      if (credentialFile != null && !credentialFile.isEmpty()) {
          String fileUrl = UploadUtil.upload(credentialFile).getUrl();
         data.setCredentialFileUrl(fileUrl);
         data.setCredentialFileName(credentialFile.getOriginalFilename());
      }

      ((ShkbPersonAuthorizationMapper)this.getBaseMapper()).insert(data);
      if (vo.getProjects() != null && !vo.getProjects().isEmpty()) {
         List<ShkbPersonAuthorizationProject> projects = new ArrayList<>();

         for (PersonAuthorizationProjectVo projectVo : vo.getProjects()) {
            ShkbPersonAuthorizationProject project = new ShkbPersonAuthorizationProject();
            project.setId(IdUtil.getId());
            project.setAuthorizationId(id);
            project.setProjectId(projectVo.getProjectId());
            project.setAuthorizationDate(projectVo.getAuthorizationDate());
            project.setExpiryDate(projectVo.getExpiryDate());
            project.setStatus(1);
            project.setCreateBy(SecurityUtil.getCurrentUser().getName());
            project.setUpdateBy(SecurityUtil.getCurrentUser().getName());
            projects.add(project);
         }

         this.personAuthorizationProjectService.saveBatch(projects);
      }

      return id;
   }

   @OpLog(
      type = 99,
      name = "修改人员授权，ID：{}",
      params = {"#id"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void update(String id, String description) {
      ShkbPersonAuthorization data = (ShkbPersonAuthorization)((ShkbPersonAuthorizationMapper)this.getBaseMapper()).selectById(id);
      if (ObjectUtil.isNull(data)) {
         throw new DefaultClientException("人员授权不存在！");
      } else {
         LambdaUpdateWrapper<ShkbPersonAuthorization> updateWrapper = (((Wrappers.lambdaUpdate(
                        ShkbPersonAuthorization.class
                     )
                     .set(ShkbPersonAuthorization::getDescription, StringUtil.isBlank(description) ? "" : description))
                  .set(ShkbPersonAuthorization::getUpdateBy, SecurityUtil.getCurrentUser().getName()))
               .set(ShkbPersonAuthorization::getUpdateTime, LocalDateTime.now()))
            .eq(ShkbPersonAuthorization::getId, id);
         ((ShkbPersonAuthorizationMapper)this.getBaseMapper()).update(null, updateWrapper);
         OpLogUtil.setVariable("id", id);
      }
   }

   @OpLog(
      type = 99,
      name = "修改人员授权项目，ID：{}",
      params = {"#id"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void updateProjects(String id, List<PersonAuthorizationProjectVo> projects) {
      ShkbPersonAuthorization data = (ShkbPersonAuthorization)((ShkbPersonAuthorizationMapper)this.getBaseMapper()).selectById(id);
      if (ObjectUtil.isNull(data)) {
         throw new DefaultClientException("人员授权不存在！");
      } else {
         ((ShkbPersonAuthorizationProjectMapper)this.personAuthorizationProjectService.getBaseMapper()).physicallyDeleteByAuthorizationId(id);
         if (projects != null && !projects.isEmpty()) {
            List<ShkbPersonAuthorizationProject> newProjects = new ArrayList<>();

            for (PersonAuthorizationProjectVo projectVo : projects) {
               ShkbPersonAuthorizationProject project = new ShkbPersonAuthorizationProject();
               project.setId(IdUtil.getId());
               project.setAuthorizationId(id);
               project.setProjectId(projectVo.getProjectId());
               project.setAuthorizationDate(projectVo.getAuthorizationDate());
               project.setExpiryDate(projectVo.getExpiryDate());
               project.setStatus(1);
               project.setCreateBy(SecurityUtil.getCurrentUser().getName());
               project.setUpdateBy(SecurityUtil.getCurrentUser().getName());
               newProjects.add(project);
            }

            this.personAuthorizationProjectService.saveBatch(newProjects);
         }

         OpLogUtil.setVariable("id", id);
      }
   }

   @OpLog(
      type = 99,
      name = "延期授权项目，ID：{}",
      params = {"#id"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void extend(String id, String projectId, LocalDate expiryDate) {
      ShkbPersonAuthorization data = (ShkbPersonAuthorization)((ShkbPersonAuthorizationMapper)this.getBaseMapper()).selectById(id);
      if (ObjectUtil.isNull(data)) {
         throw new DefaultClientException("人员授权不存在！");
      } else {
         LambdaQueryWrapper<ShkbPersonAuthorizationProject> queryWrapper = (Wrappers.lambdaQuery(
                  ShkbPersonAuthorizationProject.class
               )
               .eq(ShkbPersonAuthorizationProject::getAuthorizationId, id))
            .eq(ShkbPersonAuthorizationProject::getProjectId, projectId);
         ShkbPersonAuthorizationProject project = (ShkbPersonAuthorizationProject)this.personAuthorizationProjectService.getOne(queryWrapper);
         if (ObjectUtil.isNull(project)) {
            throw new DefaultClientException("授权项目不存在！");
         } else if (project.getStatus() != 1) {
            throw new DefaultClientException("只有正常状态的授权项目才能延期！");
         } else {
            LambdaUpdateWrapper<ShkbPersonAuthorizationProject> updateWrapper = (((Wrappers.lambdaUpdate(
                           ShkbPersonAuthorizationProject.class
                        )
                        .set(ShkbPersonAuthorizationProject::getExpiryDate, expiryDate))
                     .set(ShkbPersonAuthorizationProject::getUpdateBy, SecurityUtil.getCurrentUser().getName()))
                  .set(ShkbPersonAuthorizationProject::getUpdateTime, LocalDateTime.now()))
               .eq(ShkbPersonAuthorizationProject::getId, project.getId());
            this.personAuthorizationProjectService.update(null, updateWrapper);
            OpLogUtil.setVariable("id", id);
         }
      }
   }

   @OpLog(
      type = 99,
      name = "吊销授权，ID：{}",
      params = {"#id"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void revoke(String id) {
      ShkbPersonAuthorization data = (ShkbPersonAuthorization)((ShkbPersonAuthorizationMapper)this.getBaseMapper()).selectById(id);
      if (ObjectUtil.isNull(data)) {
         throw new DefaultClientException("人员授权不存在！");
      } else if (data.getStatus() != 1) {
         throw new DefaultClientException("只有正常状态才能吊销！");
      } else {
         LambdaUpdateWrapper<ShkbPersonAuthorization> updateWrapper = (((Wrappers.lambdaUpdate(
                        ShkbPersonAuthorization.class
                     )
                     .set(ShkbPersonAuthorization::getStatus, 0))
                  .set(ShkbPersonAuthorization::getUpdateBy, SecurityUtil.getCurrentUser().getName()))
               .set(ShkbPersonAuthorization::getUpdateTime, LocalDateTime.now()))
            .eq(ShkbPersonAuthorization::getId, id);
         ((ShkbPersonAuthorizationMapper)this.getBaseMapper()).update(null, updateWrapper);
         OpLogUtil.setVariable("id", id);
      }
   }

   @OpLog(
      type = 99,
      name = "删除人员授权，ID：{}",
      params = {"#id"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void deleteById(String id) {
      ShkbPersonAuthorization data = (ShkbPersonAuthorization)((ShkbPersonAuthorizationMapper)this.getBaseMapper()).selectById(id);
      if (ObjectUtil.isNull(data)) {
         throw new DefaultClientException("人员授权不存在！");
      } else {
         ((ShkbPersonAuthorizationMapper)this.getBaseMapper()).deleteById(id);
         OpLogUtil.setVariable("id", id);
      }
   }

   @OpLog(
      type = 99,
      name = "批量删除人员授权，数量：{}",
      params = {"#ids.size()"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void deleteByIds(List<String> ids) {
      if (ids != null && !ids.isEmpty()) {
         for (String id : ids) {
            this.deleteById(id);
         }
      } else {
         throw new DefaultClientException("请选择要删除的人员授权！");
      }
   }
}
