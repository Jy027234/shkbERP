package com.lframework.xingyun.shkb.vo.employee;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;

@ApiModel(value="\u5458\u5de5\u79bb\u804c\u72b6\u6001\u8bf7\u6c42\u53c2\u6570")
public class LeaveStatusShkbEmployeeVo {
    @ApiModelProperty(value="ID")
    @NotBlank(message="ID\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="ID\u4e0d\u80fd\u4e3a\u7a7a") String id;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LeaveStatusShkbEmployeeVo)) {
            return false;
        }
        LeaveStatusShkbEmployeeVo other = (LeaveStatusShkbEmployeeVo)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$id = this.getId();
        String other$id = other.getId();
        return !(this$id == null ? other$id != null : !this$id.equals(other$id));
    }

    protected boolean canEqual(Object other) {
        return other instanceof LeaveStatusShkbEmployeeVo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        return result;
    }

    public String toString() {
        return "LeaveStatusShkbEmployeeVo(id=" + this.getId() + ")";
    }
}


