package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import java.time.LocalDateTime;
import java.util.Date;

@TableName(value="shkb_employee")
public class ShkbEmployee
extends BaseEntity
implements BaseDto {
    @TableField(exist=false)
    private static final long serialVersionUID = 1L;
    @TableId
    private String id;
    private String code;
    private String name;
    private Integer gender;
    private String idCard;
    private Date birthday;
    private String nation;
    private String nativePlace;
    private String politicalStatus;
    private String education;
    private String major;
    private String graduateSchool;
    private Date graduateDate;
    private String phone;
    private String email;
    private String address;
    private String emergencyContact;
    private String emergencyPhone;
    private String deptId;
    private String position;
    private Date entryDate;
    private Date regularDate;
    private Date leaveDate;
    private String leaveReason;
    private Integer status;
    private String photoUrl;
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

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public Integer getGender() {
        return this.gender;
    }

    public String getIdCard() {
        return this.idCard;
    }

    public Date getBirthday() {
        return this.birthday;
    }

    public String getNation() {
        return this.nation;
    }

    public String getNativePlace() {
        return this.nativePlace;
    }

    public String getPoliticalStatus() {
        return this.politicalStatus;
    }

    public String getEducation() {
        return this.education;
    }

    public String getMajor() {
        return this.major;
    }

    public String getGraduateSchool() {
        return this.graduateSchool;
    }

    public Date getGraduateDate() {
        return this.graduateDate;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getEmail() {
        return this.email;
    }

    public String getAddress() {
        return this.address;
    }

    public String getEmergencyContact() {
        return this.emergencyContact;
    }

    public String getEmergencyPhone() {
        return this.emergencyPhone;
    }

    public String getDeptId() {
        return this.deptId;
    }

    public String getPosition() {
        return this.position;
    }

    public Date getEntryDate() {
        return this.entryDate;
    }

    public Date getRegularDate() {
        return this.regularDate;
    }

    public Date getLeaveDate() {
        return this.leaveDate;
    }

    public String getLeaveReason() {
        return this.leaveReason;
    }

    public Integer getStatus() {
        return this.status;
    }

    public String getPhotoUrl() {
        return this.photoUrl;
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

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public void setNativePlace(String nativePlace) {
        this.nativePlace = nativePlace;
    }

    public void setPoliticalStatus(String politicalStatus) {
        this.politicalStatus = politicalStatus;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setGraduateSchool(String graduateSchool) {
        this.graduateSchool = graduateSchool;
    }

    public void setGraduateDate(Date graduateDate) {
        this.graduateDate = graduateDate;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public void setRegularDate(Date regularDate) {
        this.regularDate = regularDate;
    }

    public void setLeaveDate(Date leaveDate) {
        this.leaveDate = leaveDate;
    }

    public void setLeaveReason(String leaveReason) {
        this.leaveReason = leaveReason;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
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
        if (!(o instanceof ShkbEmployee)) {
            return false;
        }
        ShkbEmployee other = (ShkbEmployee)((Object)o);
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
        String this$code = this.getCode();
        String other$code = other.getCode();
        if (this$code == null ? other$code != null : !this$code.equals(other$code)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        Integer this$gender = this.getGender();
        Integer other$gender = other.getGender();
        if (this$gender == null ? other$gender != null : !((Object)this$gender).equals(other$gender)) {
            return false;
        }
        String this$idCard = this.getIdCard();
        String other$idCard = other.getIdCard();
        if (this$idCard == null ? other$idCard != null : !this$idCard.equals(other$idCard)) {
            return false;
        }
        Date this$birthday = this.getBirthday();
        Date other$birthday = other.getBirthday();
        if (this$birthday == null ? other$birthday != null : !((Object)this$birthday).equals(other$birthday)) {
            return false;
        }
        String this$nation = this.getNation();
        String other$nation = other.getNation();
        if (this$nation == null ? other$nation != null : !this$nation.equals(other$nation)) {
            return false;
        }
        String this$nativePlace = this.getNativePlace();
        String other$nativePlace = other.getNativePlace();
        if (this$nativePlace == null ? other$nativePlace != null : !this$nativePlace.equals(other$nativePlace)) {
            return false;
        }
        String this$politicalStatus = this.getPoliticalStatus();
        String other$politicalStatus = other.getPoliticalStatus();
        if (this$politicalStatus == null ? other$politicalStatus != null : !this$politicalStatus.equals(other$politicalStatus)) {
            return false;
        }
        String this$education = this.getEducation();
        String other$education = other.getEducation();
        if (this$education == null ? other$education != null : !this$education.equals(other$education)) {
            return false;
        }
        String this$major = this.getMajor();
        String other$major = other.getMajor();
        if (this$major == null ? other$major != null : !this$major.equals(other$major)) {
            return false;
        }
        String this$graduateSchool = this.getGraduateSchool();
        String other$graduateSchool = other.getGraduateSchool();
        if (this$graduateSchool == null ? other$graduateSchool != null : !this$graduateSchool.equals(other$graduateSchool)) {
            return false;
        }
        Date this$graduateDate = this.getGraduateDate();
        Date other$graduateDate = other.getGraduateDate();
        if (this$graduateDate == null ? other$graduateDate != null : !((Object)this$graduateDate).equals(other$graduateDate)) {
            return false;
        }
        String this$phone = this.getPhone();
        String other$phone = other.getPhone();
        if (this$phone == null ? other$phone != null : !this$phone.equals(other$phone)) {
            return false;
        }
        String this$email = this.getEmail();
        String other$email = other.getEmail();
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) {
            return false;
        }
        String this$address = this.getAddress();
        String other$address = other.getAddress();
        if (this$address == null ? other$address != null : !this$address.equals(other$address)) {
            return false;
        }
        String this$emergencyContact = this.getEmergencyContact();
        String other$emergencyContact = other.getEmergencyContact();
        if (this$emergencyContact == null ? other$emergencyContact != null : !this$emergencyContact.equals(other$emergencyContact)) {
            return false;
        }
        String this$emergencyPhone = this.getEmergencyPhone();
        String other$emergencyPhone = other.getEmergencyPhone();
        if (this$emergencyPhone == null ? other$emergencyPhone != null : !this$emergencyPhone.equals(other$emergencyPhone)) {
            return false;
        }
        String this$deptId = this.getDeptId();
        String other$deptId = other.getDeptId();
        if (this$deptId == null ? other$deptId != null : !this$deptId.equals(other$deptId)) {
            return false;
        }
        String this$position = this.getPosition();
        String other$position = other.getPosition();
        if (this$position == null ? other$position != null : !this$position.equals(other$position)) {
            return false;
        }
        Date this$entryDate = this.getEntryDate();
        Date other$entryDate = other.getEntryDate();
        if (this$entryDate == null ? other$entryDate != null : !((Object)this$entryDate).equals(other$entryDate)) {
            return false;
        }
        Date this$regularDate = this.getRegularDate();
        Date other$regularDate = other.getRegularDate();
        if (this$regularDate == null ? other$regularDate != null : !((Object)this$regularDate).equals(other$regularDate)) {
            return false;
        }
        Date this$leaveDate = this.getLeaveDate();
        Date other$leaveDate = other.getLeaveDate();
        if (this$leaveDate == null ? other$leaveDate != null : !((Object)this$leaveDate).equals(other$leaveDate)) {
            return false;
        }
        String this$leaveReason = this.getLeaveReason();
        String other$leaveReason = other.getLeaveReason();
        if (this$leaveReason == null ? other$leaveReason != null : !this$leaveReason.equals(other$leaveReason)) {
            return false;
        }
        Integer this$status = this.getStatus();
        Integer other$status = other.getStatus();
        if (this$status == null ? other$status != null : !((Object)this$status).equals(other$status)) {
            return false;
        }
        String this$photoUrl = this.getPhotoUrl();
        String other$photoUrl = other.getPhotoUrl();
        if (this$photoUrl == null ? other$photoUrl != null : !this$photoUrl.equals(other$photoUrl)) {
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
        return other instanceof ShkbEmployee;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $code = this.getCode();
        result = result * 59 + ($code == null ? 43 : $code.hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Integer $gender = this.getGender();
        result = result * 59 + ($gender == null ? 43 : ((Object)$gender).hashCode());
        String $idCard = this.getIdCard();
        result = result * 59 + ($idCard == null ? 43 : $idCard.hashCode());
        Date $birthday = this.getBirthday();
        result = result * 59 + ($birthday == null ? 43 : ((Object)$birthday).hashCode());
        String $nation = this.getNation();
        result = result * 59 + ($nation == null ? 43 : $nation.hashCode());
        String $nativePlace = this.getNativePlace();
        result = result * 59 + ($nativePlace == null ? 43 : $nativePlace.hashCode());
        String $politicalStatus = this.getPoliticalStatus();
        result = result * 59 + ($politicalStatus == null ? 43 : $politicalStatus.hashCode());
        String $education = this.getEducation();
        result = result * 59 + ($education == null ? 43 : $education.hashCode());
        String $major = this.getMajor();
        result = result * 59 + ($major == null ? 43 : $major.hashCode());
        String $graduateSchool = this.getGraduateSchool();
        result = result * 59 + ($graduateSchool == null ? 43 : $graduateSchool.hashCode());
        Date $graduateDate = this.getGraduateDate();
        result = result * 59 + ($graduateDate == null ? 43 : ((Object)$graduateDate).hashCode());
        String $phone = this.getPhone();
        result = result * 59 + ($phone == null ? 43 : $phone.hashCode());
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        String $address = this.getAddress();
        result = result * 59 + ($address == null ? 43 : $address.hashCode());
        String $emergencyContact = this.getEmergencyContact();
        result = result * 59 + ($emergencyContact == null ? 43 : $emergencyContact.hashCode());
        String $emergencyPhone = this.getEmergencyPhone();
        result = result * 59 + ($emergencyPhone == null ? 43 : $emergencyPhone.hashCode());
        String $deptId = this.getDeptId();
        result = result * 59 + ($deptId == null ? 43 : $deptId.hashCode());
        String $position = this.getPosition();
        result = result * 59 + ($position == null ? 43 : $position.hashCode());
        Date $entryDate = this.getEntryDate();
        result = result * 59 + ($entryDate == null ? 43 : ((Object)$entryDate).hashCode());
        Date $regularDate = this.getRegularDate();
        result = result * 59 + ($regularDate == null ? 43 : ((Object)$regularDate).hashCode());
        Date $leaveDate = this.getLeaveDate();
        result = result * 59 + ($leaveDate == null ? 43 : ((Object)$leaveDate).hashCode());
        String $leaveReason = this.getLeaveReason();
        result = result * 59 + ($leaveReason == null ? 43 : $leaveReason.hashCode());
        Integer $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)$status).hashCode());
        String $photoUrl = this.getPhotoUrl();
        result = result * 59 + ($photoUrl == null ? 43 : $photoUrl.hashCode());
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
        return "ShkbEmployee(id=" + this.getId() + ", code=" + this.getCode() + ", name=" + this.getName() + ", gender=" + this.getGender() + ", idCard=" + this.getIdCard() + ", birthday=" + this.getBirthday() + ", nation=" + this.getNation() + ", nativePlace=" + this.getNativePlace() + ", politicalStatus=" + this.getPoliticalStatus() + ", education=" + this.getEducation() + ", major=" + this.getMajor() + ", graduateSchool=" + this.getGraduateSchool() + ", graduateDate=" + this.getGraduateDate() + ", phone=" + this.getPhone() + ", email=" + this.getEmail() + ", address=" + this.getAddress() + ", emergencyContact=" + this.getEmergencyContact() + ", emergencyPhone=" + this.getEmergencyPhone() + ", deptId=" + this.getDeptId() + ", position=" + this.getPosition() + ", entryDate=" + this.getEntryDate() + ", regularDate=" + this.getRegularDate() + ", leaveDate=" + this.getLeaveDate() + ", leaveReason=" + this.getLeaveReason() + ", status=" + this.getStatus() + ", photoUrl=" + this.getPhotoUrl() + ", description=" + this.getDescription() + ", createById=" + this.getCreateById() + ", createBy=" + this.getCreateBy() + ", createTime=" + this.getCreateTime() + ", updateBy=" + this.getUpdateBy() + ", updateById=" + this.getUpdateById() + ", updateTime=" + this.getUpdateTime() + ")";
    }
}
