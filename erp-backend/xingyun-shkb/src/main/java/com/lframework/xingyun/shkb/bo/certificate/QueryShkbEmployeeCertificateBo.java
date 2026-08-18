package com.lframework.xingyun.shkb.bo.certificate;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class QueryShkbEmployeeCertificateBo
implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value="ID")
    private String id;
    @ApiModelProperty(value="\u5458\u5de5ID")
    private String employeeId;
    @ApiModelProperty(value="\u5458\u5de5\u59d3\u540d")
    private String employeeName;
    @ApiModelProperty(value="\u8bc1\u4e66\u7c7b\u578b")
    private String certificateType;
    @ApiModelProperty(value="\u8bc1\u4e66\u540d\u79f0")
    private String certificateName;
    @ApiModelProperty(value="\u8bc1\u4e66\u7f16\u53f7")
    private String certificateNo;
    @ApiModelProperty(value="\u53d1\u8bc1\u673a\u6784")
    private String issueOrg;
    @ApiModelProperty(value="\u53d1\u8bc1\u65e5\u671f")
    private String issueDate;
    @ApiModelProperty(value="\u6709\u6548\u671f\u5f00\u59cb")
    private String validStartDate;
    @ApiModelProperty(value="\u6709\u6548\u671f\u7ed3\u675f")
    private String validEndDate;
    @ApiModelProperty(value="\u72b6\u6001")
    private Integer status;
    @ApiModelProperty(value="\u72b6\u6001\u6587\u672c")
    private String statusText;
    @ApiModelProperty(value="\u8bc1\u4e66\u6587\u4ef6URL")
    private String fileUrl;
    @ApiModelProperty(value="\u5907\u6ce8")
    private String description;

    public String getId() {
        return this.id;
    }

    public String getEmployeeId() {
        return this.employeeId;
    }

    public String getEmployeeName() {
        return this.employeeName;
    }

    public String getCertificateType() {
        return this.certificateType;
    }

    public String getCertificateName() {
        return this.certificateName;
    }

    public String getCertificateNo() {
        return this.certificateNo;
    }

    public String getIssueOrg() {
        return this.issueOrg;
    }

    public String getIssueDate() {
        return this.issueDate;
    }

    public String getValidStartDate() {
        return this.validStartDate;
    }

    public String getValidEndDate() {
        return this.validEndDate;
    }

    public Integer getStatus() {
        return this.status;
    }

    public String getStatusText() {
        return this.statusText;
    }

    public String getFileUrl() {
        return this.fileUrl;
    }

    public String getDescription() {
        return this.description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public void setCertificateType(String certificateType) {
        this.certificateType = certificateType;
    }

    public void setCertificateName(String certificateName) {
        this.certificateName = certificateName;
    }

    public void setCertificateNo(String certificateNo) {
        this.certificateNo = certificateNo;
    }

    public void setIssueOrg(String issueOrg) {
        this.issueOrg = issueOrg;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public void setValidStartDate(String validStartDate) {
        this.validStartDate = validStartDate;
    }

    public void setValidEndDate(String validEndDate) {
        this.validEndDate = validEndDate;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof QueryShkbEmployeeCertificateBo)) {
            return false;
        }
        QueryShkbEmployeeCertificateBo other = (QueryShkbEmployeeCertificateBo)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$id = this.getId();
        String other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
            return false;
        }
        String this$employeeId = this.getEmployeeId();
        String other$employeeId = other.getEmployeeId();
        if (this$employeeId == null ? other$employeeId != null : !this$employeeId.equals(other$employeeId)) {
            return false;
        }
        String this$employeeName = this.getEmployeeName();
        String other$employeeName = other.getEmployeeName();
        if (this$employeeName == null ? other$employeeName != null : !this$employeeName.equals(other$employeeName)) {
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
        String this$certificateNo = this.getCertificateNo();
        String other$certificateNo = other.getCertificateNo();
        if (this$certificateNo == null ? other$certificateNo != null : !this$certificateNo.equals(other$certificateNo)) {
            return false;
        }
        String this$issueOrg = this.getIssueOrg();
        String other$issueOrg = other.getIssueOrg();
        if (this$issueOrg == null ? other$issueOrg != null : !this$issueOrg.equals(other$issueOrg)) {
            return false;
        }
        String this$issueDate = this.getIssueDate();
        String other$issueDate = other.getIssueDate();
        if (this$issueDate == null ? other$issueDate != null : !this$issueDate.equals(other$issueDate)) {
            return false;
        }
        String this$validStartDate = this.getValidStartDate();
        String other$validStartDate = other.getValidStartDate();
        if (this$validStartDate == null ? other$validStartDate != null : !this$validStartDate.equals(other$validStartDate)) {
            return false;
        }
        String this$validEndDate = this.getValidEndDate();
        String other$validEndDate = other.getValidEndDate();
        if (this$validEndDate == null ? other$validEndDate != null : !this$validEndDate.equals(other$validEndDate)) {
            return false;
        }
        Integer this$status = this.getStatus();
        Integer other$status = other.getStatus();
        if (this$status == null ? other$status != null : !((Object)this$status).equals(other$status)) {
            return false;
        }
        String this$statusText = this.getStatusText();
        String other$statusText = other.getStatusText();
        if (this$statusText == null ? other$statusText != null : !this$statusText.equals(other$statusText)) {
            return false;
        }
        String this$fileUrl = this.getFileUrl();
        String other$fileUrl = other.getFileUrl();
        if (this$fileUrl == null ? other$fileUrl != null : !this$fileUrl.equals(other$fileUrl)) {
            return false;
        }
        String this$description = this.getDescription();
        String other$description = other.getDescription();
        return !(this$description == null ? other$description != null : !this$description.equals(other$description));
    }

    protected boolean canEqual(Object other) {
        return other instanceof QueryShkbEmployeeCertificateBo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $employeeId = this.getEmployeeId();
        result = result * 59 + ($employeeId == null ? 43 : $employeeId.hashCode());
        String $employeeName = this.getEmployeeName();
        result = result * 59 + ($employeeName == null ? 43 : $employeeName.hashCode());
        String $certificateType = this.getCertificateType();
        result = result * 59 + ($certificateType == null ? 43 : $certificateType.hashCode());
        String $certificateName = this.getCertificateName();
        result = result * 59 + ($certificateName == null ? 43 : $certificateName.hashCode());
        String $certificateNo = this.getCertificateNo();
        result = result * 59 + ($certificateNo == null ? 43 : $certificateNo.hashCode());
        String $issueOrg = this.getIssueOrg();
        result = result * 59 + ($issueOrg == null ? 43 : $issueOrg.hashCode());
        String $issueDate = this.getIssueDate();
        result = result * 59 + ($issueDate == null ? 43 : $issueDate.hashCode());
        String $validStartDate = this.getValidStartDate();
        result = result * 59 + ($validStartDate == null ? 43 : $validStartDate.hashCode());
        String $validEndDate = this.getValidEndDate();
        result = result * 59 + ($validEndDate == null ? 43 : $validEndDate.hashCode());
        Integer $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)$status).hashCode());
        String $statusText = this.getStatusText();
        result = result * 59 + ($statusText == null ? 43 : $statusText.hashCode());
        String $fileUrl = this.getFileUrl();
        result = result * 59 + ($fileUrl == null ? 43 : $fileUrl.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        return result;
    }

    public String toString() {
        return "QueryShkbEmployeeCertificateBo(id=" + this.getId() + ", employeeId=" + this.getEmployeeId() + ", employeeName=" + this.getEmployeeName() + ", certificateType=" + this.getCertificateType() + ", certificateName=" + this.getCertificateName() + ", certificateNo=" + this.getCertificateNo() + ", issueOrg=" + this.getIssueOrg() + ", issueDate=" + this.getIssueDate() + ", validStartDate=" + this.getValidStartDate() + ", validEndDate=" + this.getValidEndDate() + ", status=" + this.getStatus() + ", statusText=" + this.getStatusText() + ", fileUrl=" + this.getFileUrl() + ", description=" + this.getDescription() + ")";
    }
}


