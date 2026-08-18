package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import java.time.LocalDateTime;

@TableName(value="shkb_authorization_project")
public class ShkbAuthorizationProject
extends BaseEntity
implements BaseDto {
    @TableField(exist=false)
    private static final long serialVersionUID = 1L;
    @TableId
    private String id;
    private String tenantId;
    private String projectName;
    private String authorizationItem;
    private String qualificationRequirement;
    private String trainingRequirement;
    private Integer validityPeriod;
    private String validityUnit;
    private String description;
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

    public String getProjectName() {
        return this.projectName;
    }

    public String getAuthorizationItem() {
        return this.authorizationItem;
    }

    public String getQualificationRequirement() {
        return this.qualificationRequirement;
    }

    public String getTrainingRequirement() {
        return this.trainingRequirement;
    }

    public Integer getValidityPeriod() {
        return this.validityPeriod;
    }

    public String getValidityUnit() {
        return this.validityUnit;
    }

    public String getDescription() {
        return this.description;
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

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setAuthorizationItem(String authorizationItem) {
        this.authorizationItem = authorizationItem;
    }

    public void setQualificationRequirement(String qualificationRequirement) {
        this.qualificationRequirement = qualificationRequirement;
    }

    public void setTrainingRequirement(String trainingRequirement) {
        this.trainingRequirement = trainingRequirement;
    }

    public void setValidityPeriod(Integer validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public void setValidityUnit(String validityUnit) {
        this.validityUnit = validityUnit;
    }

    public void setDescription(String description) {
        this.description = description;
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
        if (!(o instanceof ShkbAuthorizationProject)) {
            return false;
        }
        ShkbAuthorizationProject other = (ShkbAuthorizationProject)((Object)o);
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
        String this$projectName = this.getProjectName();
        String other$projectName = other.getProjectName();
        if (this$projectName == null ? other$projectName != null : !this$projectName.equals(other$projectName)) {
            return false;
        }
        String this$authorizationItem = this.getAuthorizationItem();
        String other$authorizationItem = other.getAuthorizationItem();
        if (this$authorizationItem == null ? other$authorizationItem != null : !this$authorizationItem.equals(other$authorizationItem)) {
            return false;
        }
        String this$qualificationRequirement = this.getQualificationRequirement();
        String other$qualificationRequirement = other.getQualificationRequirement();
        if (this$qualificationRequirement == null ? other$qualificationRequirement != null : !this$qualificationRequirement.equals(other$qualificationRequirement)) {
            return false;
        }
        String this$trainingRequirement = this.getTrainingRequirement();
        String other$trainingRequirement = other.getTrainingRequirement();
        if (this$trainingRequirement == null ? other$trainingRequirement != null : !this$trainingRequirement.equals(other$trainingRequirement)) {
            return false;
        }
        Integer this$validityPeriod = this.getValidityPeriod();
        Integer other$validityPeriod = other.getValidityPeriod();
        if (this$validityPeriod == null ? other$validityPeriod != null : !((Object)this$validityPeriod).equals(other$validityPeriod)) {
            return false;
        }
        String this$validityUnit = this.getValidityUnit();
        String other$validityUnit = other.getValidityUnit();
        if (this$validityUnit == null ? other$validityUnit != null : !this$validityUnit.equals(other$validityUnit)) {
            return false;
        }
        String this$description = this.getDescription();
        String other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description)) {
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
        return other instanceof ShkbAuthorizationProject;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $tenantId = this.getTenantId();
        result = result * 59 + ($tenantId == null ? 43 : $tenantId.hashCode());
        String $projectName = this.getProjectName();
        result = result * 59 + ($projectName == null ? 43 : $projectName.hashCode());
        String $authorizationItem = this.getAuthorizationItem();
        result = result * 59 + ($authorizationItem == null ? 43 : $authorizationItem.hashCode());
        String $qualificationRequirement = this.getQualificationRequirement();
        result = result * 59 + ($qualificationRequirement == null ? 43 : $qualificationRequirement.hashCode());
        String $trainingRequirement = this.getTrainingRequirement();
        result = result * 59 + ($trainingRequirement == null ? 43 : $trainingRequirement.hashCode());
        Integer $validityPeriod = this.getValidityPeriod();
        result = result * 59 + ($validityPeriod == null ? 43 : ((Object)$validityPeriod).hashCode());
        String $validityUnit = this.getValidityUnit();
        result = result * 59 + ($validityUnit == null ? 43 : $validityUnit.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
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
        return "ShkbAuthorizationProject(id=" + this.getId() + ", tenantId=" + this.getTenantId() + ", projectName=" + this.getProjectName() + ", authorizationItem=" + this.getAuthorizationItem() + ", qualificationRequirement=" + this.getQualificationRequirement() + ", trainingRequirement=" + this.getTrainingRequirement() + ", validityPeriod=" + this.getValidityPeriod() + ", validityUnit=" + this.getValidityUnit() + ", description=" + this.getDescription() + ", status=" + this.getStatus() + ", createById=" + this.getCreateById() + ", createBy=" + this.getCreateBy() + ", createTime=" + this.getCreateTime() + ", updateById=" + this.getUpdateById() + ", updateBy=" + this.getUpdateBy() + ", updateTime=" + this.getUpdateTime() + ", deleted=" + this.getDeleted() + ")";
    }
}
