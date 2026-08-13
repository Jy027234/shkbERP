package com.lframework.xingyun.sc.service;

import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface StockInitImportFlowService {

  /**
   * 预检：仅校验与落入staging/record，不落库
   * 返回统一结果结构：importBatchId、total、success、failed、successDetails、failureDetails
   */
  Map<String, Object> precheck(String taskId, MultipartFile file, boolean initOnly);

  /**
   * 执行：根据 importBatchId 执行未成功项落库
   * 返回统一结果结构：importBatchId、total、success、failed、successDetails、failureDetails
   */
  Map<String, Object> execute(String importBatchId, String taskId, boolean initOnly);
}
