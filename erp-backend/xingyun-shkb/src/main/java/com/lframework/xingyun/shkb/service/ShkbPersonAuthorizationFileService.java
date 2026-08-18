package com.lframework.xingyun.shkb.service;

import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.entity.ShkbPersonAuthorizationFile;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ShkbPersonAuthorizationFileService
extends BaseMpService<ShkbPersonAuthorizationFile> {
    public List<String> uploadPersonAuthorizationFiles(String var1, List<MultipartFile> var2);

    public List<ShkbPersonAuthorizationFile> queryByAuthorizationId(String var1);

    public boolean deletePersonAuthorizationFile(String var1);

    public ShkbPersonAuthorizationFile getFileById(String var1);
}


