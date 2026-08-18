package com.lframework.xingyun.shkb.vo.employee;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import jakarta.validation.constraints.NotEmpty;

@ApiModel(value="\u6279\u91cf\u66f4\u65b0\u5458\u5de5\u79bb\u804c\u72b6\u6001\u8bf7\u6c42\u53c2\u6570")
public class BatchLeaveStatusShkbEmployeeVo {
    @ApiModelProperty(value="ID\u5217\u8868")
    @NotEmpty(message="ID\u5217\u8868\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotEmpty(message="ID\u5217\u8868\u4e0d\u80fd\u4e3a\u7a7a") List<String> ids;

    public List<String> getIds() {
        return this.ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BatchLeaveStatusShkbEmployeeVo)) {
            return false;
        }
        BatchLeaveStatusShkbEmployeeVo other = (BatchLeaveStatusShkbEmployeeVo)o;
        if (!other.canEqual(this)) {
            return false;
        }
        List<String> this$ids = this.getIds();
        List<String> other$ids = other.getIds();
        return !(this$ids == null ? other$ids != null : !((Object)this$ids).equals(other$ids));
    }

    protected boolean canEqual(Object other) {
        return other instanceof BatchLeaveStatusShkbEmployeeVo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        List<String> $ids = this.getIds();
        result = result * 59 + ($ids == null ? 43 : ((Object)$ids).hashCode());
        return result;
    }

    public String toString() {
        return "BatchLeaveStatusShkbEmployeeVo(ids=" + this.getIds() + ")";
    }
}


