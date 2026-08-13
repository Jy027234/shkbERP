package com.lframework.starter.web.core.utils;

import com.lframework.starter.common.exceptions.impl.DefaultSysException;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.web.core.bo.BasePrintDataBo;
import com.lframework.starter.web.core.dto.BaseDto;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * 打印工具类（移植自 jugg 3.1.5 web-starter 的同名实现，jugg 5 已移除）。
 * 通过 classpath 下的 FreeMarker 模板（如 print/purchase-order.ftl）渲染打印 HTML。
 */
@Slf4j
public class PrintUtil {

  private PrintUtil() {
  }

  private static Template getTemplate(String templateName) {

    Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
    configuration.setClassForTemplateLoading(PrintUtil.class, "/");
    configuration.setDefaultEncoding("UTF-8");
    configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    try {
      return configuration.getTemplate(templateName);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      throw new DefaultSysException(e.getMessage());
    }
  }

  /**
   * 渲染打印模板，返回 HTML。
   *
   * @param templateName classpath 根下的模板路径
   * @param data 打印数据 BO（转为 Map 作为模板数据模型）
   * @return 渲染后的 HTML
   */
  public static <T extends BasePrintDataBo<? extends BaseDto>> String generate(String templateName,
      T data) {

    Template template = getTemplate(templateName);

    Map<String, Object> dataModel =
        data == null ? CollectionUtil.emptyMap() : JsonUtil.convert(data, Map.class);

    StringWriter result = new StringWriter();
    try (BufferedWriter writer = new BufferedWriter(result)) {
      template.process(dataModel, writer);
      writer.flush();
      return result.toString();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new DefaultSysException(e.getMessage());
    }
  }
}
