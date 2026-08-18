package com.lframework.xingyun.shkb.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.xingyun.shkb.entity.ShkbAuthorizationRequiredCourse;
import com.lframework.xingyun.shkb.mappers.ShkbAuthorizationRequiredCourseMapper;
import com.lframework.xingyun.shkb.service.ShkbAuthorizationRequiredCourseService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShkbAuthorizationRequiredCourseServiceImpl
   extends BaseMpServiceImpl<ShkbAuthorizationRequiredCourseMapper, ShkbAuthorizationRequiredCourse>
   implements ShkbAuthorizationRequiredCourseService {
   @Override
   public List<ShkbAuthorizationRequiredCourse> queryByProjectId(String projectId) {
      LambdaQueryWrapper<ShkbAuthorizationRequiredCourse> queryWrapper = new LambdaQueryWrapper<>();
      queryWrapper.eq(ShkbAuthorizationRequiredCourse::getProjectId, projectId);
      queryWrapper.eq(ShkbAuthorizationRequiredCourse::getIsRequired, 1);
      queryWrapper.eq(ShkbAuthorizationRequiredCourse::getDeleted, 0);
      return this.list(queryWrapper);
   }

   @Transactional
   @Override
   public void saveRequiredCourses(String projectId, List<String> courseIds) {
      LambdaQueryWrapper<ShkbAuthorizationRequiredCourse> deleteWrapper = new LambdaQueryWrapper<>();
      deleteWrapper.eq(ShkbAuthorizationRequiredCourse::getProjectId, projectId);
      deleteWrapper.eq(ShkbAuthorizationRequiredCourse::getIsRequired, 1);
      this.remove(deleteWrapper);

      for (String courseId : courseIds) {
         ShkbAuthorizationRequiredCourse course = new ShkbAuthorizationRequiredCourse();
         course.setId(IdUtil.getId());
         course.setProjectId(projectId);
         course.setCourseId(courseId);
         course.setIsRequired(1);
         this.save(course);
      }
   }
}
