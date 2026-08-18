package com.lframework.xingyun.shkb.vo.participant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;

@ApiModel(value="\u521b\u5efa\u57f9\u8bad\u5b66\u5458\u8bf7\u6c42\u53c2\u6570")
public class CreateTrainingParticipantVo
implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value="\u57f9\u8bad\u5b9e\u65bdID")
    @NotBlank(message="\u57f9\u8bad\u5b9e\u65bdID\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u57f9\u8bad\u5b9e\u65bdID\u4e0d\u80fd\u4e3a\u7a7a") String implementationId;
    @ApiModelProperty(value="\u5458\u5de5ID")
    @NotBlank(message="\u5458\u5de5ID\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u5458\u5de5ID\u4e0d\u80fd\u4e3a\u7a7a") String employeeId;
    @ApiModelProperty(value="\u57f9\u8bad\u7ed3\u679c")
    private String trainingResult;
    @ApiModelProperty(value="\u8bc1\u4e66\u7f16\u53f7")
    private String certificateNo;
    @ApiModelProperty(value="\u57f9\u8bad\u72b6\u6001")
    private Integer status;

    public String getImplementationId() {
        return this.implementationId;
    }

    public String getEmployeeId() {
        return this.employeeId;
    }

    public String getTrainingResult() {
        return this.trainingResult;
    }

    public String getCertificateNo() {
        return this.certificateNo;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setImplementationId(String implementationId) {
        this.implementationId = implementationId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public void setTrainingResult(String trainingResult) {
        this.trainingResult = trainingResult;
    }

    public void setCertificateNo(String certificateNo) {
        this.certificateNo = certificateNo;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CreateTrainingParticipantVo)) {
            return false;
        }
        CreateTrainingParticipantVo other = (CreateTrainingParticipantVo)o;
        if (!other.canEqual(this)) {
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
        Integer this$status = this.getStatus();
        Integer other$status = other.getStatus();
        return !(this$status == null ? other$status != null : !((Object)this$status).equals(other$status));
    }

    protected boolean canEqual(Object other) {
        return other instanceof CreateTrainingParticipantVo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $implementationId = this.getImplementationId();
        result = result * 59 + ($implementationId == null ? 43 : $implementationId.hashCode());
        String $employeeId = this.getEmployeeId();
        result = result * 59 + ($employeeId == null ? 43 : $employeeId.hashCode());
        String $trainingResult = this.getTrainingResult();
        result = result * 59 + ($trainingResult == null ? 43 : $trainingResult.hashCode());
        String $certificateNo = this.getCertificateNo();
        result = result * 59 + ($certificateNo == null ? 43 : $certificateNo.hashCode());
        Integer $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)$status).hashCode());
        return result;
    }

    public String toString() {
        return "CreateTrainingParticipantVo(implementationId=" + this.getImplementationId() + ", employeeId=" + this.getEmployeeId() + ", trainingResult=" + this.getTrainingResult() + ", certificateNo=" + this.getCertificateNo() + ", status=" + this.getStatus() + ")";
    }
}


