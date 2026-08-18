package com.lframework.xingyun.shkb.service;

import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.entity.ShkbAuthorizationRequiredCourse;
import java.util.List;

public interface ShkbAuthorizationRequiredCourseService
extends BaseMpService<ShkbAuthorizationRequiredCourse> {
    public List<ShkbAuthorizationRequiredCourse> queryByProjectId(String var1);

    public void saveRequiredCourses(String var1, List<String> var2);
}
