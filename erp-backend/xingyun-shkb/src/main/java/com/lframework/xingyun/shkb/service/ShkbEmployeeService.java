package com.lframework.xingyun.shkb.service;

import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.bo.employee.GetShkbEmployeeBo;
import com.lframework.xingyun.shkb.bo.employee.QueryShkbEmployeeBo;
import com.lframework.xingyun.shkb.entity.ShkbEmployee;
import com.lframework.xingyun.shkb.vo.employee.BatchLeaveStatusShkbEmployeeVo;
import com.lframework.xingyun.shkb.vo.employee.CreateShkbEmployeeVo;
import com.lframework.xingyun.shkb.vo.employee.LeaveShkbEmployeeVo;
import com.lframework.xingyun.shkb.vo.employee.LeaveStatusShkbEmployeeVo;
import com.lframework.xingyun.shkb.vo.employee.QueryShkbEmployeeVo;
import com.lframework.xingyun.shkb.vo.employee.UpdateShkbEmployeeVo;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface ShkbEmployeeService
extends BaseMpService<ShkbEmployee> {
    public ShkbEmployee findById(String var1);

    public PageResult<QueryShkbEmployeeBo> query(Integer var1, Integer var2, QueryShkbEmployeeVo var3);

    public GetShkbEmployeeBo getDetail(String var1);

    public void create(CreateShkbEmployeeVo var1);

    public void update(UpdateShkbEmployeeVo var1);

    public void deleteById(String var1);

    public void deleteByIds(List<String> var1);

    public void leave(LeaveShkbEmployeeVo var1);

    public void leaveStatus(LeaveStatusShkbEmployeeVo var1);

    public void batchLeaveStatus(BatchLeaveStatusShkbEmployeeVo var1);

    public void updateLeaveInfo(LeaveShkbEmployeeVo var1);

    public Map<String, Long> getStatistics();

    public String uploadEmployeePhoto(String var1, MultipartFile var2);
}
