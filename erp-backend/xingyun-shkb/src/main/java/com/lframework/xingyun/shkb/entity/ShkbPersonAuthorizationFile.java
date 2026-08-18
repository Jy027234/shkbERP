package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import java.time.LocalDateTime;

@TableName(value="shkb_person_authorization_file")
public class ShkbPersonAuthorizationFile
extends BaseEntity
implements BaseDto {
    @TableId
    private String id;
    private String authorizationId;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String fileUrl;
    @TableField(fill=FieldFill.INSERT)
    private String createBy;
    @TableField(fill=FieldFill.INSERT)
    private String createById;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;

    public String getId() {
        return this.id;
    }

    public String getAuthorizationId() {
        return this.authorizationId;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getFileType() {
        return this.fileType;
    }

    public Long getFileSize() {
        return this.fileSize;
    }

    public String getFileUrl() {
        return this.fileUrl;
    }

    public String getCreateBy() {
        return this.createBy;
    }

    public String getCreateById() {
        return this.createById;
    }

    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAuthorizationId(String authorizationId) {
        this.authorizationId = authorizationId;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public void setCreateById(String createById) {
        this.createById = createById;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ShkbPersonAuthorizationFile)) {
            return false;
        }
        ShkbPersonAuthorizationFile other = (ShkbPersonAuthorizationFile)((Object)o);
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        String this$id = this.getId();
        String other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
            return false;
        }
        String this$authorizationId = this.getAuthorizationId();
        String other$authorizationId = other.getAuthorizationId();
        if (this$authorizationId == null ? other$authorizationId != null : !this$authorizationId.equals(other$authorizationId)) {
            return false;
        }
        String this$fileName = this.getFileName();
        String other$fileName = other.getFileName();
        if (this$fileName == null ? other$fileName != null : !this$fileName.equals(other$fileName)) {
            return false;
        }
        String this$fileType = this.getFileType();
        String other$fileType = other.getFileType();
        if (this$fileType == null ? other$fileType != null : !this$fileType.equals(other$fileType)) {
            return false;
        }
        Long this$fileSize = this.getFileSize();
        Long other$fileSize = other.getFileSize();
        if (this$fileSize == null ? other$fileSize != null : !((Object)this$fileSize).equals(other$fileSize)) {
            return false;
        }
        String this$fileUrl = this.getFileUrl();
        String other$fileUrl = other.getFileUrl();
        if (this$fileUrl == null ? other$fileUrl != null : !this$fileUrl.equals(other$fileUrl)) {
            return false;
        }
        String this$createBy = this.getCreateBy();
        String other$createBy = other.getCreateBy();
        if (this$createBy == null ? other$createBy != null : !this$createBy.equals(other$createBy)) {
            return false;
        }
        String this$createById = this.getCreateById();
        String other$createById = other.getCreateById();
        if (this$createById == null ? other$createById != null : !this$createById.equals(other$createById)) {
            return false;
        }
        LocalDateTime this$createTime = this.getCreateTime();
        LocalDateTime other$createTime = other.getCreateTime();
        return !(this$createTime == null ? other$createTime != null : !((Object)this$createTime).equals(other$createTime));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ShkbPersonAuthorizationFile;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $authorizationId = this.getAuthorizationId();
        result = result * 59 + ($authorizationId == null ? 43 : $authorizationId.hashCode());
        String $fileName = this.getFileName();
        result = result * 59 + ($fileName == null ? 43 : $fileName.hashCode());
        String $fileType = this.getFileType();
        result = result * 59 + ($fileType == null ? 43 : $fileType.hashCode());
        Long $fileSize = this.getFileSize();
        result = result * 59 + ($fileSize == null ? 43 : ((Object)$fileSize).hashCode());
        String $fileUrl = this.getFileUrl();
        result = result * 59 + ($fileUrl == null ? 43 : $fileUrl.hashCode());
        String $createBy = this.getCreateBy();
        result = result * 59 + ($createBy == null ? 43 : $createBy.hashCode());
        String $createById = this.getCreateById();
        result = result * 59 + ($createById == null ? 43 : $createById.hashCode());
        LocalDateTime $createTime = this.getCreateTime();
        result = result * 59 + ($createTime == null ? 43 : ((Object)$createTime).hashCode());
        return result;
    }

    public String toString() {
        return "ShkbPersonAuthorizationFile(id=" + this.getId() + ", authorizationId=" + this.getAuthorizationId() + ", fileName=" + this.getFileName() + ", fileType=" + this.getFileType() + ", fileSize=" + this.getFileSize() + ", fileUrl=" + this.getFileUrl() + ", createBy=" + this.getCreateBy() + ", createById=" + this.getCreateById() + ", createTime=" + this.getCreateTime() + ")";
    }
}


