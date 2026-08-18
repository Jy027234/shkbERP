package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import java.time.LocalDateTime;

@TableName(value="shkb_authorization_required_course")
public class ShkbAuthorizationRequiredCourse
extends BaseEntity
implements BaseDto {
    @TableField(exist=false)
    private static final long serialVersionUID = 1L;
    @TableId
    private String id;
    private String tenantId;
    private String projectId;
    private String courseId;
    private Integer isRequired;
    @TableField(fill=FieldFill.INSERT)
    private String createById;
    @TableField(fill=FieldFill.INSERT)
    private String createBy;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableLogic
    private Integer deleted;

    public String getId() {
        return this.id;
    }

    public String getTenantId() {
        return this.tenantId;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public String getCourseId() {
        return this.courseId;
    }

    public Integer getIsRequired() {
        return this.isRequired;
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

    public Integer getDeleted() {
        return this.deleted;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setIsRequired(Integer isRequired) {
        this.isRequired = isRequired;
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

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ShkbAuthorizationRequiredCourse)) {
            return false;
        }
        ShkbAuthorizationRequiredCourse other = (ShkbAuthorizationRequiredCourse)((Object)o);
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
        String this$projectId = this.getProjectId();
        String other$projectId = other.getProjectId();
        if (this$projectId == null ? other$projectId != null : !this$projectId.equals(other$projectId)) {
            return false;
        }
        String this$courseId = this.getCourseId();
        String other$courseId = other.getCourseId();
        if (this$courseId == null ? other$courseId != null : !this$courseId.equals(other$courseId)) {
            return false;
        }
        Integer this$isRequired = this.getIsRequired();
        Integer other$isRequired = other.getIsRequired();
        if (this$isRequired == null ? other$isRequired != null : !((Object)this$isRequired).equals(other$isRequired)) {
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
        Integer this$deleted = this.getDeleted();
        Integer other$deleted = other.getDeleted();
        return !(this$deleted == null ? other$deleted != null : !((Object)this$deleted).equals(other$deleted));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ShkbAuthorizationRequiredCourse;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $tenantId = this.getTenantId();
        result = result * 59 + ($tenantId == null ? 43 : $tenantId.hashCode());
        String $projectId = this.getProjectId();
        result = result * 59 + ($projectId == null ? 43 : $projectId.hashCode());
        String $courseId = this.getCourseId();
        result = result * 59 + ($courseId == null ? 43 : $courseId.hashCode());
        Integer $isRequired = this.getIsRequired();
        result = result * 59 + ($isRequired == null ? 43 : ((Object)$isRequired).hashCode());
        String $createById = this.getCreateById();
        result = result * 59 + ($createById == null ? 43 : $createById.hashCode());
        String $createBy = this.getCreateBy();
        result = result * 59 + ($createBy == null ? 43 : $createBy.hashCode());
        LocalDateTime $createTime = this.getCreateTime();
        result = result * 59 + ($createTime == null ? 43 : ((Object)$createTime).hashCode());
        Integer $deleted = this.getDeleted();
        result = result * 59 + ($deleted == null ? 43 : ((Object)$deleted).hashCode());
        return result;
    }

    public String toString() {
        return "ShkbAuthorizationRequiredCourse(id=" + this.getId() + ", tenantId=" + this.getTenantId() + ", projectId=" + this.getProjectId() + ", courseId=" + this.getCourseId() + ", isRequired=" + this.getIsRequired() + ", createById=" + this.getCreateById() + ", createBy=" + this.getCreateBy() + ", createTime=" + this.getCreateTime() + ", deleted=" + this.getDeleted() + ")";
    }
}
