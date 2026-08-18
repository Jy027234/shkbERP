package com.lframework.xingyun.shkb.bo.participant;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class GetTrainingParticipantBo
implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value="ID")
    private String id;
    @ApiModelProperty(value="\u57f9\u8bad\u5b9e\u65bdID")
    private String implementationId;
    @ApiModelProperty(value="\u5458\u5de5ID")
    private String employeeId;
    @ApiModelProperty(value="\u5458\u5de5\u5de5\u53f7")
    private String employeeCode;
    @ApiModelProperty(value="\u5458\u5de5\u59d3\u540d")
    private String employeeName;
    @ApiModelProperty(value="\u90e8\u95e8\u540d\u79f0")
    private String deptName;
    @ApiModelProperty(value="\u57f9\u8bad\u72b6\u6001")
    private Integer status;
    @ApiModelProperty(value="\u57f9\u8bad\u72b6\u6001\u6587\u672c")
    private String statusText;
    @ApiModelProperty(value="\u57f9\u8bad\u7ed3\u679c")
    private String trainingResult;
    @ApiModelProperty(value="\u8bc1\u4e66\u7f16\u53f7")
    private String certificateNo;
    @ApiModelProperty(value="\u521b\u5efa\u65f6\u95f4")
    private String createTime;

    public String getId() {
        return this.id;
    }

    public String getImplementationId() {
        return this.implementationId;
    }

    public String getEmployeeId() {
        return this.employeeId;
    }

    public String getEmployeeCode() {
        return this.employeeCode;
    }

    public String getEmployeeName() {
        return this.employeeName;
    }

    public String getDeptName() {
        return this.deptName;
    }

    public Integer getStatus() {
        return this.status;
    }

    public String getStatusText() {
        return this.statusText;
    }

    public String getTrainingResult() {
        return this.trainingResult;
    }

    public String getCertificateNo() {
        return this.certificateNo;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImplementationId(String implementationId) {
        this.implementationId = implementationId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public void setTrainingResult(String trainingResult) {
        this.trainingResult = trainingResult;
    }

    public void setCertificateNo(String certificateNo) {
        this.certificateNo = certificateNo;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GetTrainingParticipantBo)) {
            return false;
        }
        GetTrainingParticipantBo other = (GetTrainingParticipantBo)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$id = this.getId();
        String other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
            return false;
        }
        String this$implementationId = this.getImplementationId();
        String other$implementationId = other.getImplementationId();
        if (this$implementationId == null ? other$implementationId != null : !this$implementationId.equals(other$implementationId)) {
            return false;
        }
        String this$employeeId = this.getEmployeeId();
        String other$employeeId = other.getEmployeeId();
        if (this$employeeId == null ? other$employeeId != null : !this$employeeId.equals(other$employeeId)) {
            return false;
        }
        String this$employeeCode = this.getEmployeeCode();
        String other$employeeCode = other.getEmployeeCode();
        if (this$employeeCode == null ? other$employeeCode != null : !this$employeeCode.equals(other$employeeCode)) {
            return false;
        }
        String this$employeeName = this.getEmployeeName();
        String other$employeeName = other.getEmployeeName();
        if (this$employeeName == null ? other$employeeName != null : !this$employeeName.equals(other$employeeName)) {
            return false;
        }
        String this$deptName = this.getDeptName();
        String other$deptName = other.getDeptName();
        if (this$deptName == null ? other$deptName != null : !this$deptName.equals(other$deptName)) {
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
        String this$trainingResult = this.getTrainingResult();
        String other$trainingResult = other.getTrainingResult();
        if (this$trainingResult == null ? other$trainingResult != null : !this$trainingResult.equals(other$trainingResult)) {
            return false;
        }
        String this$certificateNo = this.getCertificateNo();
        String other$certificateNo = other.getCertificateNo();
        if (this$certificateNo == null ? other$certificateNo != null : !this$certificateNo.equals(other$certificateNo)) {
            return false;
        }
        String this$createTime = this.getCreateTime();
        String other$createTime = other.getCreateTime();
        return !(this$createTime == null ? other$createTime != null : !this$createTime.equals(other$createTime));
    }

    protected boolean canEqual(Object other) {
        return other instanceof GetTrainingParticipantBo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $implementationId = this.getImplementationId();
        result = result * 59 + ($implementationId == null ? 43 : $implementationId.hashCode());
        String $employeeId = this.getEmployeeId();
        result = result * 59 + ($employeeId == null ? 43 : $employeeId.hashCode());
        String $employeeCode = this.getEmployeeCode();
        result = result * 59 + ($employeeCode == null ? 43 : $employeeCode.hashCode());
        String $employeeName = this.getEmployeeName();
        result = result * 59 + ($employeeName == null ? 43 : $employeeName.hashCode());
        String $deptName = this.getDeptName();
        result = result * 59 + ($deptName == null ? 43 : $deptName.hashCode());
        Integer $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)$status).hashCode());
        String $statusText = this.getStatusText();
        result = result * 59 + ($statusText == null ? 43 : $statusText.hashCode());
        String $trainingResult = this.getTrainingResult();
        result = result * 59 + ($trainingResult == null ? 43 : $trainingResult.hashCode());
        String $certificateNo = this.getCertificateNo();
        result = result * 59 + ($certificateNo == null ? 43 : $certificateNo.hashCode());
        String $createTime = this.getCreateTime();
        result = result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
        return result;
    }

    public String toString() {
        return "GetTrainingParticipantBo(id=" + this.getId() + ", implementationId=" + this.getImplementationId() + ", employeeId=" + this.getEmployeeId() + ", employeeCode=" + this.getEmployeeCode() + ", employeeName=" + this.getEmployeeName() + ", deptName=" + this.getDeptName() + ", status=" + this.getStatus() + ", statusText=" + this.getStatusText() + ", trainingResult=" + this.getTrainingResult() + ", certificateNo=" + this.getCertificateNo() + ", createTime=" + this.getCreateTime() + ")";
    }
}


