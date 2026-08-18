package com.lframework.xingyun.shkb.vo.employee;

import com.lframework.starter.web.core.vo.PageVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="\u67e5\u8be2\u5458\u5de5\u57f9\u8bad\u8bb0\u5f55\u8bf7\u6c42\u53c2\u6570")
public class QueryShkbEmployeeTrainingVo
extends PageVo {
    @ApiModelProperty(value="ID\u96c6\u5408")
    private String ids;
    @ApiModelProperty(value="\u5173\u952e\u5b57\uff08\u57f9\u8bad\u540d\u79f0/\u57f9\u8bad\u7c7b\u578b\uff09")
    private String keyword;
    @ApiModelProperty(value="\u5458\u5de5ID")
    private String employeeId;
    @ApiModelProperty(value="\u57f9\u8bad\u7c7b\u578b")
    private String trainingType;

    public String getIds() {
        return this.ids;
    }

    public String getKeyword() {
        return this.keyword;
    }

    public String getEmployeeId() {
        return this.employeeId;
    }

    public String getTrainingType() {
        return this.trainingType;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public void setTrainingType(String trainingType) {
        this.trainingType = trainingType;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof QueryShkbEmployeeTrainingVo)) {
            return false;
        }
        QueryShkbEmployeeTrainingVo other = (QueryShkbEmployeeTrainingVo)((Object)o);
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        String this$ids = this.getIds();
        String other$ids = other.getIds();
        if (this$ids == null ? other$ids != null : !this$ids.equals(other$ids)) {
            return false;
        }
        String this$keyword = this.getKeyword();
        String other$keyword = other.getKeyword();
        if (this$keyword == null ? other$keyword != null : !this$keyword.equals(other$keyword)) {
            return false;
        }
        String this$employeeId = this.getEmployeeId();
        String other$employeeId = other.getEmployeeId();
        if (this$employeeId == null ? other$employeeId != null : !this$employeeId.equals(other$employeeId)) {
            return false;
        }
        String this$trainingType = this.getTrainingType();
        String other$trainingType = other.getTrainingType();
        return !(this$trainingType == null ? other$trainingType != null : !this$trainingType.equals(other$trainingType));
    }

    protected boolean canEqual(Object other) {
        return other instanceof QueryShkbEmployeeTrainingVo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        String $ids = this.getIds();
        result = result * 59 + ($ids == null ? 43 : $ids.hashCode());
        String $keyword = this.getKeyword();
        result = result * 59 + ($keyword == null ? 43 : $keyword.hashCode());
        String $employeeId = this.getEmployeeId();
        result = result * 59 + ($employeeId == null ? 43 : $employeeId.hashCode());
        String $trainingType = this.getTrainingType();
        result = result * 59 + ($trainingType == null ? 43 : $trainingType.hashCode());
        return result;
    }

    public String toString() {
        return "QueryShkbEmployeeTrainingVo(ids=" + this.getIds() + ", keyword=" + this.getKeyword() + ", employeeId=" + this.getEmployeeId() + ", trainingType=" + this.getTrainingType() + ")";
    }
}


