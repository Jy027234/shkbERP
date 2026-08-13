package com.lframework.starter.web.core.bo;

import com.lframework.starter.web.core.dto.BaseDto;

/**
 * Compatibility shim for classes removed from jugg 5.x web-starter.
 */
public class BasePrintDataBo<T extends BaseDto> extends BaseBo<T> {

  public BasePrintDataBo() {
  }

  public BasePrintDataBo(T dto) {
    super(dto);
  }
}
