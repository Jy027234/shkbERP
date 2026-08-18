package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import com.lframework.xingyun.shkb.entity.ShkbAuthorizationProject;
import com.lframework.xingyun.shkb.entity.ShkbPersonAuthorizationProject;
import java.time.LocalDateTime;
import java.util.List;

@TableName(value="shkb_person_authorization")
public class ShkbPersonAuthorization
extends BaseEntity
implements BaseDto {
    @TableField(exist=false)
    private static final long serialVersionUID = 1L;
    @TableId
    private String id;
    private String tenantId;
    private String employeeId;
    private Integer status;
    private String credentialFileUrl;
    private String credentialFileName;
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
    @TableField(exist=false)
    private String employeeName;
    @TableField(exist=false)
    private String employeeCode;
    @TableField(exist=false)
    private List<ShkbAuthorizationProject> projects;
    @TableField(exist=false)
    private List<ShkbPersonAuthorizationProject> projectRelations;

    public String getId() {
        return this.id;
    }

    public String getTenantId() {
        return this.tenantId;
    }

    public String getEmployeeId() {
        return this.employeeId;
    }

    public Integer getStatus() {
        return this.status;
    }

    public String getCredentialFileUrl() {
        return this.credentialFileUrl;
    }

    public String getCredentialFileName() {
        return this.credentialFileName;
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

    public String getEmployeeName() {
        return this.employeeName;
    }

    public String getEmployeeCode() {
        return this.employeeCode;
    }

    public List<ShkbAuthorizationProject> getProjects() {
        return this.projects;
    }

    public List<ShkbPersonAuthorizationProject> getProjectRelations() {
        return this.projectRelations;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setCredentialFileUrl(String credentialFileUrl) {
        this.credentialFileUrl = credentialFileUrl;
    }

    public void setCredentialFileName(String credentialFileName) {
        this.credentialFileName = credentialFileName;
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

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public void setProjects(List<ShkbAuthorizationProject> projects) {
        this.projects = projects;
    }

    public void setProjectRelations(List<ShkbPersonAuthorizationProject> projectRelations) {
        this.projectRelations = projectRelations;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ShkbPersonAuthorization)) {
            return false;
        }
        ShkbPersonAuthorization other = (ShkbPersonAuthorization)((Object)o);
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
        String this$employeeId = this.getEmployeeId();
        String other$employeeId = other.getEmployeeId();
        if (this$employeeId == null ? other$employeeId != null : !this$employeeId.equals(other$employeeId)) {
            return false;
        }
        Integer this$status = this.getStatus();
        Integer other$status = other.getStatus();
        if (this$status == null ? other$status != null : !((Object)this$status).equals(other$status)) {
            return false;
        }
        String this$credentialFileUrl = this.getCredentialFileUrl();
        String other$credentialFileUrl = other.getCredentialFileUrl();
        if (this$credentialFileUrl == null ? other$credentialFileUrl != null : !this$credentialFileUrl.equals(other$credentialFileUrl)) {
            return false;
        }
        String this$credentialFileName = this.getCredentialFileName();
        String other$credentialFileName = other.getCredentialFileName();
        if (this$credentialFileName == null ? other$credentialFileName != null : !this$credentialFileName.equals(other$credentialFileName)) {
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
        String this$employeeName = this.getEmployeeName();
        String other$employeeName = other.getEmployeeName();
        if (this$employeeName == null ? other$employeeName != null : !this$employeeName.equals(other$employeeName)) {
            return false;
        }
        String this$employeeCode = this.getEmployeeCode();
        String other$employeeCode = other.getEmployeeCode();
        if (this$employeeCode == null ? other$employeeCode != null : !this$employeeCode.equals(other$employeeCode)) {
            return false;
        }
        List<ShkbAuthorizationProject> this$projects = this.getProjects();
        List<ShkbAuthorizationProject> other$projects = other.getProjects();
        if (this$projects == null ? other$projects != null : !((Object)this$projects).equals(other$projects)) {
            return false;
        }
        List<ShkbPersonAuthorizationProject> this$projectRelations = this.getProjectRelations();
        List<ShkbPersonAuthorizationProject> other$projectRelations = other.getProjectRelations();
        return !(this$projectRelations == null ? other$projectRelations != null : !((Object)this$projectRelations).equals(other$projectRelations));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ShkbPersonAuthorization;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $tenantId = this.getTenantId();
        result = result * 59 + ($tenantId == null ? 43 : $tenantId.hashCode());
        String $employeeId = this.getEmployeeId();
        result = result * 59 + ($employeeId == null ? 43 : $employeeId.hashCode());
        Integer $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)$status).hashCode());
        String $credentialFileUrl = this.getCredentialFileUrl();
        result = result * 59 + ($credentialFileUrl == null ? 43 : $credentialFileUrl.hashCode());
        String $credentialFileName = this.getCredentialFileName();
        result = result * 59 + ($credentialFileName == null ? 43 : $credentialFileName.hashCode());
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
        String $employeeName = this.getEmployeeName();
        result = result * 59 + ($employeeName == null ? 43 : $employeeName.hashCode());
        String $employeeCode = this.getEmployeeCode();
        result = result * 59 + ($employeeCode == null ? 43 : $employeeCode.hashCode());
        List<ShkbAuthorizationProject> $projects = this.getProjects();
        result = result * 59 + ($projects == null ? 43 : ((Object)$projects).hashCode());
        List<ShkbPersonAuthorizationProject> $projectRelations = this.getProjectRelations();
        result = result * 59 + ($projectRelations == null ? 43 : ((Object)$projectRelations).hashCode());
        return result;
    }

    public String toString() {
        return "ShkbPersonAuthorization(id=" + this.getId() + ", tenantId=" + this.getTenantId() + ", employeeId=" + this.getEmployeeId() + ", status=" + this.getStatus() + ", credentialFileUrl=" + this.getCredentialFileUrl() + ", credentialFileName=" + this.getCredentialFileName() + ", description=" + this.getDescription() + ", createById=" + this.getCreateById() + ", createBy=" + this.getCreateBy() + ", createTime=" + this.getCreateTime() + ", updateById=" + this.getUpdateById() + ", updateBy=" + this.getUpdateBy() + ", updateTime=" + this.getUpdateTime() + ", deleted=" + this.getDeleted() + ", employeeName=" + this.getEmployeeName() + ", employeeCode=" + this.getEmployeeCode() + ", projects=" + this.getProjects() + ", projectRelations=" + this.getProjectRelations() + ")";
    }
}
