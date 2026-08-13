package com.lframework.xingyun.shkb.service.contract;

import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.entity.ContractTaskNonPartFile;

/**
* @author kison
* @description 针对表【shkb_contract_task_non_part_file(非必换件文件列表)】的数据库操作Service
* @createDate 2025-06-04 14:16:19
*/
public interface ContractTaskNonPartFileService extends BaseMpService<ContractTaskNonPartFile> {

    /**
     * 根据非必换件ID删除相关附件
     *
     * @param nonPartId 非必换件ID
     */
    void removeByNonPartId(String nonPartId);
}
