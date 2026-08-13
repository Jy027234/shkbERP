package com.lframework.xingyun.basedata.excel.product;

import com.alibaba.excel.context.AnalysisContext;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.core.components.excel.ExcelImportListener;
import com.lframework.starter.web.core.utils.ApplicationUtil;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.xingyun.basedata.entity.*;
import com.lframework.xingyun.basedata.enums.ProductType;
import com.lframework.xingyun.basedata.service.machineType.MachineTypeService;
import com.lframework.xingyun.basedata.service.product.ProductBrandService;
import com.lframework.xingyun.basedata.service.product.ProductCategoryService;
import com.lframework.xingyun.basedata.service.product.ProductPurchaseService;
import com.lframework.xingyun.basedata.service.product.ProductRetailService;
import com.lframework.xingyun.basedata.service.product.ProductSaleService;
import com.lframework.xingyun.basedata.service.product.ProductService;
import com.lframework.xingyun.basedata.vo.product.brand.CreateProductBrandVo;
import com.lframework.xingyun.basedata.vo.product.category.CreateProductCategoryVo;
import com.lframework.xingyun.basedata.vo.product.purchase.CreateProductPurchaseVo;
import com.lframework.xingyun.basedata.vo.product.retail.CreateProductRetailVo;
import com.lframework.xingyun.basedata.vo.product.sale.CreateProductSaleVo;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 航材导入监听器
 */
public class ProductCustomImportListener extends ExcelImportListener<ProductCustomImportModel> {

  private final List<String> partNoCheck = new ArrayList<>();
  private final List<String> successDetails = new ArrayList<>();
  private final List<String> failureDetails = new ArrayList<>();
  private final java.util.Set<Integer> invalidIndices = new java.util.HashSet<>();
  private int currentIndex = 0;
  // afterAllAnalysed 阶段仅筛选出的有效数据，延迟到 doComplete 再落库
  private final List<Integer> validIndices = new ArrayList<>();

  @Override
  protected void doInvoke(ProductCustomImportModel data, AnalysisContext context) {
    // 使用内部索引定位 datas 列表中的顺序，避免抛异常导致底层事务回滚
    int index = currentIndex++;
    // 基础字段校验，仅记录失败，不抛异常
    if (StringUtil.isBlank(data.getCode())) {
      failureDetails.add("第" + (index + 1) + "行“件号”不能为空");
      invalidIndices.add(index);
      return;
    }
    if (partNoCheck.contains(data.getCode())) {
      int first = partNoCheck.indexOf(data.getCode());
      failureDetails.add("第" + (index + 1) + "行“件号”与第" + (first + 1) + "行重复");
      invalidIndices.add(index);
      return;
    }
    partNoCheck.add(data.getCode());

    if (StringUtil.isBlank(data.getName())) {
      failureDetails.add("第" + (index + 1) + "行“名称”不能为空");
      invalidIndices.add(index);
      return;
    }
    if (StringUtil.isBlank(data.getCategoryName())) {
      failureDetails.add("第" + (index + 1) + "行“分类名称”不能为空");
      invalidIndices.add(index);
      return;
    }
    if (StringUtil.isBlank(data.getMachineTypeName())) {
      failureDetails.add("第" + (index + 1) + "行“机型”不能为空");
      invalidIndices.add(index);
      return;
    }
  }

  @Override
  protected void afterAllAnalysed(AnalysisContext context) {
    // 仅做轻量校验与筛选，不进行任何落库，避免触发基类事务包裹
    ProductService productService = ApplicationUtil.getBean(ProductService.class);
    List<ProductCustomImportModel> datas = this.getDatas();
    for (int i = 0; i < datas.size(); i++) {
      if (invalidIndices.contains(i)) {
        continue;
      }
      ProductCustomImportModel data = datas.get(i);
      try {
        Wrapper<Product> checkCode = Wrappers.lambdaQuery(Product.class)
            .eq(Product::getCode, data.getCode());
        if (productService.count(checkCode) > 0) {
          throw new DefaultClientException("第" + (i + 1) + "行“件号”已存在");
        }
        validIndices.add(i);
      } catch (Exception ex) {
        failureDetails.add("第" + (i + 1) + "行【" + data.getCode() + "】导入失败，原因：" + ex.getMessage());
      }
    }
  }

