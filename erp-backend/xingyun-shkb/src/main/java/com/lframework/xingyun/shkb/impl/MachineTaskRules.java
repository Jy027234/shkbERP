package com.lframework.xingyun.shkb.impl;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import java.util.Objects;

/**
 * 自动化设备任务中不依赖数据库的状态与幂等规则。
 */
final class MachineTaskRules {

    private static final Integer PENDING = 0;
    private static final Integer COMPLETED = 1;

    private MachineTaskRules() {
    }

    /**
     * @return 已完成且上报内容相同时返回 true，表示这是可安全接受的设备重试。
     */
    static boolean isIdempotentReport(Integer status, String storedReportData, String incomingReportData) {
        if (PENDING.equals(status)) {
            return false;
        }
        if (COMPLETED.equals(status)) {
            if (Objects.equals(storedReportData, incomingReportData)) {
                return true;
            }
            throw new DefaultClientException("任务已完成，上报数据与原记录不一致！");
        }
        throw new DefaultClientException("任务状态异常，无法上报！");
    }

    static void requireMagneticSendable(Integer status) {
        if (PENDING.equals(status)) {
            return;
        }
        if (COMPLETED.equals(status)) {
            throw new DefaultClientException("磁粉机任务已下发！");
        }
        throw new DefaultClientException("磁粉机任务状态异常，无法下发！");
    }
}
