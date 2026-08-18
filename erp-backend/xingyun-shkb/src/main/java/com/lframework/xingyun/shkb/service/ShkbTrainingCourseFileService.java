package com.lframework.xingyun.shkb.service;

import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.entity.ShkbTrainingCourseFile;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ShkbTrainingCourseFileService
extends BaseMpService<ShkbTrainingCourseFile> {
    public List<ShkbTrainingCourseFile> queryByCourseId(String var1);

    public String upload(String var1, MultipartFile var2, String var3);

    public void deleteById(String var1);

    public void deleteByIds(List<String> var1);
}
