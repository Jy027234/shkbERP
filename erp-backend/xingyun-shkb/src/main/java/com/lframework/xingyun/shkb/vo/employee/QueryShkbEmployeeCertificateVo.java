package com.lframework.xingyun.shkb.vo.employee;

import com.lframework.starter.web.core.vo.PageVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="\u67e5\u8be2\u5458\u5de5\u8bc1\u4e66\u8bf7\u6c42\u53c2\u6570")
public class QueryShkbEmployeeCertificateVo
extends PageVo {
    @ApiModelProperty(value="ID\u96c6\u5408")
    private String ids;
    @ApiModelProperty(value="\u5173\u952e\u5b57\uff08\u5458\u5de5\u59d3\u540d/\u8bc1\u4e66\u540d\u79f0/\u8bc1\u4e66\u7f16\u53f7\uff09")
    private String keyword;
    @ApiModelProperty(value="\u5458\u5de5ID")
    private String employeeId;
    @ApiModelProperty(value="\u8bc1\u4e66\u7c7b\u578b")
    private String certificateType;
    @ApiModelProperty(value="\u8bc1\u4e66\u540d\u79f0")
    private String certificateName;
    @ApiModelProperty(value="\u8bc1\u4e66\u72b6\u6001")
    private Integer status;

    public String getIds() {
        return this.ids;
    }

    public String getKeyword() {
        return this.keyword;
    }

    public String getEmployeeId() {
        return this.employeeId;
    }

    public String getCertificateType() {
        return this.certificateType;
    }

    public String getCertificateName() {
        return this.certificateName;
    }

    public Integer getStatus() {
        return this.status;
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

    public void setCertificateType(String certificateType) {
        this.certificateType = certificateType;
    }

    public void setCertificateName(String certificateName) {
        this.certificateName = certificateName;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof QueryShkbEmployeeCertificateVo)) {
            return false;
        }
        QueryShkbEmployeeCertificateVo other = (QueryShkbEmployeeCertificateVo)((Object)o);
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
        String this$certificateType = this.getCertificateType();
        String other$certificateType = other.getCertificateType();
        if (this$certificateType == null ? other$certificateType != null : !this$certificateType.equals(other$certificateType)) {
            return false;
        }
        String this$certificateName = this.getCertificateName();
        String other$certificateName = other.getCertificateName();
        if (this$certificateName == null ? other$certificateName != null : !this$certificateName.equals(other$certificateName)) {
            return false;
        }
        Integer this$status = this.getStatus();
        Integer other$status = other.getStatus();
        return !(this$status == null ? other$status != null : !((Object)this$status).equals(other$status));
    }

    protected boolean canEqual(Object other) {
        return other instanceof QueryShkbEmployeeCertificateVo;
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
        String $certificateType = this.getCertificateType();
        result = result * 59 + ($certificateType == null ? 43 : $certificateType.hashCode());
        String $certificateName = this.getCertificateName();
        result = result * 59 + ($certificateName == null ? 43 : $certificateName.hashCode());
        Integer $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)$status).hashCode());
        return result;
    }

    public String toString() {
        return "QueryShkbEmployeeCertificateVo(ids=" + this.getIds() + ", keyword=" + this.getKeyword() + ", employeeId=" + this.getEmployeeId() + ", certificateType=" + this.getCertificateType() + ", certificateName=" + this.getCertificateName() + ", status=" + this.getStatus() + ")";
    }
}


