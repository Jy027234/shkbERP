package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName(value="shkb_employee_training")
public class ShkbEmployeeTraining
extends BaseEntity
implements BaseDto {
    @TableField(exist=false)
    private static final long serialVersionUID = 1L;
    @TableId
    private String id;
    private String employeeId;
    private String trainingName;
    private String trainingType;
    private String trainingOrg;
    private String trainingContent;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal trainingHours;
    private String trainingResult;
    private String certificateNo;
    private String fileUrl;
    private String description;
    @TableField(fill=FieldFill.INSERT)
    private String createById;
    @TableField(fill=FieldFill.INSERT)
    private String createBy;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private String updateBy;
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private String updateById;
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    public String getId() {
        return this.id;
    }

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

    public String getCreateById() {
        return this.createById;
    }

    public String getCreateBy() {
        return this.createBy;
    }

    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    public String getUpdateBy() {
        return this.updateBy;
    }

    public String getUpdateById() {
        return this.updateById;
    }

    public LocalDateTime getUpdateTime() {
        return this.updateTime;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setCreateById(String createById) {
        this.createById = createById;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public void setUpdateById(String updateById) {
        this.updateById = updateById;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ShkbEmployeeTraining)) {
            return false;
        }
        ShkbEmployeeTraining other = (ShkbEmployeeTraining)((Object)o);
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (!super.equals(o)) {
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
        String this$createById = this.getCreateById();
        String other$createById = other.getCreateById();
        if (this$createById == null ? other$createById != null : !this$createById.equals(other$createById)) {
            return false;
        }
        String this$createBy = this.getCreateBy();
        String other$createBy = other.getCreateBy();
        if (this$createBy == null ? other$createBy != null : !this$createBy.equals(other$createBy)) {
            return false;
        }
        LocalDateTime this$createTime = this.getCreateTime();
        LocalDateTime other$createTime = other.getCreateTime();
        if (this$createTime == null ? other$createTime != null : !((Object)this$createTime).equals(other$createTime)) {
            return false;
        }
        String this$updateBy = this.getUpdateBy();
        String other$updateBy = other.getUpdateBy();
        if (this$updateBy == null ? other$updateBy != null : !this$updateBy.equals(other$updateBy)) {
            return false;
        }
        String this$updateById = this.getUpdateById();
        String other$updateById = other.getUpdateById();
        if (this$updateById == null ? other$updateById != null : !this$updateById.equals(other$updateById)) {
            return false;
        }
        LocalDateTime this$updateTime = this.getUpdateTime();
        LocalDateTime other$updateTime = other.getUpdateTime();
        return !(this$updateTime == null ? other$updateTime != null : !((Object)this$updateTime).equals(other$updateTime));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ShkbEmployeeTraining;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
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
        String $createById = this.getCreateById();
        result = result * 59 + ($createById == null ? 43 : $createById.hashCode());
        String $createBy = this.getCreateBy();
        result = result * 59 + ($createBy == null ? 43 : $createBy.hashCode());
        LocalDateTime $createTime = this.getCreateTime();
        result = result * 59 + ($createTime == null ? 43 : ((Object)$createTime).hashCode());
        String $updateBy = this.getUpdateBy();
        result = result * 59 + ($updateBy == null ? 43 : $updateBy.hashCode());
        String $updateById = this.getUpdateById();
        result = result * 59 + ($updateById == null ? 43 : $updateById.hashCode());
        LocalDateTime $updateTime = this.getUpdateTime();
        result = result * 59 + ($updateTime == null ? 43 : ((Object)$updateTime).hashCode());
        return result;
    }

    public String toString() {
        return "ShkbEmployeeTraining(id=" + this.getId() + ", employeeId=" + this.getEmployeeId() + ", trainingName=" + this.getTrainingName() + ", trainingType=" + this.getTrainingType() + ", trainingOrg=" + this.getTrainingOrg() + ", trainingContent=" + this.getTrainingContent() + ", startDate=" + this.getStartDate() + ", endDate=" + this.getEndDate() + ", trainingHours=" + this.getTrainingHours() + ", trainingResult=" + this.getTrainingResult() + ", certificateNo=" + this.getCertificateNo() + ", fileUrl=" + this.getFileUrl() + ", description=" + this.getDescription() + ", createById=" + this.getCreateById() + ", createBy=" + this.getCreateBy() + ", createTime=" + this.getCreateTime() + ", updateBy=" + this.getUpdateBy() + ", updateById=" + this.getUpdateById() + ", updateTime=" + this.getUpdateTime() + ")";
    }
}
