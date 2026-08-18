package com.lframework.xingyun.shkb.service;

import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.bo.training.GetShkbEmployeeTrainingBo;
import com.lframework.xingyun.shkb.bo.training.QueryShkbEmployeeTrainingBo;
import com.lframework.xingyun.shkb.bo.training.TrainingStatisticsBo;
import com.lframework.xingyun.shkb.entity.ShkbEmployeeTraining;
import com.lframework.xingyun.shkb.vo.employee.CreateShkbEmployeeTrainingVo;
import com.lframework.xingyun.shkb.vo.employee.QueryShkbEmployeeTrainingVo;
import com.lframework.xingyun.shkb.vo.employee.UpdateShkbEmployeeTrainingVo;
import java.util.List;

public interface ShkbEmployeeTrainingService
extends BaseMpService<ShkbEmployeeTraining> {
    public ShkbEmployeeTraining findById(String var1);

    public GetShkbEmployeeTrainingBo getById(String var1);

    public PageResult<QueryShkbEmployeeTrainingBo> query(Integer var1, Integer var2, QueryShkbEmployeeTrainingVo var3);

    public List<ShkbEmployeeTraining> queryByEmployeeId(String var1);

    public void create(CreateShkbEmployeeTrainingVo var1);

    public void update(UpdateShkbEmployeeTrainingVo var1);

    public void deleteById(String var1);

    public void deleteByIds(List<String> var1);

    public TrainingStatisticsBo getStatistics();
}
