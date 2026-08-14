package com.lframework.xingyun.shkb.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.exceptions.impl.InputErrorException;
import com.lframework.xingyun.shkb.entity.MaterialOrderDetail;
import com.lframework.xingyun.shkb.entity.MaterialOutSheetDetail;
import com.lframework.xingyun.shkb.enums.MaterialOutSheetStatus;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class MaterialOutSheetRulesTest {

    @Test
    void permitsMutableStatesAndRejectsIssuedOrUnknownStates() {
        assertDoesNotThrow(() -> MaterialOutSheetRules.requireUpdatable(code(MaterialOutSheetStatus.PREPARING)));
        assertDoesNotThrow(() -> MaterialOutSheetRules.requireUpdatable(code(MaterialOutSheetStatus.PICKABLE)));

        DefaultClientException issued = assertThrows(DefaultClientException.class,
                () -> MaterialOutSheetRules.requireUpdatable(code(MaterialOutSheetStatus.ISSUED)));
        assertEquals("发料出库单当前状态不允许修改！", issued.getMsg());
        assertThrows(DefaultClientException.class, () -> MaterialOutSheetRules.requireUpdatable(null));
    }

    @Test
    void keepsDuplicateApprovalMessageDistinctFromOtherInvalidStates() {
        assertDoesNotThrow(() -> MaterialOutSheetRules.requireApprovable(code(MaterialOutSheetStatus.PREPARING)));
        assertDoesNotThrow(() -> MaterialOutSheetRules.requireApprovable(code(MaterialOutSheetStatus.PICKABLE)));

        DefaultClientException issued = assertThrows(DefaultClientException.class,
                () -> MaterialOutSheetRules.requireApprovable(code(MaterialOutSheetStatus.ISSUED)));
        assertEquals("发料出库单已发料！", issued.getMsg());

        DefaultClientException unknown = assertThrows(DefaultClientException.class,
                () -> MaterialOutSheetRules.requireApprovable(99));
        assertEquals("发料出库单无法发料！", unknown.getMsg());
    }

    @Test
    void onlyPreparingSheetCanBecomePickable() {
        assertDoesNotThrow(() -> MaterialOutSheetRules.requirePickable(code(MaterialOutSheetStatus.PREPARING)));

        DefaultClientException exception = assertThrows(DefaultClientException.class,
                () -> MaterialOutSheetRules.requirePickable(code(MaterialOutSheetStatus.PICKABLE)));
        assertEquals("发料出库单无法标记为可领料！", exception.getMsg());
    }

    @Test
    void onlyUnissuedSheetCanBeDeleted() {
        assertDoesNotThrow(() -> MaterialOutSheetRules.requireDeletable(code(MaterialOutSheetStatus.PREPARING)));
        assertDoesNotThrow(() -> MaterialOutSheetRules.requireDeletable(code(MaterialOutSheetStatus.PICKABLE)));

        DefaultClientException exception = assertThrows(DefaultClientException.class,
                () -> MaterialOutSheetRules.requireDeletable(code(MaterialOutSheetStatus.ISSUED)));
        assertEquals("发料出库单无法删除！", exception.getMsg());
    }

    @Test
    void aggregatesMultipleLinesAgainstTheSameOrderDetailAndAllowsExactRemainingQuantity() {
        MaterialOrderDetail orderDetail = orderDetail("detail-1", "order-1", "product-1", 10, 3);
        List<MaterialOutSheetDetail> details = List.of(
                outDetail("detail-1", "product-1", 3),
                outDetail("detail-1", "product-1", 4));

        Map<String, Integer> result = MaterialOutSheetRules.validateAndSumOrderDetails("order-1", details,
                List.of(orderDetail));

        assertEquals(Map.of("detail-1", 7), result);
    }

    @Test
    void rejectsCombinedLinesThatExceedRemainingQuantity() {
        MaterialOrderDetail orderDetail = orderDetail("detail-1", "order-1", "product-1", 10, 3);
        List<MaterialOutSheetDetail> details = List.of(
                outDetail("detail-1", "product-1", 4),
                outDetail("detail-1", "product-1", 4));

        InputErrorException exception = assertThrows(InputErrorException.class,
                () -> MaterialOutSheetRules.validateAndSumOrderDetails("order-1", details,
                        List.of(orderDetail)));

        assertEquals("发料单明细超出可出库数量！需要出库：10，已出库：3，剩余：7，本次申请：8",
                exception.getMsg());
    }

    @Test
    void rejectsMissingForeignOrProductMismatchedOrderDetails() {
        MaterialOrderDetail orderDetail = orderDetail("detail-1", "order-1", "product-1", 10, 0);

        InputErrorException missing = assertThrows(InputErrorException.class,
                () -> MaterialOutSheetRules.validateAndSumOrderDetails("order-1",
                        List.of(outDetail(null, "product-1", 1)), List.of(orderDetail)));
        assertEquals("关联发料单的出库明细必须指定发料单明细！", missing.getMsg());

        InputErrorException foreign = assertThrows(InputErrorException.class,
                () -> MaterialOutSheetRules.validateAndSumOrderDetails("order-2",
                        List.of(outDetail("detail-1", "product-1", 1)), List.of(orderDetail)));
        assertEquals("出库明细与发料单明细不匹配！", foreign.getMsg());

        InputErrorException wrongProduct = assertThrows(InputErrorException.class,
                () -> MaterialOutSheetRules.validateAndSumOrderDetails("order-1",
                        List.of(outDetail("detail-1", "product-2", 1)), List.of(orderDetail)));
        assertEquals("出库明细与发料单明细不匹配！", wrongProduct.getMsg());
    }

    @Test
    void rejectsNonPositiveWriteQuantities() {
        MaterialOrderDetail orderDetail = orderDetail("detail-1", "order-1", "product-1", 10, 0);

        InputErrorException exception = assertThrows(InputErrorException.class,
                () -> MaterialOutSheetRules.validateAndSumOrderDetails("order-1",
                        List.of(outDetail("detail-1", "product-1", 0)), List.of(orderDetail)));

        assertEquals("出库数量必须大于0！", exception.getMsg());
    }

    @Test
    void calculatesPartialAndCompletedOrderProgress() {
        assertEquals(2, MaterialOutSheetRules.calculateTotalOutNum(null, 5, List.of(2)));
        assertEquals(5, MaterialOutSheetRules.calculateTotalOutNum(2, 5, List.of(1, 2)));
    }

    @Test
    void rejectsInvalidOrOverflowingOrderProgress() {
        InputErrorException invalidTotal = assertThrows(InputErrorException.class,
                () -> MaterialOutSheetRules.calculateTotalOutNum(0, 0, List.of(1)));
        assertEquals("发料单应发数量异常，无法更新出库进度！", invalidTotal.getMsg());

        InputErrorException invalidCurrent = assertThrows(InputErrorException.class,
                () -> MaterialOutSheetRules.calculateTotalOutNum(-1, 5, List.of(1)));
        assertEquals("发料单已出库数量异常，无法更新出库进度！", invalidCurrent.getMsg());

        InputErrorException overflow = assertThrows(InputErrorException.class,
                () -> MaterialOutSheetRules.calculateTotalOutNum(4, 5, List.of(2)));
        assertEquals("发料单累计出库数量超出应发数量，请刷新后重试！", overflow.getMsg());
    }

    private static Integer code(MaterialOutSheetStatus status) {
        return status.getCode();
    }

    private static MaterialOutSheetDetail outDetail(String orderDetailId, String productId, Integer outNum) {
        MaterialOutSheetDetail detail = new MaterialOutSheetDetail();
        detail.setMaterialOrderDetailId(orderDetailId);
        detail.setProductId(productId);
        detail.setOutNum(outNum);
        return detail;
    }

    private static MaterialOrderDetail orderDetail(String id, String orderId, String productId, Integer orderNum,
            Integer outNum) {
        MaterialOrderDetail detail = new MaterialOrderDetail();
        detail.setId(id);
        detail.setOrderId(orderId);
        detail.setProductId(productId);
        detail.setOrderNum(orderNum);
        detail.setOutNum(outNum);
        return detail;
    }
}
