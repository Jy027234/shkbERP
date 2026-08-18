package com.lframework.xingyun.shkb.vo.employee;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

@ApiModel(value="\u4fee\u6539\u5458\u5de5\u8bc1\u4e66\u8bf7\u6c42\u53c2\u6570")
public class UpdateShkbEmployeeCertificateVo {
    @ApiModelProperty(value="ID")
    @NotBlank(message="ID\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="ID\u4e0d\u80fd\u4e3a\u7a7a") String id;
    @ApiModelProperty(value="\u5458\u5de5ID")
    @NotBlank(message="\u5458\u5de5ID\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u5458\u5de5ID\u4e0d\u80fd\u4e3a\u7a7a") String employeeId;
    @ApiModelProperty(value="\u8bc1\u4e66\u7c7b\u578b")
    @NotBlank(message="\u8bc1\u4e66\u7c7b\u578b\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u8bc1\u4e66\u7c7b\u578b\u4e0d\u80fd\u4e3a\u7a7a") String certificateType;
    @ApiModelProperty(value="\u8bc1\u4e66\u540d\u79f0")
    @NotBlank(message="\u8bc1\u4e66\u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u8bc1\u4e66\u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a") String certificateName;
    @ApiModelProperty(value="\u8bc1\u4e66\u7f16\u53f7")
    private String certificateNo;
    @ApiModelProperty(value="\u53d1\u8bc1\u673a\u6784")
    private String issueOrg;
    @ApiModelProperty(value="\u53d1\u8bc1\u65e5\u671f")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    private LocalDate issueDate;
    @ApiModelProperty(value="\u6709\u6548\u671f\u5f00\u59cb")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    private LocalDate validStartDate;
    @ApiModelProperty(value="\u6709\u6548\u671f\u7ed3\u675f")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    private LocalDate validEndDate;
    @ApiModelProperty(value="\u8bc1\u4e66\u626b\u63cf\u4ef6URL")
    private String fileUrl;
    @ApiModelProperty(value="\u5907\u6ce8")
    private String description;
    @ApiModelProperty(value="\u72b6\u6001")
    private Integer status;

    public String getId() {
        return this.id;
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

    public String getCertificateNo() {
        return this.certificateNo;
    }

    public String getIssueOrg() {
        return this.issueOrg;
    }

    public LocalDate getIssueDate() {
        return this.issueDate;
    }

    public LocalDate getValidStartDate() {
        return this.validStartDate;
    }

    public LocalDate getValidEndDate() {
        return this.validEndDate;
    }

    public String getFileUrl() {
        return this.fileUrl;
    }

    public String getDescription() {
        return this.description;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setCertificateNo(String certificateNo) {
        this.certificateNo = certificateNo;
    }

    public void setIssueOrg(String issueOrg) {
        this.issueOrg = issueOrg;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public void setValidStartDate(LocalDate validStartDate) {
        this.validStartDate = validStartDate;
    }

    public void setValidEndDate(LocalDate validEndDate) {
        this.validEndDate = validEndDate;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UpdateShkbEmployeeCertificateVo)) {
            return false;
        }
        UpdateShkbEmployeeCertificateVo other = (UpdateShkbEmployeeCertificateVo)o;
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
        LocalDate this$issueDate = this.getIssueDate();
        LocalDate other$issueDate = other.getIssueDate();
        if (this$issueDate == null ? other$issueDate != null : !((Object)this$issueDate).equals(other$issueDate)) {
            return false;
        }
        LocalDate this$validStartDate = this.getValidStartDate();
        LocalDate other$validStartDate = other.getValidStartDate();
        if (this$validStartDate == null ? other$validStartDate != null : !((Object)this$validStartDate).equals(other$validStartDate)) {
            return false;
        }
        LocalDate this$validEndDate = this.getValidEndDate();
        LocalDate other$validEndDate = other.getValidEndDate();
        if (this$validEndDate == null ? other$validEndDate != null : !((Object)this$validEndDate).equals(other$validEndDate)) {
            return false;
        }
        String this$fileUrl = this.getFileUrl();
        String other$fileUrl = other.getFileUrl();
        if (this$fileUrl == null ? other$fileUrl != null : !this$fileUrl.equals(other$fileUrl)) {
            return false;
        }
        String this$description = this.getDescription();
        String other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description)) {
            return false;
        }
        Integer this$status = this.getStatus();
        Integer other$status = other.getStatus();
        return !(this$status == null ? other$status != null : !((Object)this$status).equals(other$status));
    }

    protected boolean canEqual(Object other) {
        return other instanceof UpdateShkbEmployeeCertificateVo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $employeeId = this.getEmployeeId();
        result = result * 59 + ($employeeId == null ? 43 : $employeeId.hashCode());
        String $certificateType = this.getCertificateType();
        result = result * 59 + ($certificateType == null ? 43 : $certificateType.hashCode());
        String $certificateName = this.getCertificateName();
        result = result * 59 + ($certificateName == null ? 43 : $certificateName.hashCode());
        String $certificateNo = this.getCertificateNo();
        result = result * 59 + ($certificateNo == null ? 43 : $certificateNo.hashCode());
        String $issueOrg = this.getIssueOrg();
        result = result * 59 + ($issueOrg == null ? 43 : $issueOrg.hashCode());
        LocalDate $issueDate = this.getIssueDate();
        result = result * 59 + ($issueDate == null ? 43 : ((Object)$issueDate).hashCode());
        LocalDate $validStartDate = this.getValidStartDate();
        result = result * 59 + ($validStartDate == null ? 43 : ((Object)$validStartDate).hashCode());
        LocalDate $validEndDate = this.getValidEndDate();
        result = result * 59 + ($validEndDate == null ? 43 : ((Object)$validEndDate).hashCode());
        String $fileUrl = this.getFileUrl();
        result = result * 59 + ($fileUrl == null ? 43 : $fileUrl.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        Integer $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)$status).hashCode());
        return result;
    }

    public String toString() {
        return "UpdateShkbEmployeeCertificateVo(id=" + this.getId() + ", employeeId=" + this.getEmployeeId() + ", certificateType=" + this.getCertificateType() + ", certificateName=" + this.getCertificateName() + ", certificateNo=" + this.getCertificateNo() + ", issueOrg=" + this.getIssueOrg() + ", issueDate=" + this.getIssueDate() + ", validStartDate=" + this.getValidStartDate() + ", validEndDate=" + this.getValidEndDate() + ", fileUrl=" + this.getFileUrl() + ", description=" + this.getDescription() + ", status=" + this.getStatus() + ")";
    }
}


