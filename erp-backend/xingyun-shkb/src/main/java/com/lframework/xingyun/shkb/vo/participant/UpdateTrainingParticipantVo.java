package com.lframework.xingyun.shkb.vo.participant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;

@ApiModel(value="\u66f4\u65b0\u57f9\u8bad\u5b66\u5458\u8bf7\u6c42\u53c2\u6570")
public class UpdateTrainingParticipantVo
implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value="ID")
    @NotBlank(message="ID\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="ID\u4e0d\u80fd\u4e3a\u7a7a") String id;
    @ApiModelProperty(value="\u57f9\u8bad\u7ed3\u679c")
    private String trainingResult;
    @ApiModelProperty(value="\u8bc1\u4e66\u7f16\u53f7")
    private String certificateNo;
    @ApiModelProperty(value="\u57f9\u8bad\u72b6\u6001")
    private Integer status;

    public String getId() {
        return this.id;
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

    public void setId(String id) {
        this.id = id;
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
        if (!(o instanceof UpdateTrainingParticipantVo)) {
            return false;
        }
        UpdateTrainingParticipantVo other = (UpdateTrainingParticipantVo)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$id = this.getId();
        String other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
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
        return other instanceof UpdateTrainingParticipantVo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $trainingResult = this.getTrainingResult();
        result = result * 59 + ($trainingResult == null ? 43 : $trainingResult.hashCode());
        String $certificateNo = this.getCertificateNo();
        result = result * 59 + ($certificateNo == null ? 43 : $certificateNo.hashCode());
        Integer $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)$status).hashCode());
        return result;
    }

    public String toString() {
        return "UpdateTrainingParticipantVo(id=" + this.getId() + ", trainingResult=" + this.getTrainingResult() + ", certificateNo=" + this.getCertificateNo() + ", status=" + this.getStatus() + ")";
    }
}