  private String ensureCategoryByName(ProductCategoryService categoryService, String name) {
    ProductCategory exist = categoryService.getOne(
        Wrappers.lambdaQuery(ProductCategory.class).eq(ProductCategory::getName, name));
    if (exist != null) return exist.getId();

    // 生成唯一code
    String code = genUniqueCodeForCategory(categoryService, name);
    CreateProductCategoryVo vo = new CreateProductCategoryVo();
    vo.setCode(code);
    vo.setName(name);
    vo.setDescription("");
    return categoryService.create(vo);
  }

  private String ensureBrandByName(ProductBrandService brandService, String name) {
    ProductBrand exist = brandService.getOne(
        Wrappers.lambdaQuery(ProductBrand.class).eq(ProductBrand::getName, name));
    if (exist != null) return exist.getId();

    String code = genUniqueCodeForBrand(brandService, name);
    CreateProductBrandVo vo = new CreateProductBrandVo();
    vo.setCode(code);
    vo.setName(name);
    return brandService.create(vo);
  }

  private String ensureMachineTypeByName(MachineTypeService machineTypeService, String name) {
    MachineType exist = machineTypeService.getOne(
        Wrappers.lambdaQuery(MachineType.class).eq(MachineType::getName, name));
    if (exist != null) {
      return exist.getId();
    }

    // 业务调整：机型不存在时不再自动创建，直接视为导入失败
    throw new DefaultClientException("机型不存在：" + name);
  }

  private String genUniqueCodeForCategory(ProductCategoryService service, String name) {
    String base = toCode(name);
    String code = base;
    int idx = 1;
    while (categoryServiceHasCode(service, code)) {
      code = base + (idx++);
    }
    return code;
  }

  private String genUniqueCodeForBrand(ProductBrandService service, String name) {
    String base = toCode(name);
    String code = base;
    int idx = 1;
    while (brandServiceHasCode(service, code)) {
      code = base + (idx++);
    }
    return code;
  }

  private String toCode(String name) {
    // 简单将中文或特殊字符转为可用code：大写，保留字母数字，其他转为-，去重连字符并截断到20
    if (name == null) return IdUtil.getId();
    String s = name.toUpperCase().replaceAll("[^A-Z0-9]+", "-");
    s = s.replaceAll("-+", "-");
    s = s.replaceAll("^-+", "");
    s = s.replaceAll("-+$", "");
    if (StringUtil.isBlank(s)) s = IdUtil.getId();
    if (s.length() > 20) s = s.substring(0, 20);
    return s;
  }

  private boolean categoryServiceHasCode(ProductCategoryService service, String code) {
    return service.count(Wrappers.lambdaQuery(ProductCategory.class).eq(ProductCategory::getCode, code)) > 0;
  }

  private boolean brandServiceHasCode(ProductBrandService service, String code) {
    return service.count(Wrappers.lambdaQuery(ProductBrand.class).eq(ProductBrand::getCode, code)) > 0;
  }

  // 生成唯一的 SKU 编码（内部使用），避免与数据库唯一索引冲突
  private String genUniqueSkuCode(ProductService service) {
    String sku = "SKU-" + IdUtil.getId();
    // 如存在极小概率冲突，则循环重试
    while (service.count(Wrappers.lambdaQuery(Product.class).eq(Product::getSkuCode, sku)) > 0) {
      sku = "SKU-" + IdUtil.getId();
    }
    return sku;
  }

