package com.lframework.xingyun.core.components;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lframework.starter.common.exceptions.impl.AccessDeniedException;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.exceptions.impl.DefaultSysException;
import com.lframework.starter.common.exceptions.impl.InputErrorException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class WebExceptionHandlerTest {

  @Test
  void mapsValidationFailureToBadRequest() {
    assertEquals(HttpStatus.BAD_REQUEST,
        WebExceptionHandler.resolveHttpStatus(new InputErrorException("ID不能为空！")));
  }

  @Test
  void mapsBusinessRejectionToConflictDespiteLegacyBodyCode() {
    assertEquals(HttpStatus.CONFLICT,
        WebExceptionHandler.resolveHttpStatus(new DefaultClientException("库存不足！")));
  }

  @Test
  void mapsAccessDeniedToForbidden() {
    assertEquals(HttpStatus.FORBIDDEN,
        WebExceptionHandler.resolveHttpStatus(new AccessDeniedException()));
  }

  @Test
  void keepsUnexpectedSystemFailureAsInternalServerError() {
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
        WebExceptionHandler.resolveHttpStatus(new DefaultSysException()));
  }
}
