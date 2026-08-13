package com.lframework.starter.web.core.bo;

import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.utils.PrintUtil;
import lombok.Data;

/**
 * Compatibility shim for print base class removed from jugg 5.x web-starter.
 */
@Data
public abstract class BasePrintBo<T extends BasePrintDataBo<? extends BaseDto>> {

  /**
   * 打印方向。
   */
  private Integer orient;

  /**
   * 纸张宽。
   */
  private Integer pageWidth;

  /**
   * 纸张高。
   */
  private Integer pageHeight;

  /**
   * 纸张类型名。
   */
  private String pageName;

  /**
   * 上边距。
   */
  private Double marginTop;

  /**
   * 左边距。
   */
  private Double marginLeft;

  /**
   * 右边距。
   */
  private Double marginRight;

  /**
   * 下边距。
   */
  private Double marginBottom;

  /**
   * 生成后的 HTML。
   */
  private String html;

  public BasePrintBo() {
  }

  public BasePrintBo(String templateName) {
    this(templateName, null);
  }

  public BasePrintBo(String templateName, T data) {
    this.html = PrintUtil.generate(templateName, data);
  }
}
