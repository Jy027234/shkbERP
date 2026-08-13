package com.lframework.xingyun.shkb.impl.contract;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.xingyun.shkb.entity.ContractTaskNonPartFile;
import com.lframework.xingyun.shkb.service.contract.ContractTaskNonPartFileService;
import com.lframework.xingyun.shkb.mappers.ContractTaskNonPartFileMapper;
import org.springframework.stereotype.Service;

/**
* @author kison
* @description 针对表【shkb_contract_task_non_part_file(非必换件文件列表)】的数据库操作Service实现
* @createDate 2025-06-04 14:16:19
*/
@Service
public class ContractTaskNonPartFileServiceImpl extends BaseMpServiceImpl<ContractTaskNonPartFileMapper, ContractTaskNonPartFile>
    implements ContractTaskNonPartFileService {

    /**
     * 根据非必换件ID删除相关附件
     *
     * @param nonPartId 非必换件ID
     */
    @Override
    public void removeByNonPartId(String nonPartId) {
        LambdaQueryWrapper<ContractTaskNonPartFile> queryWrapper = Wrappers.lambdaQuery(ContractTaskNonPartFile.class)
                .eq(ContractTaskNonPartFile::getNonPartId, nonPartId);
        this.remove(queryWrapper);
    }
}



