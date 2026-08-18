package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName(value="shkb_employee_certificate")
public class ShkbEmployeeCertificate
extends BaseEntity
implements BaseDto {
    @TableField(exist=false)
    private static final long serialVersionUID = 1L;
    @TableId
    private String id;
    private String employeeId;
    private String certificateType;
    private String certificateName;
    private String certificateNo;
    private String issueOrg;
    private LocalDate issueDate;
    private LocalDate validStartDate;
    private LocalDate validEndDate;
    private Integer status;
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

    public Integer getStatus() {
        return this.status;
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

    public void setStatus(Integer status) {
        this.status = status;
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
        if (!(o instanceof ShkbEmployeeCertificate)) {
            return false;
        }
        ShkbEmployeeCertificate other = (ShkbEmployeeCertificate)((Object)o);
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
        Integer this$status = this.getStatus();
        Integer other$status = other.getStatus();
        if (this$status == null ? other$status != null : !((Object)this$status).equals(other$status)) {
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
        return other instanceof ShkbEmployeeCertificate;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
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
        Integer $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)$status).hashCode());
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
        return "ShkbEmployeeCertificate(id=" + this.getId() + ", employeeId=" + this.getEmployeeId() + ", certificateType=" + this.getCertificateType() + ", certificateName=" + this.getCertificateName() + ", certificateNo=" + this.getCertificateNo() + ", issueOrg=" + this.getIssueOrg() + ", issueDate=" + this.getIssueDate() + ", validStartDate=" + this.getValidStartDate() + ", validEndDate=" + this.getValidEndDate() + ", status=" + this.getStatus() + ", fileUrl=" + this.getFileUrl() + ", description=" + this.getDescription() + ", createById=" + this.getCreateById() + ", createBy=" + this.getCreateBy() + ", createTime=" + this.getCreateTime() + ", updateBy=" + this.getUpdateBy() + ", updateById=" + this.getUpdateById() + ", updateTime=" + this.getUpdateTime() + ")";
    }
}
