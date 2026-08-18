package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import java.time.LocalDateTime;

@TableName(value="shkb_training_course")
public class ShkbTrainingCourse
extends BaseEntity
implements BaseDto {
    @TableField(exist=false)
    private static final long serialVersionUID = 1L;
    @TableId
    private String id;
    private String tenantId;
    private String courseName;
    private String courseType;
    private Integer implementationInterval;
    private String intervalUnit;
    private String description;
    private Integer initialTrainingHours;
    private Integer retrainingHours;
    private String teachingMethod;
    private String participants;
    private String instructor;
    private String assessmentMethod;
    private String trainingOutline;
    private Integer status;
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

    public String getId() {
        return this.id;
    }

    public String getTenantId() {
        return this.tenantId;
    }

    public String getCourseName() {
        return this.courseName;
    }

    public String getCourseType() {
        return this.courseType;
    }

    public Integer getImplementationInterval() {
        return this.implementationInterval;
    }

    public String getIntervalUnit() {
        return this.intervalUnit;
    }

    public String getDescription() {
        return this.description;
    }

    public Integer getInitialTrainingHours() {
        return this.initialTrainingHours;
    }

    public Integer getRetrainingHours() {
        return this.retrainingHours;
    }

    public String getTeachingMethod() {
        return this.teachingMethod;
    }

    public String getParticipants() {
        return this.participants;
    }

    public String getInstructor() {
        return this.instructor;
    }

    public String getAssessmentMethod() {
        return this.assessmentMethod;
    }

    public String getTrainingOutline() {
        return this.trainingOutline;
    }

    public Integer getStatus() {
        return this.status;
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

    public void setId(String id) {
        this.id = id;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public void setImplementationInterval(Integer implementationInterval) {
        this.implementationInterval = implementationInterval;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setInitialTrainingHours(Integer initialTrainingHours) {
        this.initialTrainingHours = initialTrainingHours;
    }

    public void setRetrainingHours(Integer retrainingHours) {
        this.retrainingHours = retrainingHours;
    }

    public void setTeachingMethod(String teachingMethod) {
        this.teachingMethod = teachingMethod;
    }

    public void setParticipants(String participants) {
        this.participants = participants;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public void setAssessmentMethod(String assessmentMethod) {
        this.assessmentMethod = assessmentMethod;
    }

    public void setTrainingOutline(String trainingOutline) {
        this.trainingOutline = trainingOutline;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ShkbTrainingCourse)) {
            return false;
        }
        ShkbTrainingCourse other = (ShkbTrainingCourse)((Object)o);
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
        Integer this$implementationInterval = this.getImplementationInterval();
        Integer other$implementationInterval = other.getImplementationInterval();
        if (this$implementationInterval == null ? other$implementationInterval != null : !((Object)this$implementationInterval).equals(other$implementationInterval)) {
            return false;
        }
        String this$intervalUnit = this.getIntervalUnit();
        String other$intervalUnit = other.getIntervalUnit();
        if (this$intervalUnit == null ? other$intervalUnit != null : !this$intervalUnit.equals(other$intervalUnit)) {
            return false;
        }
        String this$description = this.getDescription();
        String other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description)) {
            return false;
        }
        Integer this$initialTrainingHours = this.getInitialTrainingHours();
        Integer other$initialTrainingHours = other.getInitialTrainingHours();
        if (this$initialTrainingHours == null ? other$initialTrainingHours != null : !((Object)this$initialTrainingHours).equals(other$initialTrainingHours)) {
            return false;
        }
        Integer this$retrainingHours = this.getRetrainingHours();
        Integer other$retrainingHours = other.getRetrainingHours();
        if (this$retrainingHours == null ? other$retrainingHours != null : !((Object)this$retrainingHours).equals(other$retrainingHours)) {
            return false;
        }
        String this$teachingMethod = this.getTeachingMethod();
        String other$teachingMethod = other.getTeachingMethod();
        if (this$teachingMethod == null ? other$teachingMethod != null : !this$teachingMethod.equals(other$teachingMethod)) {
            return false;
        }
        String this$participants = this.getParticipants();
        String other$participants = other.getParticipants();
        if (this$participants == null ? other$participants != null : !this$participants.equals(other$participants)) {
            return false;
        }
        String this$instructor = this.getInstructor();
        String other$instructor = other.getInstructor();
        if (this$instructor == null ? other$instructor != null : !this$instructor.equals(other$instructor)) {
            return false;
        }
        String this$assessmentMethod = this.getAssessmentMethod();
        String other$assessmentMethod = other.getAssessmentMethod();
        if (this$assessmentMethod == null ? other$assessmentMethod != null : !this$assessmentMethod.equals(other$assessmentMethod)) {
            return false;
        }
        String this$trainingOutline = this.getTrainingOutline();
        String other$trainingOutline = other.getTrainingOutline();
        if (this$trainingOutline == null ? other$trainingOutline != null : !this$trainingOutline.equals(other$trainingOutline)) {
            return false;
        }
        Integer this$status = this.getStatus();
        Integer other$status = other.getStatus();
        if (this$status == null ? other$status != null : !((Object)this$status).equals(other$status)) {
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
        return !(this$deleted == null ? other$deleted != null : !((Object)this$deleted).equals(other$deleted));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ShkbTrainingCourse;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $tenantId = this.getTenantId();
        result = result * 59 + ($tenantId == null ? 43 : $tenantId.hashCode());
        String $courseName = this.getCourseName();
        result = result * 59 + ($courseName == null ? 43 : $courseName.hashCode());
        String $courseType = this.getCourseType();
        result = result * 59 + ($courseType == null ? 43 : $courseType.hashCode());
        Integer $implementationInterval = this.getImplementationInterval();
        result = result * 59 + ($implementationInterval == null ? 43 : ((Object)$implementationInterval).hashCode());
        String $intervalUnit = this.getIntervalUnit();
        result = result * 59 + ($intervalUnit == null ? 43 : $intervalUnit.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        Integer $initialTrainingHours = this.getInitialTrainingHours();
        result = result * 59 + ($initialTrainingHours == null ? 43 : ((Object)$initialTrainingHours).hashCode());
        Integer $retrainingHours = this.getRetrainingHours();
        result = result * 59 + ($retrainingHours == null ? 43 : ((Object)$retrainingHours).hashCode());
        String $teachingMethod = this.getTeachingMethod();
        result = result * 59 + ($teachingMethod == null ? 43 : $teachingMethod.hashCode());
        String $participants = this.getParticipants();
        result = result * 59 + ($participants == null ? 43 : $participants.hashCode());
        String $instructor = this.getInstructor();
        result = result * 59 + ($instructor == null ? 43 : $instructor.hashCode());
        String $assessmentMethod = this.getAssessmentMethod();
        result = result * 59 + ($assessmentMethod == null ? 43 : $assessmentMethod.hashCode());
        String $trainingOutline = this.getTrainingOutline();
        result = result * 59 + ($trainingOutline == null ? 43 : $trainingOutline.hashCode());
        Integer $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)$status).hashCode());
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
        return result;
    }

    public String toString() {
        return "ShkbTrainingCourse(id=" + this.getId() + ", tenantId=" + this.getTenantId() + ", courseName=" + this.getCourseName() + ", courseType=" + this.getCourseType() + ", implementationInterval=" + this.getImplementationInterval() + ", intervalUnit=" + this.getIntervalUnit() + ", description=" + this.getDescription() + ", initialTrainingHours=" + this.getInitialTrainingHours() + ", retrainingHours=" + this.getRetrainingHours() + ", teachingMethod=" + this.getTeachingMethod() + ", participants=" + this.getParticipants() + ", instructor=" + this.getInstructor() + ", assessmentMethod=" + this.getAssessmentMethod() + ", trainingOutline=" + this.getTrainingOutline() + ", status=" + this.getStatus() + ", createById=" + this.getCreateById() + ", createBy=" + this.getCreateBy() + ", createTime=" + this.getCreateTime() + ", updateById=" + this.getUpdateById() + ", updateBy=" + this.getUpdateBy() + ", updateTime=" + this.getUpdateTime() + ", deleted=" + this.getDeleted() + ")";
    }
}
