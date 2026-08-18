package com.lframework.xingyun.shkb.vo.authorization;

import com.lframework.starter.web.core.vo.PageVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="\u67e5\u8be2\u4eba\u5458\u6388\u6743\u8bf7\u6c42\u53c2\u6570")
public class QueryShkbPersonAuthorizationVo
extends PageVo {
    @ApiModelProperty(value="\u5458\u5de5\u59d3\u540d")
    private String employeeName;
    @ApiModelProperty(value="\u5c97\u4f4d\u540d\u79f0")
    private String projectName;
    @ApiModelProperty(value="\u72b6\u6001")
    private Integer status;

    public String getEmployeeName() {
        return this.employeeName;
    }

    public String getProjectName() {
        return this.projectName;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof QueryShkbPersonAuthorizationVo)) {
            return false;
        }
        QueryShkbPersonAuthorizationVo other = (QueryShkbPersonAuthorizationVo)((Object)o);
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        String this$employeeName = this.getEmployeeName();
        String other$employeeName = other.getEmployeeName();
        if (this$employeeName == null ? other$employeeName != null : !this$employeeName.equals(other$employeeName)) {
            return false;
        }
        String this$projectName = this.getProjectName();
        String other$projectName = other.getProjectName();
        if (this$projectName == null ? other$projectName != null : !this$projectName.equals(other$projectName)) {
            return false;
        }
        Integer this$status = this.getStatus();
        Integer other$status = other.getStatus();
        return !(this$status == null ? other$status != null : !((Object)this$status).equals(other$status));
    }

    protected boolean canEqual(Object other) {
        return other instanceof QueryShkbPersonAuthorizationVo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        String $employeeName = this.getEmployeeName();
        result = result * 59 + ($employeeName == null ? 43 : $employeeName.hashCode());
        String $projectName = this.getProjectName();
        result = result * 59 + ($projectName == null ? 43 : $projectName.hashCode());
        Integer $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)$status).hashCode());
        return result;
    }

    public String toString() {
        return "QueryShkbPersonAuthorizationVo(employeeName=" + this.getEmployeeName() + ", projectName=" + this.getProjectName() + ", status=" + this.getStatus() + ")";
    }
}


