package com.lframework.xingyun.shkb.vo.authorization;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@ApiModel(value="\u521b\u5efa\u6388\u6743\u9879\u76ee\u8bf7\u6c42\u53c2\u6570")
public class CreateShkbAuthorizationProjectVo {
    @ApiModelProperty(value="\u5c97\u4f4d\u540d\u79f0")
    @NotBlank(message="\u5c97\u4f4d\u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u5c97\u4f4d\u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a") String projectName;
    @ApiModelProperty(value="\u6388\u6743\u9879\u76ee/\u9650\u5236")
    private String authorizationItem;
    @ApiModelProperty(value="\u8d44\u8d28\u8981\u6c42")
    private String qualificationRequirement;
    @ApiModelProperty(value="\u57f9\u8bad\u8981\u6c42")
    private String trainingRequirement;
    @ApiModelProperty(value="\u6709\u6548\u671f\u6570\u503c")
    private Integer validityPeriod;
    @ApiModelProperty(value="\u6709\u6548\u671f\u5355\u4f4d")
    private String validityUnit;
    @ApiModelProperty(value="\u5907\u6ce8")
    private String description;
    @ApiModelProperty(value="\u72b6\u6001")
    @NotNull(message="\u72b6\u6001\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotNull(message="\u72b6\u6001\u4e0d\u80fd\u4e3a\u7a7a") Integer status;

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

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CreateShkbAuthorizationProjectVo)) {
            return false;
        }
        CreateShkbAuthorizationProjectVo other = (CreateShkbAuthorizationProjectVo)o;
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
        return !(this$status == null ? other$status != null : !((Object)this$status).equals(other$status));
    }

    protected boolean canEqual(Object other) {
        return other instanceof CreateShkbAuthorizationProjectVo;
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
        String $validityUnit = this.getValidityUnit();
        result = result * 59 + ($validityUnit == null ? 43 : $validityUnit.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        Integer $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)$status).hashCode());
        return result;
    }

    public String toString() {
        return "CreateShkbAuthorizationProjectVo(projectName=" + this.getProjectName() + ", authorizationItem=" + this.getAuthorizationItem() + ", qualificationRequirement=" + this.getQualificationRequirement() + ", trainingRequirement=" + this.getTrainingRequirement() + ", validityPeriod=" + this.getValidityPeriod() + ", validityUnit=" + this.getValidityUnit() + ", description=" + this.getDescription() + ", status=" + this.getStatus() + ")";
    }
}


