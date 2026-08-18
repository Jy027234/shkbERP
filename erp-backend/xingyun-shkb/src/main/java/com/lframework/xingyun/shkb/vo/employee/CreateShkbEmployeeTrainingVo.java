package com.lframework.xingyun.shkb.vo.employee;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

@ApiModel(value="\u521b\u5efa\u5458\u5de5\u57f9\u8bad\u8bb0\u5f55\u8bf7\u6c42\u53c2\u6570")
public class CreateShkbEmployeeTrainingVo {
    @ApiModelProperty(value="\u5458\u5de5ID")
    @NotBlank(message="\u5458\u5de5ID\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u5458\u5de5ID\u4e0d\u80fd\u4e3a\u7a7a") String employeeId;
    @ApiModelProperty(value="\u57f9\u8bad\u540d\u79f0")
    @NotBlank(message="\u57f9\u8bad\u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u57f9\u8bad\u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a") String trainingName;
    @ApiModelProperty(value="\u57f9\u8bad\u7c7b\u578b")
    private String trainingType;
    @ApiModelProperty(value="\u57f9\u8bad\u673a\u6784")
    private String trainingOrg;
    @ApiModelProperty(value="\u57f9\u8bad\u5185\u5bb9")
    private String trainingContent;
    @ApiModelProperty(value="\u5f00\u59cb\u65e5\u671f")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    private LocalDate startDate;
    @ApiModelProperty(value="\u7ed3\u675f\u65e5\u671f")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    private LocalDate endDate;
    @ApiModelProperty(value="\u57f9\u8bad\u5b66\u65f6")
    private Integer trainingHours;
    @ApiModelProperty(value="\u57f9\u8bad\u7ed3\u679c")
    private String trainingResult;
    @ApiModelProperty(value="\u8bc1\u4e66\u7f16\u53f7")
    private String certificateNo;
    @ApiModelProperty(value="\u5907\u6ce8")
    private String description;

    public String getEmployeeId() {
        return this.employeeId;
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

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public Integer getTrainingHours() {
        return this.trainingHours;
    }

    public String getTrainingResult() {
        return this.trainingResult;
    }

    public String getCertificateNo() {
        return this.certificateNo;
    }

    public String getDescription() {
        return this.description;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
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

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setTrainingHours(Integer trainingHours) {
        this.trainingHours = trainingHours;
    }

    public void setTrainingResult(String trainingResult) {
        this.trainingResult = trainingResult;
    }

    public void setCertificateNo(String certificateNo) {
        this.certificateNo = certificateNo;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CreateShkbEmployeeTrainingVo)) {
            return false;
        }
        CreateShkbEmployeeTrainingVo other = (CreateShkbEmployeeTrainingVo)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$employeeId = this.getEmployeeId();
        String other$employeeId = other.getEmployeeId();
        if (this$employeeId == null ? other$employeeId != null : !this$employeeId.equals(other$employeeId)) {
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
        LocalDate this$startDate = this.getStartDate();
        LocalDate other$startDate = other.getStartDate();
        if (this$startDate == null ? other$startDate != null : !((Object)this$startDate).equals(other$startDate)) {
            return false;
        }
        LocalDate this$endDate = this.getEndDate();
        LocalDate other$endDate = other.getEndDate();
        if (this$endDate == null ? other$endDate != null : !((Object)this$endDate).equals(other$endDate)) {
            return false;
        }
        Integer this$trainingHours = this.getTrainingHours();
        Integer other$trainingHours = other.getTrainingHours();
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
        String this$description = this.getDescription();
        String other$description = other.getDescription();
        return !(this$description == null ? other$description != null : !this$description.equals(other$description));
    }

    protected boolean canEqual(Object other) {
        return other instanceof CreateShkbEmployeeTrainingVo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $employeeId = this.getEmployeeId();
        result = result * 59 + ($employeeId == null ? 43 : $employeeId.hashCode());
        String $trainingName = this.getTrainingName();
        result = result * 59 + ($trainingName == null ? 43 : $trainingName.hashCode());
        String $trainingType = this.getTrainingType();
        result = result * 59 + ($trainingType == null ? 43 : $trainingType.hashCode());
        String $trainingOrg = this.getTrainingOrg();
        result = result * 59 + ($trainingOrg == null ? 43 : $trainingOrg.hashCode());
        String $trainingContent = this.getTrainingContent();
        result = result * 59 + ($trainingContent == null ? 43 : $trainingContent.hashCode());
        LocalDate $startDate = this.getStartDate();
        result = result * 59 + ($startDate == null ? 43 : ((Object)$startDate).hashCode());
        LocalDate $endDate = this.getEndDate();
        result = result * 59 + ($endDate == null ? 43 : ((Object)$endDate).hashCode());
        Integer $trainingHours = this.getTrainingHours();
        result = result * 59 + ($trainingHours == null ? 43 : ((Object)$trainingHours).hashCode());
        String $trainingResult = this.getTrainingResult();
        result = result * 59 + ($trainingResult == null ? 43 : $trainingResult.hashCode());
        String $certificateNo = this.getCertificateNo();
        result = result * 59 + ($certificateNo == null ? 43 : $certificateNo.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        return result;
    }

    public String toString() {
        return "CreateShkbEmployeeTrainingVo(employeeId=" + this.getEmployeeId() + ", trainingName=" + this.getTrainingName() + ", trainingType=" + this.getTrainingType() + ", trainingOrg=" + this.getTrainingOrg() + ", trainingContent=" + this.getTrainingContent() + ", startDate=" + this.getStartDate() + ", endDate=" + this.getEndDate() + ", trainingHours=" + this.getTrainingHours() + ", trainingResult=" + this.getTrainingResult() + ", certificateNo=" + this.getCertificateNo() + ", description=" + this.getDescription() + ")";
    }
}


