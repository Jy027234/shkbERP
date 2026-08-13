package com.lframework.starter.web.core.controller;

/**
 * Shared export page-size default for controllers after jugg 5 removed getExportSize().
 */
public final class ExportSizeSupport {

  /**
   * Default max rows for export queries.
   */
  public static final int DEFAULT_EXPORT_SIZE = 10000;

  private ExportSizeSupport() {
  }
}
