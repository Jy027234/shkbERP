package com.lframework.xingyun.api.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.lframework.xingyun.api.config.BackupProperties;
import org.junit.jupiter.api.Test;

class MysqlBackupTaskTest {

  @Test
  void parsesStandardJdbcUrl() {
    MysqlBackupTask.DatabaseTarget target = MysqlBackupTask.parseDatabaseTarget(
        "jdbc:mysql://mysql.internal:3307/shkb_platform?useUnicode=true");

    assertEquals("mysql.internal", target.host());
    assertEquals("3307", target.port());
    assertEquals("shkb_platform", target.database());
  }

  @Test
  void usesDefaultMysqlPort() {
    MysqlBackupTask.DatabaseTarget target = MysqlBackupTask.parseDatabaseTarget(
        "jdbc:mysql://127.0.0.1/shkb_platform");

    assertEquals("3306", target.port());
  }

  @Test
  void rejectsUnsupportedOrIncompleteUrls() {
    assertNull(MysqlBackupTask.parseDatabaseTarget("jdbc:postgresql://localhost/shkb_platform"));
    assertNull(MysqlBackupTask.parseDatabaseTarget("jdbc:mysql://localhost"));
    assertNull(MysqlBackupTask.parseDatabaseTarget(null));
  }

  @Test
  void backupIsDisabledByDefault() {
    assertFalse(new BackupProperties().isEnabled());
  }
}
