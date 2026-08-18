package com.lframework.xingyun.shkb.impl;

import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.xingyun.shkb.entity.ShkbPersonAuthorizationProject;
import com.lframework.xingyun.shkb.mappers.ShkbPersonAuthorizationProjectMapper;
import com.lframework.xingyun.shkb.service.ShkbPersonAuthorizationProjectService;
import org.springframework.stereotype.Service;

@Service
public class ShkbPersonAuthorizationProjectServiceImpl
   extends BaseMpServiceImpl<ShkbPersonAuthorizationProjectMapper, ShkbPersonAuthorizationProject>
   implements ShkbPersonAuthorizationProjectService {
}
