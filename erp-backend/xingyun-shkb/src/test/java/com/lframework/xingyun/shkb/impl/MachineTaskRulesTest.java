package com.lframework.xingyun.shkb.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import org.junit.jupiter.api.Test;

class MachineTaskRulesTest {

    @Test
    void pendingReportCanProceed() {
        assertFalse(MachineTaskRules.isIdempotentReport(0, null, "{\"result\":1}"));
    }

    @Test
    void identicalCompletedReportIsAnIdempotentRetry() {
        assertTrue(MachineTaskRules.isIdempotentReport(1, "{\"result\":1}", "{\"result\":1}"));
    }

    @Test
    void completedTaskRejectsConflictingReport() {
        assertThrows(DefaultClientException.class,
                () -> MachineTaskRules.isIdempotentReport(1, "{\"result\":1}", "{\"result\":2}"));
    }

    @Test
    void unknownReportStatusIsRejected() {
        assertThrows(DefaultClientException.class,
                () -> MachineTaskRules.isIdempotentReport(null, null, "{\"result\":1}"));
        assertThrows(DefaultClientException.class,
                () -> MachineTaskRules.isIdempotentReport(9, null, "{\"result\":1}"));
    }

    @Test
    void magneticTaskCanOnlyBeSentFromPendingState() {
        assertDoesNotThrow(() -> MachineTaskRules.requireMagneticSendable(0));
        assertThrows(DefaultClientException.class, () -> MachineTaskRules.requireMagneticSendable(1));
        assertThrows(DefaultClientException.class, () -> MachineTaskRules.requireMagneticSendable(null));
    }
}
