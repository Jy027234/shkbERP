package com.lframework.xingyun.shkb.excel.authorization;

import com.alibaba.excel.annotation.ExcelProperty;
import com.lframework.starter.web.core.components.excel.ExcelModel;
import com.lframework.xingyun.shkb.entity.ShkbAuthorizationProject;

public class AuthorizationProjectExportModel
implements ExcelModel {
    @ExcelProperty(value={"\u5c97\u4f4d"})
    private String projectName;
    @ExcelProperty(value={"\u6388\u6743\u9879\u76ee/\u9650\u5236"})
    private String authorizationItem;
    @ExcelProperty(value={"\u8d44\u8d28\u8981\u6c42"})
    private String qualificationRequirement;
    @ExcelProperty(value={"\u57f9\u8bad\u8981\u6c42"})
    private String trainingRequirement;
    @ExcelProperty(value={"\u6709\u6548\u671f\u6570\u503c"})
    private Integer validityPeriod;
    @ExcelProperty(value={"\u6709\u6548\u671f\u5355\u4f4d"})
    private String validityUnitText;
    @ExcelProperty(value={"\u5907\u6ce8"})
    private String description;
    @ExcelProperty(value={"\u72b6\u6001"})
    private String statusText;
    @ExcelProperty(value={"\u521b\u5efa\u4eba"})
    private String createBy;
    @ExcelProperty(value={"\u521b\u5efa\u65f6\u95f4"})
    private String createTime;

    public AuthorizationProjectExportModel() {
    }

    public AuthorizationProjectExportModel(ShkbAuthorizationProject project) {
        this.projectName = project.getProjectName();
        this.authorizationItem = project.getAuthorizationItem();
        this.qualificationRequirement = project.getQualificationRequirement();
        this.trainingRequirement = project.getTrainingRequirement();
        this.validityPeriod = project.getValidityPeriod();
        this.validityUnitText = this.getValidityUnitText(project.getValidityUnit());
        this.description = project.getDescription();
        this.statusText = this.getStatusText(project.getStatus());
        this.createBy = project.getCreateBy();
        this.createTime = project.getCreateTime() != null ? project.getCreateTime().toString().replace('T', ' ') : "";
    }

    private String getValidityUnitText(String validityUnit) {
        if ("month".equals(validityUnit)) {
            return "\u6708";
        }
        if ("year".equals(validityUnit)) {
            return "\u5e74";
        }
        return validityUnit;
    }

    private String getStatusText(Integer status) {
        if (status == 1) {
            return "\u542f\u7528";
        }
        if (status == 0) {
            return "\u7981\u7528";
        }
        return String.valueOf(status);
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

    public String getValidityUnitText() {
        return this.validityUnitText;
    }

    public String getDescription() {
        return this.description;
    }

    public String getStatusText() {
        return this.statusText;
    }

    public String getCreateBy() {
        return this.createBy;
    }

    public String getCreateTime() {
        return this.createTime;
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

    public void setValidityUnitText(String validityUnitText) {
        this.validityUnitText = validityUnitText;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AuthorizationProjectExportModel)) {
            return false;
        }
        AuthorizationProjectExportModel other = (AuthorizationProjectExportModel)o;
        if (!other.canEqual(this)) {
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
        String this$validityUnitText = this.getValidityUnitText();
        String other$validityUnitText = other.getValidityUnitText();
        if (this$validityUnitText == null ? other$validityUnitText != null : !this$validityUnitText.equals(other$validityUnitText)) {
            return false;
        }
        String this$description = this.getDescription();
        String other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description)) {
            return false;
        }
        String this$statusText = this.getStatusText();
        String other$statusText = other.getStatusText();
        if (this$statusText == null ? other$statusText != null : !this$statusText.equals(other$statusText)) {
            return false;
        }
        String this$createBy = this.getCreateBy();
        String other$createBy = other.getCreateBy();
        if (this$createBy == null ? other$createBy != null : !this$createBy.equals(other$createBy)) {
            return false;
        }
        String this$createTime = this.getCreateTime();
        String other$createTime = other.getCreateTime();
        return !(this$createTime == null ? other$createTime != null : !this$createTime.equals(other$createTime));
    }

    protected boolean canEqual(Object other) {
        return other instanceof AuthorizationProjectExportModel;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
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
        String $validityUnitText = this.getValidityUnitText();
        result = result * 59 + ($validityUnitText == null ? 43 : $validityUnitText.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        String $statusText = this.getStatusText();
        result = result * 59 + ($statusText == null ? 43 : $statusText.hashCode());
        String $createBy = this.getCreateBy();
        result = result * 59 + ($createBy == null ? 43 : $createBy.hashCode());
        String $createTime = this.getCreateTime();
        result = result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
        return result;
    }

    public String toString() {
        return "AuthorizationProjectExportModel(projectName=" + this.getProjectName() + ", authorizationItem=" + this.getAuthorizationItem() + ", qualificationRequirement=" + this.getQualificationRequirement() + ", trainingRequirement=" + this.getTrainingRequirement() + ", validityPeriod=" + this.getValidityPeriod() + ", validityUnitText=" + this.getValidityUnitText() + ", description=" + this.getDescription() + ", statusText=" + this.getStatusText() + ", createBy=" + this.getCreateBy() + ", createTime=" + this.getCreateTime() + ")";
    }
}


