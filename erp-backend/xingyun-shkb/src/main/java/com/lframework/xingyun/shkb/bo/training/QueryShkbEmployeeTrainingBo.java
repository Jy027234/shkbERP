package com.lframework.xingyun.shkb.bo.training;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;

public class QueryShkbEmployeeTrainingBo
implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value="ID")
    private String id;
    @ApiModelProperty(value="\u5458\u5de5ID")
    private String employeeId;
    @ApiModelProperty(value="\u5458\u5de5\u59d3\u540d")
    private String employeeName;
    @ApiModelProperty(value="\u57f9\u8bad\u540d\u79f0")
    private String trainingName;
    @ApiModelProperty(value="\u57f9\u8bad\u7c7b\u578b")
    private String trainingType;
    @ApiModelProperty(value="\u57f9\u8bad\u673a\u6784")
    private String trainingOrg;
    @ApiModelProperty(value="\u57f9\u8bad\u5185\u5bb9")
    private String trainingContent;
    @ApiModelProperty(value="\u5f00\u59cb\u65e5\u671f")
    private String startDate;
    @ApiModelProperty(value="\u7ed3\u675f\u65e5\u671f")
    private String endDate;
    @ApiModelProperty(value="\u57f9\u8bad\u5b66\u65f6")
    private BigDecimal trainingHours;
    @ApiModelProperty(value="\u57f9\u8bad\u7ed3\u679c")
    private String trainingResult;
    @ApiModelProperty(value="\u8bc1\u4e66\u7f16\u53f7")
    private String certificateNo;
    @ApiModelProperty(value="\u8bc1\u4e66\u6587\u4ef6URL")
    private String fileUrl;
    @ApiModelProperty(value="\u5907\u6ce8")
    private String description;
    @ApiModelProperty(value="\u521b\u5efa\u4eba")
    private String createBy;
    @ApiModelProperty(value="\u521b\u5efa\u65f6\u95f4")
    private String createTime;

    public String getId() {
        return this.id;
    }

    public String getEmployeeId() {
        return this.employeeId;
    }

    public String getEmployeeName() {
        return this.employeeName;
    }

    public String getTrainingName() {
        return this.trainingName;
    }

    public String getTrainingType() {
        return this.trainingType;
    }

    public String getTrainingOrg() {
        return this.trainingOrg;
    }

    public String getTrainingContent() {
        return this.trainingContent;
    }

    public String getStartDate() {
        return this.startDate;
    }

    public String getEndDate() {
        return this.endDate;
    }

    public BigDecimal getTrainingHours() {
        return this.trainingHours;
    }

    public String getTrainingResult() {
        return this.trainingResult;
    }

    public String getCertificateNo() {
        return this.certificateNo;
    }

    public String getFileUrl() {
        return this.fileUrl;
    }

    public String getDescription() {
        return this.description;
    }

    public String getCreateBy() {
        return this.createBy;
    }

    public String getCreateTime() {
        return this.createTime;
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

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public void setTrainingType(String trainingType) {
        this.trainingType = trainingType;
    }

    public void setTrainingOrg(String trainingOrg) {
        this.trainingOrg = trainingOrg;
    }

    public void setTrainingContent(String trainingContent) {
        this.trainingContent = trainingContent;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setTrainingHours(BigDecimal trainingHours) {
        this.trainingHours = trainingHours;
    }

    public void setTrainingResult(String trainingResult) {
        this.trainingResult = trainingResult;
    }

    public void setCertificateNo(String certificateNo) {
        this.certificateNo = certificateNo;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof QueryShkbEmployeeTrainingBo)) {
            return false;
        }
        QueryShkbEmployeeTrainingBo other = (QueryShkbEmployeeTrainingBo)o;
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
        String this$trainingName = this.getTrainingName();
        String other$trainingName = other.getTrainingName();
        if (this$trainingName == null ? other$trainingName != null : !this$trainingName.equals(other$trainingName)) {
            return false;
        }
        String this$trainingType = this.getTrainingType();
        String other$trainingType = other.getTrainingType();
        if (this$trainingType == null ? other$trainingType != null : !this$trainingType.equals(other$trainingType)) {
            return false;
        }
        String this$trainingOrg = this.getTrainingOrg();
        String other$trainingOrg = other.getTrainingOrg();
        if (this$trainingOrg == null ? other$trainingOrg != null : !this$trainingOrg.equals(other$trainingOrg)) {
            return false;
        }
        String this$trainingContent = this.getTrainingContent();
        String other$trainingContent = other.getTrainingContent();
        if (this$trainingContent == null ? other$trainingContent != null : !this$trainingContent.equals(other$trainingContent)) {
            return false;
        }
        String this$startDate = this.getStartDate();
        String other$startDate = other.getStartDate();
        if (this$startDate == null ? other$startDate != null : !this$startDate.equals(other$startDate)) {
            return false;
        }
        String this$endDate = this.getEndDate();
        String other$endDate = other.getEndDate();
        if (this$endDate == null ? other$endDate != null : !this$endDate.equals(other$endDate)) {
            return false;
        }
        BigDecimal this$trainingHours = this.getTrainingHours();
        BigDecimal other$trainingHours = other.getTrainingHours();
        if (this$trainingHours == null ? other$trainingHours != null : !((Object)this$trainingHours).equals(other$trainingHours)) {
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
        String this$createBy = this.getCreateBy();
        String other$createBy = other.getCreateBy();
        if (this$createBy == null ? other$createBy != null : !this$createBy.equals(other$createBy)) {
            return false;
        }
        String this$createTime = this.getCreateTime();
        String other$createTime = other.getCreateTime();
        return !(this$createTime == null ? other$createTime != null : !this$createTime.equals(other$createTime));
    }

    protected boolean canEqual(Object other) {
        return other instanceof QueryShkbEmployeeTrainingBo;
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
        String $trainingName = this.getTrainingName();
        result = result * 59 + ($trainingName == null ? 43 : $trainingName.hashCode());
        String $trainingType = this.getTrainingType();
        result = result * 59 + ($trainingType == null ? 43 : $trainingType.hashCode());
        String $trainingOrg = this.getTrainingOrg();
        result = result * 59 + ($trainingOrg == null ? 43 : $trainingOrg.hashCode());
        String $trainingContent = this.getTrainingContent();
        result = result * 59 + ($trainingContent == null ? 43 : $trainingContent.hashCode());
        String $startDate = this.getStartDate();
        result = result * 59 + ($startDate == null ? 43 : $startDate.hashCode());
        String $endDate = this.getEndDate();
        result = result * 59 + ($endDate == null ? 43 : $endDate.hashCode());
        BigDecimal $trainingHours = this.getTrainingHours();
        result = result * 59 + ($trainingHours == null ? 43 : ((Object)$trainingHours).hashCode());
        String $trainingResult = this.getTrainingResult();
        result = result * 59 + ($trainingResult == null ? 43 : $trainingResult.hashCode());
        String $certificateNo = this.getCertificateNo();
        result = result * 59 + ($certificateNo == null ? 43 : $certificateNo.hashCode());
        String $fileUrl = this.getFileUrl();
        result = result * 59 + ($fileUrl == null ? 43 : $fileUrl.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        String $createBy = this.getCreateBy();
        result = result * 59 + ($createBy == null ? 43 : $createBy.hashCode());
        String $createTime = this.getCreateTime();
        result = result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
        return result;
    }

    public String toString() {
        return "QueryShkbEmployeeTrainingBo(id=" + this.getId() + ", employeeId=" + this.getEmployeeId() + ", employeeName=" + this.getEmployeeName() + ", trainingName=" + this.getTrainingName() + ", trainingType=" + this.getTrainingType() + ", trainingOrg=" + this.getTrainingOrg() + ", trainingContent=" + this.getTrainingContent() + ", startDate=" + this.getStartDate() + ", endDate=" + this.getEndDate() + ", trainingHours=" + this.getTrainingHours() + ", trainingResult=" + this.getTrainingResult() + ", certificateNo=" + this.getCertificateNo() + ", fileUrl=" + this.getFileUrl() + ", description=" + this.getDescription() + ", createBy=" + this.getCreateBy() + ", createTime=" + this.getCreateTime() + ")";
    }
}


