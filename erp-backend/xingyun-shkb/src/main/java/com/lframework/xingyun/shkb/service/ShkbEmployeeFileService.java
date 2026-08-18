package com.lframework.xingyun.shkb.service;

import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.entity.ShkbEmployeeFile;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ShkbEmployeeFileService
extends BaseMpService<ShkbEmployeeFile> {
    public List<String> uploadEmployeeFiles(String var1, List<MultipartFile> var2);

    public List<ShkbEmployeeFile> queryByEmployeeId(String var1);

    public boolean deleteEmployeeFile(String var1);

    public ShkbEmployeeFile getFileById(String var1);
}
