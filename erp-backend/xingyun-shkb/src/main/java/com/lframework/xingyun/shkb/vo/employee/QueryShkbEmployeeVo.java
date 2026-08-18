package com.lframework.xingyun.shkb.vo.employee;

import com.lframework.starter.web.core.vo.PageVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="\u67e5\u8be2\u5458\u5de5\u8bf7\u6c42\u53c2\u6570")
public class QueryShkbEmployeeVo
extends PageVo {
    @ApiModelProperty(value="\u5173\u952e\u5b57\uff08\u5de5\u53f7/\u59d3\u540d/\u7535\u8bdd\uff09")
    private String keyword;
    @ApiModelProperty(value="\u5458\u5de5\u7f16\u53f7")
    private String code;
    @ApiModelProperty(value="\u5458\u5de5\u59d3\u540d")
    private String name;
    @ApiModelProperty(value="\u624b\u673a\u53f7\u7801")
    private String phone;
    @ApiModelProperty(value="\u90e8\u95e8ID")
    private String deptId;
    @ApiModelProperty(value="\u72b6\u6001")
    private Integer status;
    @ApiModelProperty(value="ID\u5217\u8868\uff0c\u9017\u53f7\u5206\u9694")
    private String ids;

    public String getKeyword() {
        return this.keyword;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getDeptId() {
        return this.deptId;
    }

    public Integer getStatus() {
        return this.status;
    }

    public String getIds() {
        return this.ids;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof QueryShkbEmployeeVo)) {
            return false;
        }
        QueryShkbEmployeeVo other = (QueryShkbEmployeeVo)((Object)o);
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        String this$keyword = this.getKeyword();
        String other$keyword = other.getKeyword();
        if (this$keyword == null ? other$keyword != null : !this$keyword.equals(other$keyword)) {
            return false;
        }
        String this$code = this.getCode();
        String other$code = other.getCode();
        if (this$code == null ? other$code != null : !this$code.equals(other$code)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        String this$phone = this.getPhone();
        String other$phone = other.getPhone();
        if (this$phone == null ? other$phone != null : !this$phone.equals(other$phone)) {
            return false;
        }
        String this$deptId = this.getDeptId();
        String other$deptId = other.getDeptId();
        if (this$deptId == null ? other$deptId != null : !this$deptId.equals(other$deptId)) {
            return false;
        }
        Integer this$status = this.getStatus();
        Integer other$status = other.getStatus();
        if (this$status == null ? other$status != null : !((Object)this$status).equals(other$status)) {
            return false;
        }
        String this$ids = this.getIds();
        String other$ids = other.getIds();
        return !(this$ids == null ? other$ids != null : !this$ids.equals(other$ids));
    }

    protected boolean canEqual(Object other) {
        return other instanceof QueryShkbEmployeeVo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        String $keyword = this.getKeyword();
        result = result * 59 + ($keyword == null ? 43 : $keyword.hashCode());
        String $code = this.getCode();
        result = result * 59 + ($code == null ? 43 : $code.hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $phone = this.getPhone();
        result = result * 59 + ($phone == null ? 43 : $phone.hashCode());
        String $deptId = this.getDeptId();
        result = result * 59 + ($deptId == null ? 43 : $deptId.hashCode());
        Integer $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)$status).hashCode());
        String $ids = this.getIds();
        result = result * 59 + ($ids == null ? 43 : $ids.hashCode());
        return result;
    }

    public String toString() {
        return "QueryShkbEmployeeVo(keyword=" + this.getKeyword() + ", code=" + this.getCode() + ", name=" + this.getName() + ", phone=" + this.getPhone() + ", deptId=" + this.getDeptId() + ", status=" + this.getStatus() + ", ids=" + this.getIds() + ")";
    }
}


