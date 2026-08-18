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
import com.lframework.xingyun.core.annotations.OpLog;
import com.lframework.xingyun.core.utils.OpLogUtil;
import com.lframework.xingyun.shkb.entity.ShkbAuthorizationProject;
import com.lframework.xingyun.shkb.mappers.ShkbAuthorizationProjectMapper;
import com.lframework.xingyun.shkb.service.ShkbAuthorizationProjectService;
import com.lframework.xingyun.shkb.vo.authorization.CreateShkbAuthorizationProjectVo;
import com.lframework.xingyun.shkb.vo.authorization.QueryShkbAuthorizationProjectVo;
import com.lframework.xingyun.shkb.vo.authorization.UpdateShkbAuthorizationProjectVo;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShkbAuthorizationProjectServiceImpl
   extends BaseMpServiceImpl<ShkbAuthorizationProjectMapper, ShkbAuthorizationProject>
   implements ShkbAuthorizationProjectService {
   @Override
   public ShkbAuthorizationProject findById(String id) {
      return (ShkbAuthorizationProject)((ShkbAuthorizationProjectMapper)this.getBaseMapper()).selectById(id);
   }

   @Override
   public PageResult<ShkbAuthorizationProject> query(Integer pageIndex, Integer pageSize, QueryShkbAuthorizationProjectVo vo) {
      Assert.greaterThanZero(pageIndex);
      Assert.greaterThanZero(pageSize);
      PageHelperUtil.startPage(pageIndex, pageSize);
      List<ShkbAuthorizationProject> datas = ((ShkbAuthorizationProjectMapper)this.getBaseMapper()).selectList(this.getQueryWrapper(vo));
      return PageResultUtil.convert(new PageInfo<>(datas));
   }

   @Override
   public List<ShkbAuthorizationProject> queryByStatus(Integer status) {
      LambdaQueryWrapper<ShkbAuthorizationProject> wrapper = (Wrappers.lambdaQuery(
               ShkbAuthorizationProject.class
            )
            .eq(ShkbAuthorizationProject::getStatus, status))
         .orderByDesc(ShkbAuthorizationProject::getCreateTime);
      return ((ShkbAuthorizationProjectMapper)this.getBaseMapper()).selectList(wrapper);
   }

   private LambdaQueryWrapper<ShkbAuthorizationProject> getQueryWrapper(QueryShkbAuthorizationProjectVo vo) {
      LambdaQueryWrapper<ShkbAuthorizationProject> wrapper = Wrappers.<ShkbAuthorizationProject>lambdaQuery(ShkbAuthorizationProject.class);
      if (vo != null) {
         if (StringUtil.isNotBlank(vo.getKeyword())) {
            wrapper.like(ShkbAuthorizationProject::getProjectName, vo.getKeyword());
         }

         if (vo.getStatus() != null) {
            wrapper.eq(ShkbAuthorizationProject::getStatus, vo.getStatus());
         }
      }

      wrapper.orderByDesc(ShkbAuthorizationProject::getCreateTime);
      return wrapper;
   }

   @OpLog(
      type = 99,
      name = "新增授权项目，项目名称：{}",
      params = {"#vo.projectName"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void create(CreateShkbAuthorizationProjectVo vo) {
      ShkbAuthorizationProject data = new ShkbAuthorizationProject();
      data.setId(IdUtil.getId());
      data.setProjectName(vo.getProjectName());
      data.setAuthorizationItem(vo.getAuthorizationItem());
      data.setQualificationRequirement(vo.getQualificationRequirement());
      data.setTrainingRequirement(vo.getTrainingRequirement());
      data.setValidityPeriod(vo.getValidityPeriod());
      data.setValidityUnit(vo.getValidityUnit());
      data.setDescription(StringUtil.isBlank(vo.getDescription()) ? "" : vo.getDescription());
      data.setStatus(vo.getStatus());
      data.setCreateBy(SecurityUtil.getCurrentUser().getName());
      data.setUpdateBy(SecurityUtil.getCurrentUser().getName());
      ((ShkbAuthorizationProjectMapper)this.getBaseMapper()).insert(data);
      OpLogUtil.setVariable("projectName", vo.getProjectName());
   }

   @OpLog(
      type = 99,
      name = "修改授权项目，项目名称：{}",
      params = {"#vo.projectName"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void update(UpdateShkbAuthorizationProjectVo vo) {
      ShkbAuthorizationProject data = (ShkbAuthorizationProject)((ShkbAuthorizationProjectMapper)this.getBaseMapper()).selectById(vo.getId());
      if (ObjectUtil.isNull(data)) {
         throw new DefaultClientException("授权项目不存在！");
      } else {
         LambdaUpdateWrapper<ShkbAuthorizationProject> updateWrapper = ((((((((((Wrappers.lambdaUpdate(
                                             ShkbAuthorizationProject.class
                                          )
                                          .set(ShkbAuthorizationProject::getProjectName, vo.getProjectName()))
                                       .set(ShkbAuthorizationProject::getAuthorizationItem, vo.getAuthorizationItem()))
                                    .set(ShkbAuthorizationProject::getQualificationRequirement, vo.getQualificationRequirement()))
                                 .set(ShkbAuthorizationProject::getTrainingRequirement, vo.getTrainingRequirement()))
                              .set(ShkbAuthorizationProject::getValidityPeriod, vo.getValidityPeriod()))
                           .set(ShkbAuthorizationProject::getValidityUnit, vo.getValidityUnit()))
                        .set(ShkbAuthorizationProject::getDescription, StringUtil.isBlank(vo.getDescription()) ? "" : vo.getDescription()))
                     .set(ShkbAuthorizationProject::getStatus, vo.getStatus()))
                  .set(ShkbAuthorizationProject::getUpdateBy, SecurityUtil.getCurrentUser().getName()))
               .set(ShkbAuthorizationProject::getUpdateTime, LocalDateTime.now()))
            .eq(ShkbAuthorizationProject::getId, vo.getId());
         ((ShkbAuthorizationProjectMapper)this.getBaseMapper()).update(null, updateWrapper);
         OpLogUtil.setVariable("projectName", vo.getProjectName());
      }
   }

   @OpLog(
      type = 99,
      name = "修改授权项目状态，ID：{}",
      params = {"#id"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void updateStatus(String id, Integer status) {
      ShkbAuthorizationProject data = (ShkbAuthorizationProject)((ShkbAuthorizationProjectMapper)this.getBaseMapper()).selectById(id);
      if (ObjectUtil.isNull(data)) {
         throw new DefaultClientException("授权项目不存在！");
      } else {
         LambdaUpdateWrapper<ShkbAuthorizationProject> updateWrapper = (((Wrappers.lambdaUpdate(
                        ShkbAuthorizationProject.class
                     )
                     .set(ShkbAuthorizationProject::getStatus, status))
                  .set(ShkbAuthorizationProject::getUpdateBy, SecurityUtil.getCurrentUser().getName()))
               .set(ShkbAuthorizationProject::getUpdateTime, LocalDateTime.now()))
            .eq(ShkbAuthorizationProject::getId, id);
         ((ShkbAuthorizationProjectMapper)this.getBaseMapper()).update(null, updateWrapper);
         OpLogUtil.setVariable("id", id);
      }
   }

   @OpLog(
      type = 99,
      name = "删除授权项目，ID：{}",
      params = {"#id"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void deleteById(String id) {
      ShkbAuthorizationProject data = (ShkbAuthorizationProject)((ShkbAuthorizationProjectMapper)this.getBaseMapper()).selectById(id);
      if (ObjectUtil.isNull(data)) {
         throw new DefaultClientException("授权项目不存在！");
      } else {
         ((ShkbAuthorizationProjectMapper)this.getBaseMapper()).deleteById(id);
         OpLogUtil.setVariable("id", id);
      }
   }

   @OpLog(
      type = 99,
      name = "批量删除授权项目，数量：{}",
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
         throw new DefaultClientException("请选择要删除的授权项目！");
      }
   }

   @Override
   public List<ShkbAuthorizationProject> findByIds(List<String> ids) {
      if (ids != null && !ids.isEmpty()) {
         LambdaQueryWrapper<ShkbAuthorizationProject> wrapper = Wrappers.lambdaQuery(
               ShkbAuthorizationProject.class
            )
            .in(ShkbAuthorizationProject::getId, ids);
         return ((ShkbAuthorizationProjectMapper)this.getBaseMapper()).selectList(wrapper);
      } else {
         return new ArrayList<>();
      }
   }
}
