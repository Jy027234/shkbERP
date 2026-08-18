package com.lframework.xingyun.shkb.service;

import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.entity.ShkbAuthorizationProject;
import com.lframework.xingyun.shkb.vo.authorization.CreateShkbAuthorizationProjectVo;
import com.lframework.xingyun.shkb.vo.authorization.QueryShkbAuthorizationProjectVo;
import com.lframework.xingyun.shkb.vo.authorization.UpdateShkbAuthorizationProjectVo;
import java.util.List;

public interface ShkbAuthorizationProjectService
extends BaseMpService<ShkbAuthorizationProject> {
    public ShkbAuthorizationProject findById(String var1);

    public PageResult<ShkbAuthorizationProject> query(Integer var1, Integer var2, QueryShkbAuthorizationProjectVo var3);

    public List<ShkbAuthorizationProject> queryByStatus(Integer var1);

    public void create(CreateShkbAuthorizationProjectVo var1);

    public void update(UpdateShkbAuthorizationProjectVo var1);

    public void updateStatus(String var1, Integer var2);

    public void deleteById(String var1);

    public void deleteByIds(List<String> var1);

    public List<ShkbAuthorizationProject> findByIds(List<String> var1);
}
