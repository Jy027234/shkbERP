package com.lframework.xingyun.shkb.utils;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 附件物理文件生命周期工具。
 *
 * <p>上传通过 jugg UploadUtil 落盘到上传根目录，数据库仅保存 URL
 * （形如 http://host/oss/1000/2026/08/17/xxx.txt 或 /oss/1000/...）。
 * 删除附件时除删除数据库行外，还应删除物理文件，避免孤儿文件堆积。
 * 本工具严格把路径限制在上传根目录内，拒绝 .. 逃逸与外部 :// URL。</p>
 */
@Component
public class ShkbUploadFileUtil {

    private static final Logger log = LoggerFactory.getLogger(ShkbUploadFileUtil.class);

    @Value("${jugg.upload.location}")
    private String uploadLocation;

    /**
     * 根据持久化 URL 删除上传根目录下的物理文件。
     *
     * @param fileUrl 数据库中的附件 URL
     * @return true 表示文件存在并已删除；false 表示文件不存在（视为已清理）
     */
    public boolean deletePhysicalFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return false;
        }
        String relativePath;
        try {
            relativePath = fileUrl.contains("://")
                ? new URI(fileUrl).getPath()
                : fileUrl;
        } catch (URISyntaxException e) {
            log.warn("Skip deleting attachment with invalid URL: {}", fileUrl);
            return false;
        }
        if (relativePath.startsWith("/oss/")) {
            relativePath = relativePath.substring("/oss".length());
        } else if (relativePath.startsWith("/uploads/")) {
            relativePath = relativePath.substring("/uploads".length());
        }
        Path uploadsRoot = Paths.get(this.uploadLocation).toAbsolutePath().normalize();
        Path target = uploadsRoot
            .resolve(relativePath.startsWith("/") ? relativePath.substring(1) : relativePath)
            .normalize();
        if (!target.startsWith(uploadsRoot)) {
            throw new DefaultClientException("不支持的附件路径！");
        }
        try {
            if (!Files.exists(target)) {
                return false;
            }
            Files.delete(target);
            log.info("Deleted attachment physical file: {}", target);
            return true;
        } catch (Exception e) {
            // 物理文件删除失败不阻断数据库删除；记录日志便于运维回收。
            log.warn("Failed to delete attachment physical file {}: {}", target, e.getMessage());
            return false;
        }
    }
}
