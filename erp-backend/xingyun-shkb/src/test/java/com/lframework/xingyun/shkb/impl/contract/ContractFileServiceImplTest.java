package com.lframework.xingyun.shkb.impl.contract;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.xingyun.shkb.entity.Contract;
import com.lframework.xingyun.shkb.mappers.ContractMapper;
import java.util.List;
import org.junit.jupiter.api.Test;

class ContractFileServiceImplTest {

    @Test
    void rejectsAttachmentForMissingContractBeforeWritingAFile() {
        ContractMapper contractMapper = mock(ContractMapper.class);
        when(contractMapper.selectById("missing")).thenReturn(null);
        ContractFileServiceImpl service = new ContractFileServiceImpl(contractMapper);

        DefaultClientException exception = assertThrows(DefaultClientException.class,
            () -> service.uploadContractFiles("missing", List.of()));

        assertEquals("合同不存在！", exception.getMessage());
    }

    @Test
    void acceptsEmptyAttachmentListForExistingContract() {
        ContractMapper contractMapper = mock(ContractMapper.class);
        when(contractMapper.selectById("contract-1")).thenReturn(new Contract());
        ContractFileServiceImpl service = new ContractFileServiceImpl(contractMapper);

        assertEquals(List.of(), service.uploadContractFiles("contract-1", List.of()));
    }
}
