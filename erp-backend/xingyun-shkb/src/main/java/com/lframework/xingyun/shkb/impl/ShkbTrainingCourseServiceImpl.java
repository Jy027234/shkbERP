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
import com.lframework.xingyun.shkb.entity.ShkbTrainingCourse;
import com.lframework.xingyun.shkb.mappers.ShkbTrainingCourseMapper;
import com.lframework.xingyun.shkb.service.ShkbTrainingCourseService;
import com.lframework.xingyun.shkb.vo.training.CreateShkbTrainingCourseVo;
import com.lframework.xingyun.shkb.vo.training.QueryShkbTrainingCourseVo;
import com.lframework.xingyun.shkb.vo.training.UpdateShkbTrainingCourseVo;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShkbTrainingCourseServiceImpl extends BaseMpServiceImpl<ShkbTrainingCourseMapper, ShkbTrainingCourse> implements ShkbTrainingCourseService {
   @Override
   public ShkbTrainingCourse findById(String id) {
      return (ShkbTrainingCourse)((ShkbTrainingCourseMapper)this.getBaseMapper()).selectById(id);
   }

   @Override
   public List<ShkbTrainingCourse> findByIds(List<String> ids) {
      return (List<ShkbTrainingCourse>)(ids != null && !ids.isEmpty()
         ? ((ShkbTrainingCourseMapper)this.getBaseMapper()).selectBatchIds(ids)
         : new ArrayList<>());
   }

   @Override
   public PageResult<ShkbTrainingCourse> query(Integer pageIndex, Integer pageSize, QueryShkbTrainingCourseVo vo) {
      Assert.greaterThanZero(pageIndex);
      Assert.greaterThanZero(pageSize);
      PageHelperUtil.startPage(pageIndex, pageSize);
      List<ShkbTrainingCourse> datas = ((ShkbTrainingCourseMapper)this.getBaseMapper()).selectList(this.getQueryWrapper(vo));
      return PageResultUtil.convert(new PageInfo<>(datas));
   }

   @Override
   public List<ShkbTrainingCourse> queryByStatus(Integer status) {
      LambdaQueryWrapper<ShkbTrainingCourse> wrapper = (Wrappers.lambdaQuery(
               ShkbTrainingCourse.class
            )
            .eq(ShkbTrainingCourse::getStatus, status))
         .orderByDesc(ShkbTrainingCourse::getCreateTime);
      return ((ShkbTrainingCourseMapper)this.getBaseMapper()).selectList(wrapper);
   }

   private LambdaQueryWrapper<ShkbTrainingCourse> getQueryWrapper(QueryShkbTrainingCourseVo vo) {
      LambdaQueryWrapper<ShkbTrainingCourse> wrapper = Wrappers.<ShkbTrainingCourse>lambdaQuery(ShkbTrainingCourse.class);
      if (vo != null) {
         if (StringUtil.isNotBlank(vo.getKeyword())) {
            wrapper.like(ShkbTrainingCourse::getCourseName, vo.getKeyword());
         }

         if (StringUtil.isNotBlank(vo.getCourseType())) {
            wrapper.eq(ShkbTrainingCourse::getCourseType, vo.getCourseType());
         }

         if (vo.getStatus() != null) {
            wrapper.eq(ShkbTrainingCourse::getStatus, vo.getStatus());
         }
      }

      wrapper.orderByDesc(ShkbTrainingCourse::getCreateTime);
      return wrapper;
   }

   @OpLog(
      type = 99,
      name = "新增培训课程，课程名称：{}",
      params = {"#courseName"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void create(CreateShkbTrainingCourseVo vo) {
      ShkbTrainingCourse data = new ShkbTrainingCourse();
      data.setId(IdUtil.getId());
      data.setCourseName(vo.getCourseName());
      data.setCourseType(vo.getCourseType());
      data.setImplementationInterval(vo.getImplementationInterval());
      data.setIntervalUnit(vo.getIntervalUnit());
      data.setDescription(StringUtil.isBlank(vo.getDescription()) ? "" : vo.getDescription());
      data.setInitialTrainingHours(vo.getInitialTrainingHours());
      data.setRetrainingHours(vo.getRetrainingHours());
      data.setTeachingMethod(vo.getTeachingMethod());
      data.setParticipants(vo.getParticipants());
      data.setInstructor(vo.getInstructor());
      data.setAssessmentMethod(vo.getAssessmentMethod());
      data.setTrainingOutline(vo.getTrainingOutline());
      data.setStatus(vo.getStatus());
      data.setCreateBy(SecurityUtil.getCurrentUser().getName());
      data.setUpdateBy(SecurityUtil.getCurrentUser().getName());
      ((ShkbTrainingCourseMapper)this.getBaseMapper()).insert(data);
      OpLogUtil.setVariable("courseName", vo.getCourseName());
   }

   @OpLog(
      type = 99,
      name = "修改培训课程，课程名称：{}",
      params = {"#courseName"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void update(UpdateShkbTrainingCourseVo vo) {
      ShkbTrainingCourse data = (ShkbTrainingCourse)((ShkbTrainingCourseMapper)this.getBaseMapper()).selectById(vo.getId());
      if (ObjectUtil.isNull(data)) {
         throw new DefaultClientException("课程不存在！");
      } else {
         LambdaUpdateWrapper<ShkbTrainingCourse> updateWrapper = (((((((((((((((Wrappers.lambdaUpdate(
                                                            ShkbTrainingCourse.class
                                                         )
                                                         .set(ShkbTrainingCourse::getCourseName, vo.getCourseName()))
                                                      .set(ShkbTrainingCourse::getCourseType, vo.getCourseType()))
                                                   .set(ShkbTrainingCourse::getImplementationInterval, vo.getImplementationInterval()))
                                                .set(ShkbTrainingCourse::getIntervalUnit, vo.getIntervalUnit()))
                                             .set(ShkbTrainingCourse::getDescription, StringUtil.isBlank(vo.getDescription()) ? "" : vo.getDescription()))
                                          .set(ShkbTrainingCourse::getInitialTrainingHours, vo.getInitialTrainingHours()))
                                       .set(ShkbTrainingCourse::getRetrainingHours, vo.getRetrainingHours()))
                                    .set(ShkbTrainingCourse::getTeachingMethod, vo.getTeachingMethod()))
                                 .set(ShkbTrainingCourse::getParticipants, vo.getParticipants()))
                              .set(ShkbTrainingCourse::getInstructor, vo.getInstructor()))
                           .set(ShkbTrainingCourse::getAssessmentMethod, vo.getAssessmentMethod()))
                        .set(ShkbTrainingCourse::getTrainingOutline, vo.getTrainingOutline()))
                     .set(ShkbTrainingCourse::getStatus, vo.getStatus()))
                  .set(ShkbTrainingCourse::getUpdateBy, SecurityUtil.getCurrentUser().getName()))
               .set(ShkbTrainingCourse::getUpdateTime, LocalDateTime.now()))
            .eq(ShkbTrainingCourse::getId, vo.getId());
         ((ShkbTrainingCourseMapper)this.getBaseMapper()).update(null, updateWrapper);
         OpLogUtil.setVariable("courseName", vo.getCourseName());
      }
   }

   @OpLog(
      type = 99,
      name = "修改培训课程状态，ID：{}",
      params = {"#id"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void updateStatus(String id, Integer status) {
      ShkbTrainingCourse data = (ShkbTrainingCourse)((ShkbTrainingCourseMapper)this.getBaseMapper()).selectById(id);
      if (ObjectUtil.isNull(data)) {
         throw new DefaultClientException("课程不存在！");
      } else {
         LambdaUpdateWrapper<ShkbTrainingCourse> updateWrapper = (((Wrappers.lambdaUpdate(
                        ShkbTrainingCourse.class
                     )
                     .set(ShkbTrainingCourse::getStatus, status))
                  .set(ShkbTrainingCourse::getUpdateBy, SecurityUtil.getCurrentUser().getName()))
               .set(ShkbTrainingCourse::getUpdateTime, LocalDateTime.now()))
            .eq(ShkbTrainingCourse::getId, id);
         ((ShkbTrainingCourseMapper)this.getBaseMapper()).update(null, updateWrapper);
         OpLogUtil.setVariable("id", id);
      }
   }

   @OpLog(
      type = 99,
      name = "删除培训课程，ID：{}",
      params = {"#id"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void deleteById(String id) {
      ShkbTrainingCourse data = (ShkbTrainingCourse)((ShkbTrainingCourseMapper)this.getBaseMapper()).selectById(id);
      if (ObjectUtil.isNull(data)) {
         throw new DefaultClientException("课程不存在！");
      } else {
         ((ShkbTrainingCourseMapper)this.getBaseMapper()).deleteById(id);
         OpLogUtil.setVariable("id", id);
      }
   }

   @OpLog(
      type = 99,
      name = "批量删除培训课程，数量：{}",
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
         throw new DefaultClientException("请选择要删除的课程！");
      }
   }
}
