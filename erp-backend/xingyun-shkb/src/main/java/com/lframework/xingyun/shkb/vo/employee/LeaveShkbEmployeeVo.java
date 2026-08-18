package com.lframework.xingyun.shkb.vo.employee;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@ApiModel(value="\u5458\u5de5\u79bb\u804c\u8bf7\u6c42\u53c2\u6570")
public class LeaveShkbEmployeeVo {
    @ApiModelProperty(value="ID")
    @NotBlank(message="ID\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="ID\u4e0d\u80fd\u4e3a\u7a7a") String id;
    @ApiModelProperty(value="\u79bb\u804c\u65e5\u671f")
    @NotNull(message="\u79bb\u804c\u65e5\u671f\u4e0d\u80fd\u4e3a\u7a7a")
    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    private @NotNull(message="\u79bb\u804c\u65e5\u671f\u4e0d\u80fd\u4e3a\u7a7a") Date leaveDate;
    @ApiModelProperty(value="\u79bb\u804c\u539f\u56e0")
    @NotBlank(message="\u79bb\u804c\u539f\u56e0\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u79bb\u804c\u539f\u56e0\u4e0d\u80fd\u4e3a\u7a7a") String leaveReason;

    public String getId() {
        return this.id;
    }

    public Date getLeaveDate() {
        return this.leaveDate;
    }

    public String getLeaveReason() {
        return this.leaveReason;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLeaveDate(Date leaveDate) {
        this.leaveDate = leaveDate;
    }

    public void setLeaveReason(String leaveReason) {
        this.leaveReason = leaveReason;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LeaveShkbEmployeeVo)) {
            return false;
        }
        LeaveShkbEmployeeVo other = (LeaveShkbEmployeeVo)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$id = this.getId();
        String other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
            return false;
        }
        Date this$leaveDate = this.getLeaveDate();
        Date other$leaveDate = other.getLeaveDate();
        if (this$leaveDate == null ? other$leaveDate != null : !((Object)this$leaveDate).equals(other$leaveDate)) {
            return false;
        }
        String this$leaveReason = this.getLeaveReason();
        String other$leaveReason = other.getLeaveReason();
        return !(this$leaveReason == null ? other$leaveReason != null : !this$leaveReason.equals(other$leaveReason));
    }

    protected boolean canEqual(Object other) {
        return other instanceof LeaveShkbEmployeeVo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Date $leaveDate = this.getLeaveDate();
        result = result * 59 + ($leaveDate == null ? 43 : ((Object)$leaveDate).hashCode());
        String $leaveReason = this.getLeaveReason();
        result = result * 59 + ($leaveReason == null ? 43 : $leaveReason.hashCode());
        return result;
    }

    public String toString() {
        return "LeaveShkbEmployeeVo(id=" + this.getId() + ", leaveDate=" + this.getLeaveDate() + ", leaveReason=" + this.getLeaveReason() + ")";
    }
}


