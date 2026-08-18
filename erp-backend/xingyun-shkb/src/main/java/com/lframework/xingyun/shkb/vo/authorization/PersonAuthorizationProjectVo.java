package com.lframework.xingyun.shkb.vo.authorization;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

@ApiModel(value="\u4eba\u5458\u6388\u6743\u9879\u76ee\u4fe1\u606f")
public class PersonAuthorizationProjectVo {
    @ApiModelProperty(value="\u9879\u76eeID")
    @NotBlank(message="\u9879\u76eeID\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u9879\u76eeID\u4e0d\u80fd\u4e3a\u7a7a") String projectId;
    @ApiModelProperty(value="\u9879\u76ee\u540d\u79f0")
    private String projectName;
    @ApiModelProperty(value="\u6388\u6743\u65e5\u671f")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    private LocalDate authorizationDate;
    @ApiModelProperty(value="\u5230\u671f\u65e5\u671f")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    private LocalDate expiryDate;

    public String getProjectId() {
        return this.projectId;
    }

    public String getProjectName() {
        return this.projectName;
    }

    public LocalDate getAuthorizationDate() {
        return this.authorizationDate;
    }

    public LocalDate getExpiryDate() {
        return this.expiryDate;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setAuthorizationDate(LocalDate authorizationDate) {
        this.authorizationDate = authorizationDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PersonAuthorizationProjectVo)) {
            return false;
        }
        PersonAuthorizationProjectVo other = (PersonAuthorizationProjectVo)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$projectId = this.getProjectId();
        String other$projectId = other.getProjectId();
        if (this$projectId == null ? other$projectId != null : !this$projectId.equals(other$projectId)) {
            return false;
        }
        String this$projectName = this.getProjectName();
        String other$projectName = other.getProjectName();
        if (this$projectName == null ? other$projectName != null : !this$projectName.equals(other$projectName)) {
            return false;
        }
        LocalDate this$authorizationDate = this.getAuthorizationDate();
        LocalDate other$authorizationDate = other.getAuthorizationDate();
        if (this$authorizationDate == null ? other$authorizationDate != null : !((Object)this$authorizationDate).equals(other$authorizationDate)) {
            return false;
        }
        LocalDate this$expiryDate = this.getExpiryDate();
        LocalDate other$expiryDate = other.getExpiryDate();
        return !(this$expiryDate == null ? other$expiryDate != null : !((Object)this$expiryDate).equals(other$expiryDate));
    }

    protected boolean canEqual(Object other) {
        return other instanceof PersonAuthorizationProjectVo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $projectId = this.getProjectId();
        result = result * 59 + ($projectId == null ? 43 : $projectId.hashCode());
        String $projectName = this.getProjectName();
        result = result * 59 + ($projectName == null ? 43 : $projectName.hashCode());
        LocalDate $authorizationDate = this.getAuthorizationDate();
        result = result * 59 + ($authorizationDate == null ? 43 : ((Object)$authorizationDate).hashCode());
        LocalDate $expiryDate = this.getExpiryDate();
        result = result * 59 + ($expiryDate == null ? 43 : ((Object)$expiryDate).hashCode());
        return result;
    }

    public String toString() {
        return "PersonAuthorizationProjectVo(projectId=" + this.getProjectId() + ", projectName=" + this.getProjectName() + ", authorizationDate=" + this.getAuthorizationDate() + ", expiryDate=" + this.getExpiryDate() + ")";
    }
}


