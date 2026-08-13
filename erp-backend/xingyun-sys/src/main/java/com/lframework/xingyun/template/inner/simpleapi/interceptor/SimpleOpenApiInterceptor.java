package com.lframework.xingyun.template.inner.simpleapi.interceptor;

import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.core.components.tenant.TenantContextHolder;
import com.lframework.starter.web.core.utils.TenantUtil;
import com.lframework.xingyun.template.inner.simpleapi.annotation.SimpleOpenApi;
import com.lframework.xingyun.template.inner.simpleapi.config.SimpleOpenApiProperties;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 简单开放接口拦截器：当 Handler 或其类标注了 @SimpleOpenApi 时，
 * 使用 X-Simple-Auth = MD5(secret + yyyyMMddHHmm) 进行校验，容忍 ±tolerance 分钟。
 */
public class SimpleOpenApiInterceptor implements HandlerInterceptor {

  private static final Logger log = LoggerFactory.getLogger(SimpleOpenApiInterceptor.class);

  private static final DateTimeFormatter MINUTE_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

  private final SimpleOpenApiProperties props;

  public SimpleOpenApiInterceptor(SimpleOpenApiProperties props) {
    this.props = Objects.requireNonNull(props);
  }

  @Override
  public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler)
      throws Exception {
    if (!props.isEnabled()) {
      return true;
    }

    if (!(handler instanceof HandlerMethod)) {
      return true;
    }
    HandlerMethod hm = (HandlerMethod) handler;

    // 是否被 @SimpleOpenApi 标注（方法或类）
    Method method = hm.getMethod();
    boolean annotated = method.isAnnotationPresent(SimpleOpenApi.class)
        || hm.getBeanType().isAnnotationPresent(SimpleOpenApi.class);
    if (!annotated) {
      return true;
    }

    String headerName = props.getHeaderName();
    String simpleAuth = request.getHeader(headerName);

    if (StringUtil.isBlank(simpleAuth)) {
      log.warn("SimpleOpenApi auth header [{}] is missing", headerName);
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json;charset=UTF-8");
      response.getWriter().write("{\"code\":401,\"msg\":\"Unauthorized: missing simple auth\"}");
      return false;
    }

    String secret = props.getSecret();
    if (StringUtil.isBlank(secret) || "change-me".equals(secret)) {
      log.error("SimpleOpenApi secret is not configured. Please set simple-open-api.secret");
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      response.setContentType("application/json;charset=UTF-8");
      response.getWriter().write("{\"code\":500,\"msg\":\"Server simple-open-api.secret not configured\"}");
      return false;
    }

    int tol = Math.max(0, props.getToleranceMinutes());

    LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
    boolean pass = false;
    for (int offset = -tol; offset <= tol; offset++) {
      LocalDateTime slot = now.plusMinutes(offset);
      String base = secret + MINUTE_FMT.format(slot);
      String md5 = md5Hex(base);
      if (md5.equalsIgnoreCase(simpleAuth)) {
        pass = true;
        break;
      }
    }

    if (!pass) {
      log.warn("SimpleOpenApi auth failed for uri={}, header={}, value={}", request.getRequestURI(), headerName, simpleAuth);
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json;charset=UTF-8");
      response.getWriter().write("{\"code\":401,\"msg\":\"Unauthorized: invalid simple auth\"}");
      return false;
    }

    // 多租户兼容：若启用多租户
    if (TenantUtil.enableTenant()) {
      String headerKey = props.getTenantHeaderName();
      Integer tenantIdToSet = null;
      if (StringUtil.isNotBlank(headerKey)) {
        String tenantIdStr = request.getHeader(headerKey);
        if (StringUtil.isNotBlank(tenantIdStr)) {
          try {
            tenantIdToSet = Integer.valueOf(tenantIdStr);
          } catch (NumberFormatException e) {
            log.warn("Invalid tenant id in header {}: {}", headerKey, tenantIdStr);
          }
        }
      }

      // 若请求头未提供或无效，则尝试使用默认tenantId
      if (tenantIdToSet == null && props.getDefaultTenantId() != null) {
        tenantIdToSet = props.getDefaultTenantId();
      }

      if (tenantIdToSet != null) {
        TenantContextHolder.setTenantId(tenantIdToSet);
      }
    }

    return true;
  }

  private static String md5Hex(String source) throws Exception {
    MessageDigest md = MessageDigest.getInstance("MD5");
    byte[] digest = md.digest(source.getBytes(StandardCharsets.UTF_8));
    StringBuilder sb = new StringBuilder();
    for (byte b : digest) {
      String s = Integer.toHexString(b & 0xff);
      if (s.length() == 1) sb.append('0');
      sb.append(s);
    }
    return sb.toString();
  }
}
