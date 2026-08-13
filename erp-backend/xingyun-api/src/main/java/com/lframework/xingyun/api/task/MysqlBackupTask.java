package com.lframework.xingyun.api.task;

import com.lframework.xingyun.api.config.BackupProperties;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.zip.GZIPOutputStream;

@Component
public class MysqlBackupTask {

  private static final Logger log = LoggerFactory.getLogger(MysqlBackupTask.class);

  private static final Pattern DB_NAME_PATTERN = Pattern.compile("jdbc:mysql://[^/]+/([^?]+)");

  @Autowired
  private Environment env;

  @Autowired
  private BackupProperties properties;

  @Scheduled(cron = "0 0 2 * * ?")
  public void backup() {
    if (!properties.isEnabled()) {
      return;
    }
    String dir = properties.getDir();
    if (dir == null || dir.trim().isEmpty()) {
      log.warn("backup dir is empty, skip");
      return;
    }
    String url = getProp("spring.datasource.dynamic.datasource.master.url");
    String username = getProp("spring.datasource.dynamic.datasource.master.username");
    String password = getProp("spring.datasource.dynamic.datasource.master.password");
    if (url == null || username == null || password == null) {
      log.error("datasource properties missing, skip");
      return;
    }
    String host = extractHost(url);
    String port = extractPort(url);
    String dbName = extractDbName(url);
    if (dbName == null) {
      log.error("cannot parse db name from url: {}", url);
      return;
    }
    Path backupDir = Paths.get(dir);
    try {
      Files.createDirectories(backupDir);
    } catch (IOException e) {
      log.error("create backup dir error: {}", dir, e);
      return;
    }
    String ts = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(new Date());
    File outFile = backupDir.resolve(dbName + "_" + ts + ".sql.gz").toFile();
    ProcessBuilder pb = new ProcessBuilder();
    Path homeDefaults = Paths.get(System.getProperty("user.home"), ".my.cnf");
    Path tempDefaults = null;
    String defaultsArg;
    if (Files.exists(homeDefaults)) {
      defaultsArg = "--defaults-file=" + homeDefaults.toAbsolutePath();
      // log.info("using credentials file: {}", homeDefaults.toAbsolutePath());
    } else {
      try {
        tempDefaults = createDefaultsFile(host, port, username, password);
        defaultsArg = "--defaults-extra-file=" + tempDefaults.toAbsolutePath();
        log.info("using temporary credentials file: {}", tempDefaults.toAbsolutePath());
      } catch (IOException e) {
        log.error("create defaults file failed", e);
        return;
      }
    }
    pb.command(
        "mysqldump",
        defaultsArg,
        "--column-statistics=0",
        "--no-tablespaces",
        dbName
    );
    long start = System.currentTimeMillis();
    try {
      // 打印当前执行日期时间 yyyyMMdd HH:mm:ss
      log.info("backup time {}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
      log.info("backup start, db={}, file={}", dbName, outFile.getAbsolutePath());
      Process p = pb.start();
      Thread errLogger = new Thread(() -> logErrorStream(p));
      errLogger.setDaemon(true);
      errLogger.start();
      try (InputStream in = new BufferedInputStream(p.getInputStream());
           GZIPOutputStream gz = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)))) {
        byte[] buf = new byte[8192];
        int n;
        while ((n = in.read(buf)) != -1) {
          gz.write(buf, 0, n);
        }
        gz.finish();
      }
      boolean finished = p.waitFor(30, TimeUnit.MINUTES);
      if (!finished) {
        p.destroyForcibly();
        log.error("mysqldump timeout: {}", outFile.getAbsolutePath());
        outFile.delete();
        return;
      }
      int code = p.exitValue();
      if (code != 0) {
        log.error("mysqldump exit code={}, file={}", code, outFile.getAbsolutePath());
        outFile.delete();
        return;
      }
      long cost = System.currentTimeMillis() - start;
      log.info("mysql backup success, cost={}ms, size={} bytes", cost, outFile.length());
      cleanupOldFiles(backupDir, properties.getRetainDays());
    } catch (Exception e) {
      log.error("mysql backup failed", e);
      Optional.ofNullable(outFile).filter(File::exists).ifPresent(File::delete);
    } finally {
      if (tempDefaults != null) {
        try {
          Files.deleteIfExists(tempDefaults);
        } catch (IOException ignore) {
        }
      }
    }
  }

  private void logErrorStream(Process p) {
    try (InputStream es = p.getErrorStream()) {
      byte[] buf = new byte[1024];
      int n;
      while ((n = es.read(buf)) != -1) {
        if (n > 0) {
          String s = new String(buf, 0, n);
          if (!s.trim().isEmpty()) {
            log.warn("mysqldump stderr: {}", s.trim());
          }
        }
      }
    } catch (IOException ignore) {
    }
  }

  private Path createDefaultsFile(String host, String port, String user, String password) throws IOException {
    Path tmp = Files.createTempFile("mysqldump-", ".cnf");
    String content = "[client]\n" +
        "host=" + host + "\n" +
        "port=" + port + "\n" +
        "user=" + user + "\n" +
        "password=" + password + "\n";
    Files.write(tmp, content.getBytes());
    try {
      Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rw-------");
      Files.setPosixFilePermissions(tmp, perms);
    } catch (UnsupportedOperationException ignored) {
      // Non-POSIX (e.g., Windows); best-effort without setting permissions
    }
    return tmp;
  }

  private void cleanupOldFiles(Path dir, int retainDays) {
    if (retainDays <= 0) {
      return;
    }
    long now = System.currentTimeMillis();
    long keepMillis = TimeUnit.DAYS.toMillis(retainDays);
    try {
      Files.list(dir).filter(Files::isRegularFile).forEach(p -> {
        try {
          long lastModified = Files.getLastModifiedTime(p).toMillis();
          long age = now - lastModified;
          if (age > keepMillis) {
            Files.deleteIfExists(p);
            log.info("removed expired backup: {}", p.toAbsolutePath());
          }
        } catch (Exception e) {
          log.warn("cleanup file failed: {}", p, e);
        }
      });
    } catch (IOException e) {
      log.warn("list backup dir failed: {}", dir, e);
    }
  }

  private String getProp(String key) {
    return env.getProperty(key);
  }

  private String extractDbName(String url) {
    Matcher m = DB_NAME_PATTERN.matcher(url);
    if (m.find()) {
      return m.group(1);
    }
    return null;
  }

  private String extractHost(String url) {
    try {
      String s = url.substring("jdbc:mysql://".length());
      int slash = s.indexOf('/') ;
      String hostPort = slash >= 0 ? s.substring(0, slash) : s;
      int idx = hostPort.indexOf(':');
      if (idx > 0) {
        return hostPort.substring(0, idx);
      }
      return hostPort;
    } catch (Exception e) {
      return "127.0.0.1";
    }
  }

  private String extractPort(String url) {
    try {
      String s = url.substring("jdbc:mysql://".length());
      int slash = s.indexOf('/') ;
      String hostPort = slash >= 0 ? s.substring(0, slash) : s;
      int idx = hostPort.indexOf(':');
      if (idx > 0) {
        return hostPort.substring(idx + 1);
      }
      return "3306";
    } catch (Exception e) {
      return "3306";
    }
  }
}
