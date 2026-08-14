package com.lframework.xingyun.shkb.impl;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.exceptions.impl.InputErrorException;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.xingyun.shkb.entity.MaterialOrderDetail;
import com.lframework.xingyun.shkb.entity.MaterialOutSheetDetail;
import com.lframework.xingyun.shkb.enums.MaterialOutSheetStatus;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 发料出库写操作中不依赖数据库的业务规则。
 *
 * <p>事务服务负责加锁和持久化，本类集中状态守卫、明细归属、数量汇总与累计进度规则，
 * 使关键不变量能够在 CI 中快速回归。</p>
 */
final class MaterialOutSheetRules {

    private MaterialOutSheetRules() {
    }

    static void requireUpdatable(Integer status) {
        if (!isPreparingOrPickable(status)) {
            throw new DefaultClientException("发料出库单当前状态不允许修改！");
        }
    }

    static void requireApprovable(Integer status) {
        if (isPreparingOrPickable(status)) {
            return;
        }
        if (MaterialOutSheetStatus.ISSUED.getCode().equals(status)) {
            throw new DefaultClientException("发料出库单已发料！");
        }
        throw new DefaultClientException("发料出库单无法发料！");
    }

    static void requirePickable(Integer status) {
        if (!MaterialOutSheetStatus.PREPARING.getCode().equals(status)) {
            throw new DefaultClientException("发料出库单无法标记为可领料！");
        }
    }

    static void requireDeletable(Integer status) {
        if (!isPreparingOrPickable(status)) {
            throw new DefaultClientException("发料出库单无法删除！");
        }
    }

    static Map<String, Integer> validateAndSumOrderDetails(String materialOrderId,
            List<MaterialOutSheetDetail> details, List<MaterialOrderDetail> lockedOrderDetails) {
        Map<String, MaterialOrderDetail> lockedDetailMap = new HashMap<>();
        for (MaterialOrderDetail orderDetail : lockedOrderDetails) {
            lockedDetailMap.put(orderDetail.getId(), orderDetail);
        }

        Map<String, Integer> orderDetailSum = new LinkedHashMap<>();
        for (MaterialOutSheetDetail detail : details) {
            if (StringUtil.isBlank(detail.getMaterialOrderDetailId())) {
                throw new InputErrorException("关联发料单的出库明细必须指定发料单明细！");
            }
            if (detail.getOutNum() == null || detail.getOutNum() <= 0) {
                throw new InputErrorException("出库数量必须大于0！");
            }

            MaterialOrderDetail orderDetail = lockedDetailMap.get(detail.getMaterialOrderDetailId());
            if (orderDetail == null
                    || !materialOrderId.equals(orderDetail.getOrderId())
                    || !Objects.equals(detail.getProductId(), orderDetail.getProductId())) {
                throw new InputErrorException("出库明细与发料单明细不匹配！");
            }
            orderDetailSum.merge(detail.getMaterialOrderDetailId(), detail.getOutNum(), Integer::sum);
        }

        for (Map.Entry<String, Integer> entry : orderDetailSum.entrySet()) {
            MaterialOrderDetail orderDetail = lockedDetailMap.get(entry.getKey());
            if (orderDetail.getOrderNum() == null || orderDetail.getOrderNum() <= 0) {
                throw new InputErrorException("发料单明细数量异常！");
            }
            int alreadyOut = orderDetail.getOutNum() == null ? 0 : orderDetail.getOutNum();
            int remaining = orderDetail.getOrderNum() - alreadyOut;
            if (entry.getValue() > remaining) {
                throw new InputErrorException("发料单明细超出可出库数量！需要出库：" + orderDetail.getOrderNum()
                        + "，已出库：" + orderDetail.getOutNum() + "，剩余：" + remaining
                        + "，本次申请：" + entry.getValue());
            }
        }
        return orderDetailSum;
    }

    static int calculateTotalOutNum(Integer currentTotalOutNum, Integer totalNum,
            Iterable<Integer> approvedQuantities) {
        if (totalNum == null || totalNum <= 0) {
            throw new InputErrorException("发料单应发数量异常，无法更新出库进度！");
        }

        if (currentTotalOutNum != null && currentTotalOutNum < 0) {
            throw new InputErrorException("发料单已出库数量异常，无法更新出库进度！");
        }

        long totalOutNum = currentTotalOutNum == null ? 0 : currentTotalOutNum;
        for (Integer quantity : approvedQuantities) {
            if (quantity == null || quantity <= 0) {
                throw new InputErrorException("出库数量必须大于0！");
            }
            totalOutNum += quantity;
        }
        if (totalOutNum > totalNum) {
            throw new InputErrorException("发料单累计出库数量超出应发数量，请刷新后重试！");
        }
        return (int) totalOutNum;
    }

    private static boolean isPreparingOrPickable(Integer status) {
        return MaterialOutSheetStatus.PREPARING.getCode().equals(status)
                || MaterialOutSheetStatus.PICKABLE.getCode().equals(status);
    }
}
