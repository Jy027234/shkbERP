package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.apache.ibatis.type.JdbcType;

@TableName(value="shkb_person_authorization_project")
public class ShkbPersonAuthorizationProject
extends BaseEntity
implements BaseDto {
    @TableField(exist=false)
    private static final long serialVersionUID = 1L;
    @TableId
    private String id;
    private String tenantId;
    private String authorizationId;
    private String projectId;
    @TableField(jdbcType=JdbcType.DATE, insertStrategy=FieldStrategy.NOT_EMPTY)
    private LocalDate authorizationDate;
    @TableField(jdbcType=JdbcType.DATE, insertStrategy=FieldStrategy.NOT_EMPTY)
    private LocalDate expiryDate;
    private Integer status;
    private Integer requiredCoursesCompleted;
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
    @TableField(exist=false)
    private String projectName;

    public String getId() {
        return this.id;
    }

    public String getTenantId() {
        return this.tenantId;
    }

    public String getAuthorizationId() {
        return this.authorizationId;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public LocalDate getAuthorizationDate() {
        return this.authorizationDate;
    }

    public LocalDate getExpiryDate() {
        return this.expiryDate;
    }

    public Integer getStatus() {
        return this.status;
    }

    public Integer getRequiredCoursesCompleted() {
        return this.requiredCoursesCompleted;
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

    public String getProjectName() {
        return this.projectName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public void setAuthorizationId(String authorizationId) {
        this.authorizationId = authorizationId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setAuthorizationDate(LocalDate authorizationDate) {
        this.authorizationDate = authorizationDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setRequiredCoursesCompleted(Integer requiredCoursesCompleted) {
        this.requiredCoursesCompleted = requiredCoursesCompleted;
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

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ShkbPersonAuthorizationProject)) {
            return false;
        }
        ShkbPersonAuthorizationProject other = (ShkbPersonAuthorizationProject)((Object)o);
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
        String this$authorizationId = this.getAuthorizationId();
        String other$authorizationId = other.getAuthorizationId();
        if (this$authorizationId == null ? other$authorizationId != null : !this$authorizationId.equals(other$authorizationId)) {
            return false;
        }
        String this$projectId = this.getProjectId();
        String other$projectId = other.getProjectId();
        if (this$projectId == null ? other$projectId != null : !this$projectId.equals(other$projectId)) {
            return false;
        }
        LocalDate this$authorizationDate = this.getAuthorizationDate();
        LocalDate other$authorizationDate = other.getAuthorizationDate();
        if (this$authorizationDate == null ? other$authorizationDate != null : !((Object)this$authorizationDate).equals(other$authorizationDate)) {
            return false;
        }
        LocalDate this$expiryDate = this.getExpiryDate();
        LocalDate other$expiryDate = other.getExpiryDate();
        if (this$expiryDate == null ? other$expiryDate != null : !((Object)this$expiryDate).equals(other$expiryDate)) {
            return false;
        }
        Integer this$status = this.getStatus();
        Integer other$status = other.getStatus();
        if (this$status == null ? other$status != null : !((Object)this$status).equals(other$status)) {
            return false;
        }
        Integer this$requiredCoursesCompleted = this.getRequiredCoursesCompleted();
        Integer other$requiredCoursesCompleted = other.getRequiredCoursesCompleted();
        if (this$requiredCoursesCompleted == null ? other$requiredCoursesCompleted != null : !((Object)this$requiredCoursesCompleted).equals(other$requiredCoursesCompleted)) {
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
        String this$projectName = this.getProjectName();
        String other$projectName = other.getProjectName();
        return !(this$projectName == null ? other$projectName != null : !this$projectName.equals(other$projectName));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ShkbPersonAuthorizationProject;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $tenantId = this.getTenantId();
        result = result * 59 + ($tenantId == null ? 43 : $tenantId.hashCode());
        String $authorizationId = this.getAuthorizationId();
        result = result * 59 + ($authorizationId == null ? 43 : $authorizationId.hashCode());
        String $projectId = this.getProjectId();
        result = result * 59 + ($projectId == null ? 43 : $projectId.hashCode());
        LocalDate $authorizationDate = this.getAuthorizationDate();
        result = result * 59 + ($authorizationDate == null ? 43 : ((Object)$authorizationDate).hashCode());
        LocalDate $expiryDate = this.getExpiryDate();
        result = result * 59 + ($expiryDate == null ? 43 : ((Object)$expiryDate).hashCode());
        Integer $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)$status).hashCode());
        Integer $requiredCoursesCompleted = this.getRequiredCoursesCompleted();
        result = result * 59 + ($requiredCoursesCompleted == null ? 43 : ((Object)$requiredCoursesCompleted).hashCode());
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
        String $projectName = this.getProjectName();
        result = result * 59 + ($projectName == null ? 43 : $projectName.hashCode());
        return result;
    }

    public String toString() {
        return "ShkbPersonAuthorizationProject(id=" + this.getId() + ", tenantId=" + this.getTenantId() + ", authorizationId=" + this.getAuthorizationId() + ", projectId=" + this.getProjectId() + ", authorizationDate=" + this.getAuthorizationDate() + ", expiryDate=" + this.getExpiryDate() + ", status=" + this.getStatus() + ", requiredCoursesCompleted=" + this.getRequiredCoursesCompleted() + ", createById=" + this.getCreateById() + ", createBy=" + this.getCreateBy() + ", createTime=" + this.getCreateTime() + ", updateById=" + this.getUpdateById() + ", updateBy=" + this.getUpdateBy() + ", updateTime=" + this.getUpdateTime() + ", deleted=" + this.getDeleted() + ", projectName=" + this.getProjectName() + ")";
    }
}
