package com.lframework.xingyun.shkb.service;

import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.entity.ShkbTrainingImplementation;
import com.lframework.xingyun.shkb.vo.training.QueryShkbTrainingImplementationVo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ShkbTrainingImplementationService
extends BaseMpService<ShkbTrainingImplementation> {
    public ShkbTrainingImplementation findById(String var1);

    public PageResult<ShkbTrainingImplementation> query(Integer var1, Integer var2, QueryShkbTrainingImplementationVo var3);

    public List<ShkbTrainingImplementation> queryByCourseId(String var1);

    public String create(String var1, LocalDate var2, LocalDate var3, String var4, String var5, String var6);

    public void update(String var1, String var2, LocalDate var3, LocalDate var4, String var5, String var6, String var7);

    public void changeStatus(String var1, Integer var2);

    public void startWithDate(String var1, LocalDateTime var2);

    public void completeWithDate(String var1, LocalDateTime var2, String var3, String var4, String var5, Integer var6, String var7, MultipartFile var8);

    public void cancel(String var1);

    public void deleteById(String var1);

    public void deleteByIds(List<String> var1);
}