  @Override
  protected void doComplete() {
    // 在此阶段进行实际落库，逐行 try/catch，避免影响整体流程
    ProductService productService = ApplicationUtil.getBean(ProductService.class);
    ProductCategoryService categoryService = ApplicationUtil.getBean(ProductCategoryService.class);
    ProductBrandService brandService = ApplicationUtil.getBean(ProductBrandService.class);
    MachineTypeService machineTypeService = ApplicationUtil.getBean(MachineTypeService.class);
    ProductPurchaseService productPurchaseService = ApplicationUtil.getBean(ProductPurchaseService.class);
    ProductSaleService productSaleService = ApplicationUtil.getBean(ProductSaleService.class);
    ProductRetailService productRetailService = ApplicationUtil.getBean(ProductRetailService.class);

    List<ProductCustomImportModel> datas = this.getDatas();
    for (Integer i : validIndices) {
      ProductCustomImportModel data = datas.get(i);
      try {
        String categoryId = ensureCategoryByName(categoryService, data.getCategoryName());
        String brandId = null;
        if (StringUtil.isNotBlank(data.getBrandName())) {
          brandId = ensureBrandByName(brandService, data.getBrandName());
        }
        String machineTypeId = ensureMachineTypeByName(machineTypeService, data.getMachineTypeName());

        // 解析批次号/序列号管理字段，并做二选一校验
        boolean batchFlag = false;
        boolean serialFlag = false;

        if (StringUtil.isNotBlank(data.getIsBatch())) {
          String v = data.getIsBatch().trim();
          batchFlag = "是".equals(v) || "1".equals(v) || "true".equalsIgnoreCase(v)
              || "y".equalsIgnoreCase(v) || "yes".equalsIgnoreCase(v);
        }
        if (StringUtil.isNotBlank(data.getIsSerial())) {
          String v2 = data.getIsSerial().trim();
          serialFlag = "是".equals(v2) || "1".equals(v2) || "true".equalsIgnoreCase(v2)
              || "y".equalsIgnoreCase(v2) || "yes".equalsIgnoreCase(v2);
        }

        // 业务规则：是否启用批次号管理、是否启用序列号管理必须二选一
        if ((batchFlag && serialFlag) || (!batchFlag && !serialFlag)) {
          throw new DefaultClientException("\"是否启用批次号管理\"和\"是否启用序列号管理\"必须二选一");
        }

        BigDecimal zero = BigDecimal.ZERO;
        Product record = new Product();
        record.setId(IdUtil.getId());
        record.setCode(data.getCode());
        record.setName(data.getName());
        // 业务未使用 skuCode，这里生成一个全局唯一的 skuCode 以满足数据库唯一约束
        record.setSkuCode(genUniqueSkuCode(productService));
        record.setExternalCode(null);
        record.setCategoryId(categoryId);
        if (StringUtil.isNotBlank(brandId)) {
          record.setBrandId(brandId);
        }
        record.setProductType(ProductType.NORMAL);
        record.setAvailable(Boolean.TRUE);
        // 映射批次号/序列号管理（使用上面的解析结果）
        record.setIsBatch(batchFlag);
        record.setIsSerial(serialFlag);
        record.setMachineTypeId(machineTypeId);
        // 税率字段在模板未提供，但库表非空，统一初始化为0
        record.setTaxRate(zero);
        record.setSaleTaxRate(zero);

        productService.save(record);
        data.setId(record.getId());
        CreateProductPurchaseVo createPurchase = new CreateProductPurchaseVo();
        createPurchase.setId(record.getId());
        createPurchase.setPrice(zero);
        productPurchaseService.create(createPurchase);

        CreateProductSaleVo createSale = new CreateProductSaleVo();
        createSale.setId(record.getId());
        createSale.setPrice(zero);
        productSaleService.create(createSale);

        CreateProductRetailVo createRetail = new CreateProductRetailVo();
        createRetail.setId(record.getId());
        createRetail.setPrice(zero);
        productRetailService.create(createRetail);

        this.setSuccessProcess(i);
        successDetails.add("第" + (i + 1) + "行【" + data.getCode() + "】导入成功");
      } catch (Exception e) {
        failureDetails.add("第" + (i + 1) + "行【" + data.getCode() + "】导入失败，原因：" + e.getMessage());
      }
    }
  }

  public List<String> getSuccessDetails() {
    return successDetails;
  }

  public List<String> getFailureDetails() {
    return failureDetails;
  }
}
