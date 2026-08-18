package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import java.time.LocalDateTime;

@TableName(value="shkb_employee_file")
public class ShkbEmployeeFile
extends BaseEntity
implements BaseDto {
    @TableField(exist=false)
    private static final long serialVersionUID = 1L;
    @TableId
    private String id;
    private String employeeId;
    private String fileName;
    private String fileType;
    private String fileUrl;
    private Long fileSize;
    private String description;
    @TableField(fill=FieldFill.INSERT)
    private String createById;
    @TableField(fill=FieldFill.INSERT)
    private String createBy;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;

    public String getId() {
        return this.id;
    }

    public String getEmployeeId() {
        return this.employeeId;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getFileType() {
        return this.fileType;
    }

    public String getFileUrl() {
        return this.fileUrl;
    }

    public Long getFileSize() {
        return this.fileSize;
    }

    public String getDescription() {
        return this.description;
    }

    public String getCreateById() {
        return this.createById;
    }

    public String getCreateBy() {
        return this.createBy;
    }

    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreateById(String createById) {
        this.createById = createById;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ShkbEmployeeFile)) {
            return false;
        }
        ShkbEmployeeFile other = (ShkbEmployeeFile)((Object)o);
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
        String this$employeeId = this.getEmployeeId();
        String other$employeeId = other.getEmployeeId();
        if (this$employeeId == null ? other$employeeId != null : !this$employeeId.equals(other$employeeId)) {
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
        String this$fileUrl = this.getFileUrl();
        String other$fileUrl = other.getFileUrl();
        if (this$fileUrl == null ? other$fileUrl != null : !this$fileUrl.equals(other$fileUrl)) {
            return false;
        }
        Long this$fileSize = this.getFileSize();
        Long other$fileSize = other.getFileSize();
        if (this$fileSize == null ? other$fileSize != null : !((Object)this$fileSize).equals(other$fileSize)) {
            return false;
        }
        String this$description = this.getDescription();
        String other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description)) {
            return false;
        }
        String this$createById = this.getCreateById();
        String other$createById = other.getCreateById();
        if (this$createById == null ? other$createById != null : !this$createById.equals(other$createById)) {
            return false;
        }
        String this$createBy = this.getCreateBy();
        String other$createBy = other.getCreateBy();
        if (this$createBy == null ? other$createBy != null : !this$createBy.equals(other$createBy)) {
            return false;
        }
        LocalDateTime this$createTime = this.getCreateTime();
        LocalDateTime other$createTime = other.getCreateTime();
        return !(this$createTime == null ? other$createTime != null : !((Object)this$createTime).equals(other$createTime));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ShkbEmployeeFile;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $employeeId = this.getEmployeeId();
        result = result * 59 + ($employeeId == null ? 43 : $employeeId.hashCode());
        String $fileName = this.getFileName();
        result = result * 59 + ($fileName == null ? 43 : $fileName.hashCode());
        String $fileType = this.getFileType();
        result = result * 59 + ($fileType == null ? 43 : $fileType.hashCode());
        String $fileUrl = this.getFileUrl();
        result = result * 59 + ($fileUrl == null ? 43 : $fileUrl.hashCode());
        Long $fileSize = this.getFileSize();
        result = result * 59 + ($fileSize == null ? 43 : ((Object)$fileSize).hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        String $createById = this.getCreateById();
        result = result * 59 + ($createById == null ? 43 : $createById.hashCode());
        String $createBy = this.getCreateBy();
        result = result * 59 + ($createBy == null ? 43 : $createBy.hashCode());
        LocalDateTime $createTime = this.getCreateTime();
        result = result * 59 + ($createTime == null ? 43 : ((Object)$createTime).hashCode());
        return result;
    }

    public String toString() {
        return "ShkbEmployeeFile(id=" + this.getId() + ", employeeId=" + this.getEmployeeId() + ", fileName=" + this.getFileName() + ", fileType=" + this.getFileType() + ", fileUrl=" + this.getFileUrl() + ", fileSize=" + this.getFileSize() + ", description=" + this.getDescription() + ", createById=" + this.getCreateById() + ", createBy=" + this.getCreateBy() + ", createTime=" + this.getCreateTime() + ")";
    }
}
