package com.lframework.xingyun.shkb.vo.authorization;

import com.lframework.xingyun.shkb.vo.authorization.PersonAuthorizationProjectVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import jakarta.validation.constraints.NotBlank;

@ApiModel(value="\u521b\u5efa\u4eba\u5458\u6388\u6743\u8bf7\u6c42\u53c2\u6570")
public class CreateShkbPersonAuthorizationVo {
    @ApiModelProperty(value="\u5458\u5de5ID")
    @NotBlank(message="\u5458\u5de5ID\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u5458\u5de5ID\u4e0d\u80fd\u4e3a\u7a7a") String employeeId;
    @ApiModelProperty(value="\u63cf\u8ff0")
    private String description;
    @ApiModelProperty(value="\u6388\u6743\u9879\u76ee\u5217\u8868")
    private List<PersonAuthorizationProjectVo> projects;

    public String getEmployeeId() {
        return this.employeeId;
    }

    public String getDescription() {
        return this.description;
    }

    public List<PersonAuthorizationProjectVo> getProjects() {
        return this.projects;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProjects(List<PersonAuthorizationProjectVo> projects) {
        this.projects = projects;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CreateShkbPersonAuthorizationVo)) {
            return false;
        }
        CreateShkbPersonAuthorizationVo other = (CreateShkbPersonAuthorizationVo)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$employeeId = this.getEmployeeId();
        String other$employeeId = other.getEmployeeId();
        if (this$employeeId == null ? other$employeeId != null : !this$employeeId.equals(other$employeeId)) {
            return false;
        }
        String this$description = this.getDescription();
        String other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description)) {
            return false;
        }
        List<PersonAuthorizationProjectVo> this$projects = this.getProjects();
        List<PersonAuthorizationProjectVo> other$projects = other.getProjects();
        return !(this$projects == null ? other$projects != null : !((Object)this$projects).equals(other$projects));
    }

    protected boolean canEqual(Object other) {
        return other instanceof CreateShkbPersonAuthorizationVo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $employeeId = this.getEmployeeId();
        result = result * 59 + ($employeeId == null ? 43 : $employeeId.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        List<PersonAuthorizationProjectVo> $projects = this.getProjects();
        result = result * 59 + ($projects == null ? 43 : ((Object)$projects).hashCode());
        return result;
    }

    public String toString() {
        return "CreateShkbPersonAuthorizationVo(employeeId=" + this.getEmployeeId() + ", description=" + this.getDescription() + ", projects=" + this.getProjects() + ")";
    }
}


