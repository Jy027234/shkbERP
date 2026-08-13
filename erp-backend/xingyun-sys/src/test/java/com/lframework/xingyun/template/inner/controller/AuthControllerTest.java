package com.lframework.xingyun.template.inner.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.lframework.starter.common.exceptions.impl.UserLoginException;
import com.lframework.starter.web.core.components.security.DefaultUserDetails;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AuthControllerTest {

  @Test
  void acceptsEnabledUnlockedUserWithPermission() {
    DefaultUserDetails user = eligibleUser();

    assertDoesNotThrow(() -> AuthController.validateLoginEligibility(user));
  }

  @Test
  void reportsLockedAccountAsLockedInsteadOfExpired() {
    DefaultUserDetails user = eligibleUser();
    user.setLockStatus(true);

    UserLoginException exception = assertThrows(UserLoginException.class,
        () -> AuthController.validateLoginEligibility(user));

    assertEquals("账户已锁定，不允许登录！", exception.getMsg());
  }

  @Test
  void rejectsDisabledAccount() {
    DefaultUserDetails user = eligibleUser();
    user.setAvailable(false);

    UserLoginException exception = assertThrows(UserLoginException.class,
        () -> AuthController.validateLoginEligibility(user));

    assertEquals("账户已停用，不允许登录！", exception.getMsg());
  }

  @Test
  void rejectsAccountWithoutPermissions() {
    DefaultUserDetails user = eligibleUser();
    user.setPermissions(Set.of());

    UserLoginException exception = assertThrows(UserLoginException.class,
        () -> AuthController.validateLoginEligibility(user));

    assertEquals("账户未授权，不允许登录！", exception.getMsg());
  }

  private static DefaultUserDetails eligibleUser() {
    DefaultUserDetails user = new DefaultUserDetails();
    user.setAvailable(true);
    user.setLockStatus(false);
    user.setPermissions(Set.of("base-data:store-center:query"));
    return user;
  }
}
