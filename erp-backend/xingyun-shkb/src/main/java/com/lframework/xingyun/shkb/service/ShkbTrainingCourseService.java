package com.lframework.xingyun.shkb.service;

import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.entity.ShkbTrainingCourse;
import com.lframework.xingyun.shkb.vo.training.CreateShkbTrainingCourseVo;
import com.lframework.xingyun.shkb.vo.training.QueryShkbTrainingCourseVo;
import com.lframework.xingyun.shkb.vo.training.UpdateShkbTrainingCourseVo;
import java.util.List;

public interface ShkbTrainingCourseService
extends BaseMpService<ShkbTrainingCourse> {
    public ShkbTrainingCourse findById(String var1);

    public List<ShkbTrainingCourse> findByIds(List<String> var1);

    public PageResult<ShkbTrainingCourse> query(Integer var1, Integer var2, QueryShkbTrainingCourseVo var3);

    public List<ShkbTrainingCourse> queryByStatus(Integer var1);

    public void create(CreateShkbTrainingCourseVo var1);

    public void update(UpdateShkbTrainingCourseVo var1);

    public void updateStatus(String var1, Integer var2);

    public void deleteById(String var1);

    public void deleteByIds(List<String> var1);
}
