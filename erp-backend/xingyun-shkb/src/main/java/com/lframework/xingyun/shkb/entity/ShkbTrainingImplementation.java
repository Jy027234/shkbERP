package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName(value="shkb_training_implementation")
public class ShkbTrainingImplementation
extends BaseEntity
implements BaseDto {
    @TableField(exist=false)
    private static final long serialVersionUID = 1L;
    @TableId
    private String id;
    private String tenantId;
    private String courseId;
    @TableField(exist=false)
    private String courseName;
    @TableField(exist=false)
    private String courseType;
    @TableField(exist=false)
    private BigDecimal courseTrainingHours;
    private LocalDate planStartDate;
    private LocalDate planEndDate;
    private LocalDateTime actualStartDate;
    private LocalDateTime actualEndDate;
    private Integer status;
    private String trainingLocation;
    private String instructor;
    private Integer participantCount;
    private String description;
    @TableField(fill=FieldFill.INSERT)
    private String createById;
    @TableField(fill=FieldFill.INSERT)
    private String createBy;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private String updateById;
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private String updateBy;
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
    private String url;
    private String fileName;
    private String contentType;
    private String fileSize;
    private String fileSuffix;

    public String getId() {
        return this.id;
    }

    public String getTenantId() {
        return this.tenantId;
    }

    public String getCourseId() {
        return this.courseId;
    }

    public String getCourseName() {
        return this.courseName;
    }

    public String getCourseType() {
        return this.courseType;
    }

    public BigDecimal getCourseTrainingHours() {
        return this.courseTrainingHours;
    }

    public LocalDate getPlanStartDate() {
        return this.planStartDate;
    }

    public LocalDate getPlanEndDate() {
        return this.planEndDate;
    }

    public LocalDateTime getActualStartDate() {
        return this.actualStartDate;
    }

    public LocalDateTime getActualEndDate() {
        return this.actualEndDate;
    }

    public Integer getStatus() {
        return this.status;
    }

    public String getTrainingLocation() {
        return this.trainingLocation;
    }

    public String getInstructor() {
        return this.instructor;
    }

    public Integer getParticipantCount() {
        return this.participantCount;
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

    public String getUpdateById() {
        return this.updateById;
    }

    public String getUpdateBy() {
        return this.updateBy;
    }

    public LocalDateTime getUpdateTime() {
        return this.updateTime;
    }

    public Integer getDeleted() {
        return this.deleted;
    }

    public String getUrl() {
        return this.url;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getContentType() {
        return this.contentType;
    }

    public String getFileSize() {
        return this.fileSize;
    }

    public String getFileSuffix() {
        return this.fileSuffix;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public void setCourseTrainingHours(BigDecimal courseTrainingHours) {
        this.courseTrainingHours = courseTrainingHours;
    }

    public void setPlanStartDate(LocalDate planStartDate) {
        this.planStartDate = planStartDate;
    }

    public void setPlanEndDate(LocalDate planEndDate) {
        this.planEndDate = planEndDate;
    }

    public void setActualStartDate(LocalDateTime actualStartDate) {
        this.actualStartDate = actualStartDate;
    }

    public void setActualEndDate(LocalDateTime actualEndDate) {
        this.actualEndDate = actualEndDate;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setTrainingLocation(String trainingLocation) {
        this.trainingLocation = trainingLocation;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public void setParticipantCount(Integer participantCount) {
        this.participantCount = participantCount;
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

    public void setUpdateById(String updateById) {
        this.updateById = updateById;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public void setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ShkbTrainingImplementation)) {
            return false;
        }
        ShkbTrainingImplementation other = (ShkbTrainingImplementation)((Object)o);
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
        String this$tenantId = this.getTenantId();
        String other$tenantId = other.getTenantId();
        if (this$tenantId == null ? other$tenantId != null : !this$tenantId.equals(other$tenantId)) {
            return false;
        }
        String this$courseId = this.getCourseId();
        String other$courseId = other.getCourseId();
        if (this$courseId == null ? other$courseId != null : !this$courseId.equals(other$courseId)) {
            return false;
        }
        String this$courseName = this.getCourseName();
        String other$courseName = other.getCourseName();
        if (this$courseName == null ? other$courseName != null : !this$courseName.equals(other$courseName)) {
            return false;
        }
        String this$courseType = this.getCourseType();
        String other$courseType = other.getCourseType();
        if (this$courseType == null ? other$courseType != null : !this$courseType.equals(other$courseType)) {
            return false;
        }
        BigDecimal this$courseTrainingHours = this.getCourseTrainingHours();
        BigDecimal other$courseTrainingHours = other.getCourseTrainingHours();
        if (this$courseTrainingHours == null ? other$courseTrainingHours != null : !((Object)this$courseTrainingHours).equals(other$courseTrainingHours)) {
            return false;
        }
        LocalDate this$planStartDate = this.getPlanStartDate();
        LocalDate other$planStartDate = other.getPlanStartDate();
        if (this$planStartDate == null ? other$planStartDate != null : !((Object)this$planStartDate).equals(other$planStartDate)) {
            return false;
        }
        LocalDate this$planEndDate = this.getPlanEndDate();
        LocalDate other$planEndDate = other.getPlanEndDate();
        if (this$planEndDate == null ? other$planEndDate != null : !((Object)this$planEndDate).equals(other$planEndDate)) {
            return false;
        }
        LocalDateTime this$actualStartDate = this.getActualStartDate();
        LocalDateTime other$actualStartDate = other.getActualStartDate();
        if (this$actualStartDate == null ? other$actualStartDate != null : !((Object)this$actualStartDate).equals(other$actualStartDate)) {
            return false;
        }
        LocalDateTime this$actualEndDate = this.getActualEndDate();
        LocalDateTime other$actualEndDate = other.getActualEndDate();
        if (this$actualEndDate == null ? other$actualEndDate != null : !((Object)this$actualEndDate).equals(other$actualEndDate)) {
            return false;
        }
        Integer this$status = this.getStatus();
        Integer other$status = other.getStatus();
        if (this$status == null ? other$status != null : !((Object)this$status).equals(other$status)) {
            return false;
        }
        String this$trainingLocation = this.getTrainingLocation();
        String other$trainingLocation = other.getTrainingLocation();
        if (this$trainingLocation == null ? other$trainingLocation != null : !this$trainingLocation.equals(other$trainingLocation)) {
            return false;
        }
        String this$instructor = this.getInstructor();
        String other$instructor = other.getInstructor();
        if (this$instructor == null ? other$instructor != null : !this$instructor.equals(other$instructor)) {
            return false;
        }
        Integer this$participantCount = this.getParticipantCount();
        Integer other$participantCount = other.getParticipantCount();
        if (this$participantCount == null ? other$participantCount != null : !((Object)this$participantCount).equals(other$participantCount)) {
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
        if (this$createTime == null ? other$createTime != null : !((Object)this$createTime).equals(other$createTime)) {
            return false;
        }
        String this$updateById = this.getUpdateById();
        String other$updateById = other.getUpdateById();
        if (this$updateById == null ? other$updateById != null : !this$updateById.equals(other$updateById)) {
            return false;
        }
        String this$updateBy = this.getUpdateBy();
        String other$updateBy = other.getUpdateBy();
        if (this$updateBy == null ? other$updateBy != null : !this$updateBy.equals(other$updateBy)) {
            return false;
        }
        LocalDateTime this$updateTime = this.getUpdateTime();
        LocalDateTime other$updateTime = other.getUpdateTime();
        if (this$updateTime == null ? other$updateTime != null : !((Object)this$updateTime).equals(other$updateTime)) {
            return false;
        }
        Integer this$deleted = this.getDeleted();
        Integer other$deleted = other.getDeleted();
        if (this$deleted == null ? other$deleted != null : !((Object)this$deleted).equals(other$deleted)) {
            return false;
        }
        String this$url = this.getUrl();
        String other$url = other.getUrl();
        if (this$url == null ? other$url != null : !this$url.equals(other$url)) {
            return false;
        }
        String this$fileName = this.getFileName();
        String other$fileName = other.getFileName();
        if (this$fileName == null ? other$fileName != null : !this$fileName.equals(other$fileName)) {
            return false;
        }
        String this$contentType = this.getContentType();
        String other$contentType = other.getContentType();
        if (this$contentType == null ? other$contentType != null : !this$contentType.equals(other$contentType)) {
            return false;
        }
        String this$fileSize = this.getFileSize();
        String other$fileSize = other.getFileSize();
        if (this$fileSize == null ? other$fileSize != null : !this$fileSize.equals(other$fileSize)) {
            return false;
        }
        String this$fileSuffix = this.getFileSuffix();
        String other$fileSuffix = other.getFileSuffix();
        return !(this$fileSuffix == null ? other$fileSuffix != null : !this$fileSuffix.equals(other$fileSuffix));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ShkbTrainingImplementation;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $tenantId = this.getTenantId();
        result = result * 59 + ($tenantId == null ? 43 : $tenantId.hashCode());
        String $courseId = this.getCourseId();
        result = result * 59 + ($courseId == null ? 43 : $courseId.hashCode());
        String $courseName = this.getCourseName();
        result = result * 59 + ($courseName == null ? 43 : $courseName.hashCode());
        String $courseType = this.getCourseType();
        result = result * 59 + ($courseType == null ? 43 : $courseType.hashCode());
        BigDecimal $courseTrainingHours = this.getCourseTrainingHours();
        result = result * 59 + ($courseTrainingHours == null ? 43 : ((Object)$courseTrainingHours).hashCode());
        LocalDate $planStartDate = this.getPlanStartDate();
        result = result * 59 + ($planStartDate == null ? 43 : ((Object)$planStartDate).hashCode());
        LocalDate $planEndDate = this.getPlanEndDate();
        result = result * 59 + ($planEndDate == null ? 43 : ((Object)$planEndDate).hashCode());
        LocalDateTime $actualStartDate = this.getActualStartDate();
        result = result * 59 + ($actualStartDate == null ? 43 : ((Object)$actualStartDate).hashCode());
        LocalDateTime $actualEndDate = this.getActualEndDate();
        result = result * 59 + ($actualEndDate == null ? 43 : ((Object)$actualEndDate).hashCode());
        Integer $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)$status).hashCode());
        String $trainingLocation = this.getTrainingLocation();
        result = result * 59 + ($trainingLocation == null ? 43 : $trainingLocation.hashCode());
        String $instructor = this.getInstructor();
        result = result * 59 + ($instructor == null ? 43 : $instructor.hashCode());
        Integer $participantCount = this.getParticipantCount();
        result = result * 59 + ($participantCount == null ? 43 : ((Object)$participantCount).hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        String $createById = this.getCreateById();
        result = result * 59 + ($createById == null ? 43 : $createById.hashCode());
        String $createBy = this.getCreateBy();
        result = result * 59 + ($createBy == null ? 43 : $createBy.hashCode());
        LocalDateTime $createTime = this.getCreateTime();
        result = result * 59 + ($createTime == null ? 43 : ((Object)$createTime).hashCode());
        String $updateById = this.getUpdateById();
        result = result * 59 + ($updateById == null ? 43 : $updateById.hashCode());
        String $updateBy = this.getUpdateBy();
        result = result * 59 + ($updateBy == null ? 43 : $updateBy.hashCode());
        LocalDateTime $updateTime = this.getUpdateTime();
        result = result * 59 + ($updateTime == null ? 43 : ((Object)$updateTime).hashCode());
        Integer $deleted = this.getDeleted();
        result = result * 59 + ($deleted == null ? 43 : ((Object)$deleted).hashCode());
        String $url = this.getUrl();
        result = result * 59 + ($url == null ? 43 : $url.hashCode());
        String $fileName = this.getFileName();
        result = result * 59 + ($fileName == null ? 43 : $fileName.hashCode());
        String $contentType = this.getContentType();
        result = result * 59 + ($contentType == null ? 43 : $contentType.hashCode());
        String $fileSize = this.getFileSize();
        result = result * 59 + ($fileSize == null ? 43 : $fileSize.hashCode());
        String $fileSuffix = this.getFileSuffix();
        result = result * 59 + ($fileSuffix == null ? 43 : $fileSuffix.hashCode());
        return result;
    }

    public String toString() {
        return "ShkbTrainingImplementation(id=" + this.getId() + ", tenantId=" + this.getTenantId() + ", courseId=" + this.getCourseId() + ", courseName=" + this.getCourseName() + ", courseType=" + this.getCourseType() + ", courseTrainingHours=" + this.getCourseTrainingHours() + ", planStartDate=" + this.getPlanStartDate() + ", planEndDate=" + this.getPlanEndDate() + ", actualStartDate=" + this.getActualStartDate() + ", actualEndDate=" + this.getActualEndDate() + ", status=" + this.getStatus() + ", trainingLocation=" + this.getTrainingLocation() + ", instructor=" + this.getInstructor() + ", participantCount=" + this.getParticipantCount() + ", description=" + this.getDescription() + ", createById=" + this.getCreateById() + ", createBy=" + this.getCreateBy() + ", createTime=" + this.getCreateTime() + ", updateById=" + this.getUpdateById() + ", updateBy=" + this.getUpdateBy() + ", updateTime=" + this.getUpdateTime() + ", deleted=" + this.getDeleted() + ", url=" + this.getUrl() + ", fileName=" + this.getFileName() + ", contentType=" + this.getContentType() + ", fileSize=" + this.getFileSize() + ", fileSuffix=" + this.getFileSuffix() + ")";
    }
}
