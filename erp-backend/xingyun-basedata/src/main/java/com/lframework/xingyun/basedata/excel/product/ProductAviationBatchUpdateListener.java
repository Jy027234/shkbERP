package com.lframework.xingyun.basedata.excel.product;

import com.alibaba.excel.context.AnalysisContext;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.core.components.excel.ExcelImportListener;
import com.lframework.starter.web.core.utils.ApplicationUtil;
import com.lframework.xingyun.basedata.entity.MachineType;
import com.lframework.xingyun.basedata.entity.Product;
import com.lframework.xingyun.basedata.service.machineType.MachineTypeService;
import com.lframework.xingyun.basedata.service.product.ProductService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 航材批量修改（仅机型）导入监听器
 */
public class ProductAviationBatchUpdateListener extends ExcelImportListener<ProductAviationBatchUpdateModel> {

  private final List<String> successDetails = new ArrayList<>();
  private final List<String> failureDetails = new ArrayList<>();
  private final Set<String> codeCheck = new HashSet<>();
  private int rowIndex = 0;

  public List<String> getSuccessDetails() {
    return successDetails;
  }

  public List<String> getFailureDetails() {
    return failureDetails;
  }

  @Override
  protected void doInvoke(ProductAviationBatchUpdateModel data, AnalysisContext context) {
    int i = rowIndex++;

    try {
      int row = i + 1;

      if (StringUtil.isBlank(data.getCode())) {
        failureDetails.add("第" + row + "行“件号”不能为空");
        return;
      }
      if (codeCheck.contains(data.getCode())) {
        failureDetails.add("第" + row + "行“件号”与前面行重复");
        return;
      }
      codeCheck.add(data.getCode());

      if (StringUtil.isBlank(data.getMachineTypeName())) {
        failureDetails.add("第" + row + "行“机型”不能为空");
        return;
      }

      ProductService productService = ApplicationUtil.getBean(ProductService.class);
      MachineTypeService machineTypeService = ApplicationUtil.getBean(MachineTypeService.class);

      Product product = productService.getOne(
          Wrappers.lambdaQuery(Product.class).eq(Product::getCode, data.getCode()));
      if (product == null) {
        failureDetails.add("第" + row + "行“件号”在系统中不存在");
        return;
      }

      // 可选修改名称：仅当填写了名称时才修改
      if (StringUtil.isNotBlank(data.getName())) {
        product.setName(data.getName());
      }

      MachineType machineType = machineTypeService.getOne(
          Wrappers.lambdaQuery(MachineType.class)
              .eq(MachineType::getName, data.getMachineTypeName())
              .eq(MachineType::getAvailable, true));
      if (machineType == null) {
        failureDetails.add("第" + row + "行机型不存在：" + data.getMachineTypeName());
        return;
      }

      // 更新机型（名称已在前面按需更新）
      product.setMachineTypeId(machineType.getId());
      productService.updateById(product);

      this.setSuccessProcess(i);
      successDetails.add("第" + row + "行【" + data.getCode() + "】机型修改成功");
    } catch (Exception ex) {
      int row = i + 1;
      failureDetails.add("第" + row + "行【" + data.getCode() + "】机型修改失败，原因：" + ex.getMessage());
    }
  }

  @Override
  protected void afterAllAnalysed(AnalysisContext context) {
    // 无需额外汇总，这里仅按行更新，不做批量落库
  }

  @Override
  protected void doComplete() {
    // nothing extra, 成功/失败信息由导入接口统一封装返回
  }
}
