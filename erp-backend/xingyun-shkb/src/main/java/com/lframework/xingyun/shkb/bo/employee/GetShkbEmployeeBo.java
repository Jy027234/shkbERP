package com.lframework.xingyun.shkb.bo.employee;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class GetShkbEmployeeBo
implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value="ID")
    private String id;
    @ApiModelProperty(value="\u5458\u5de5\u5de5\u53f7")
    private String code;
    @ApiModelProperty(value="\u59d3\u540d")
    private String name;
    @ApiModelProperty(value="\u6027\u522b")
    private Integer gender;
    @ApiModelProperty(value="\u6027\u522b\u6587\u672c")
    private String genderText;
    @ApiModelProperty(value="\u8eab\u4efd\u8bc1\u53f7")
    private String idCard;
    @ApiModelProperty(value="\u51fa\u751f\u65e5\u671f")
    private String birthday;
    @ApiModelProperty(value="\u6c11\u65cf")
    private String nation;
    @ApiModelProperty(value="\u7c4d\u8d2f")
    private String nativePlace;
    @ApiModelProperty(value="\u653f\u6cbb\u9762\u8c8c")
    private String politicalStatus;
    @ApiModelProperty(value="\u5b66\u5386")
    private String education;
    @ApiModelProperty(value="\u4e13\u4e1a")
    private String major;
    @ApiModelProperty(value="\u6bd5\u4e1a\u9662\u6821")
    private String graduateSchool;
    @ApiModelProperty(value="\u6bd5\u4e1a\u65e5\u671f")
    private String graduateDate;
    @ApiModelProperty(value="\u8054\u7cfb\u7535\u8bdd")
    private String phone;
    @ApiModelProperty(value="\u7535\u5b50\u90ae\u7bb1")
    private String email;
    @ApiModelProperty(value="\u90e8\u95e8ID")
    private String deptId;
    @ApiModelProperty(value="\u90e8\u95e8\u540d\u79f0")
    private String deptName;
    @ApiModelProperty(value="\u804c\u4f4d")
    private String position;
    @ApiModelProperty(value="\u5165\u804c\u65e5\u671f")
    private String entryDate;
    @ApiModelProperty(value="\u8f6c\u6b63\u65e5\u671f")
    private String regularDate;
    @ApiModelProperty(value="\u79bb\u804c\u65e5\u671f")
    private String leaveDate;
    @ApiModelProperty(value="\u79bb\u804c\u539f\u56e0")
    private String leaveReason;
    @ApiModelProperty(value="\u72b6\u6001")
    private Integer status;
    @ApiModelProperty(value="\u72b6\u6001\u6587\u672c")
    private String statusText;
    @ApiModelProperty(value="\u7167\u7247URL")
    private String photoUrl;
    @ApiModelProperty(value="\u73b0\u5c45\u4f4f\u5730\u5740")
    private String address;
    @ApiModelProperty(value="\u7d27\u6025\u8054\u7cfb\u4eba")
    private String emergencyContact;
    @ApiModelProperty(value="\u7d27\u6025\u8054\u7cfb\u7535\u8bdd")
    private String emergencyPhone;
    @ApiModelProperty(value="\u5907\u6ce8")
    private String description;
    @ApiModelProperty(value="\u521b\u5efa\u4eba")
    private String createBy;
    @ApiModelProperty(value="\u521b\u5efa\u65f6\u95f4")
    private String createTime;

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

    public String getGenderText() {
        return this.genderText;
    }

    public String getIdCard() {
        return this.idCard;
    }

    public String getBirthday() {
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

    public String getGraduateDate() {
        return this.graduateDate;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getEmail() {
        return this.email;
    }

    public String getDeptId() {
        return this.deptId;
    }

    public String getDeptName() {
        return this.deptName;
    }

    public String getPosition() {
        return this.position;
    }

    public String getEntryDate() {
        return this.entryDate;
    }

    public String getRegularDate() {
        return this.regularDate;
    }

    public String getLeaveDate() {
        return this.leaveDate;
    }

    public String getLeaveReason() {
        return this.leaveReason;
    }

    public Integer getStatus() {
        return this.status;
    }

    public String getStatusText() {
        return this.statusText;
    }

    public String getPhotoUrl() {
        return this.photoUrl;
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

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public void setGenderText(String genderText) {
        this.genderText = genderText;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public void setBirthday(String birthday) {
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

    public void setGraduateDate(String graduateDate) {
        this.graduateDate = graduateDate;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }

    public void setRegularDate(String regularDate) {
        this.regularDate = regularDate;
    }

    public void setLeaveDate(String leaveDate) {
        this.leaveDate = leaveDate;
    }

    public void setLeaveReason(String leaveReason) {
        this.leaveReason = leaveReason;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
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
        if (!(o instanceof GetShkbEmployeeBo)) {
            return false;
        }
        GetShkbEmployeeBo other = (GetShkbEmployeeBo)o;
        if (!other.canEqual(this)) {
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
        String this$genderText = this.getGenderText();
        String other$genderText = other.getGenderText();
        if (this$genderText == null ? other$genderText != null : !this$genderText.equals(other$genderText)) {
            return false;
        }
        String this$idCard = this.getIdCard();
        String other$idCard = other.getIdCard();
        if (this$idCard == null ? other$idCard != null : !this$idCard.equals(other$idCard)) {
            return false;
        }
        String this$birthday = this.getBirthday();
        String other$birthday = other.getBirthday();
        if (this$birthday == null ? other$birthday != null : !this$birthday.equals(other$birthday)) {
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
        String this$graduateDate = this.getGraduateDate();
        String other$graduateDate = other.getGraduateDate();
        if (this$graduateDate == null ? other$graduateDate != null : !this$graduateDate.equals(other$graduateDate)) {
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
        String this$deptId = this.getDeptId();
        String other$deptId = other.getDeptId();
        if (this$deptId == null ? other$deptId != null : !this$deptId.equals(other$deptId)) {
            return false;
        }
        String this$deptName = this.getDeptName();
        String other$deptName = other.getDeptName();
        if (this$deptName == null ? other$deptName != null : !this$deptName.equals(other$deptName)) {
            return false;
        }
        String this$position = this.getPosition();
        String other$position = other.getPosition();
        if (this$position == null ? other$position != null : !this$position.equals(other$position)) {
            return false;
        }
        String this$entryDate = this.getEntryDate();
        String other$entryDate = other.getEntryDate();
        if (this$entryDate == null ? other$entryDate != null : !this$entryDate.equals(other$entryDate)) {
            return false;
        }
        String this$regularDate = this.getRegularDate();
        String other$regularDate = other.getRegularDate();
        if (this$regularDate == null ? other$regularDate != null : !this$regularDate.equals(other$regularDate)) {
            return false;
        }
        String this$leaveDate = this.getLeaveDate();
        String other$leaveDate = other.getLeaveDate();
        if (this$leaveDate == null ? other$leaveDate != null : !this$leaveDate.equals(other$leaveDate)) {
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
        String this$statusText = this.getStatusText();
        String other$statusText = other.getStatusText();
        if (this$statusText == null ? other$statusText != null : !this$statusText.equals(other$statusText)) {
            return false;
        }
        String this$photoUrl = this.getPhotoUrl();
        String other$photoUrl = other.getPhotoUrl();
        if (this$photoUrl == null ? other$photoUrl != null : !this$photoUrl.equals(other$photoUrl)) {
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
        return other instanceof GetShkbEmployeeBo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $code = this.getCode();
        result = result * 59 + ($code == null ? 43 : $code.hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Integer $gender = this.getGender();
        result = result * 59 + ($gender == null ? 43 : ((Object)$gender).hashCode());
        String $genderText = this.getGenderText();
        result = result * 59 + ($genderText == null ? 43 : $genderText.hashCode());
        String $idCard = this.getIdCard();
        result = result * 59 + ($idCard == null ? 43 : $idCard.hashCode());
        String $birthday = this.getBirthday();
        result = result * 59 + ($birthday == null ? 43 : $birthday.hashCode());
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
        String $graduateDate = this.getGraduateDate();
        result = result * 59 + ($graduateDate == null ? 43 : $graduateDate.hashCode());
        String $phone = this.getPhone();
        result = result * 59 + ($phone == null ? 43 : $phone.hashCode());
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        String $deptId = this.getDeptId();
        result = result * 59 + ($deptId == null ? 43 : $deptId.hashCode());
        String $deptName = this.getDeptName();
        result = result * 59 + ($deptName == null ? 43 : $deptName.hashCode());
        String $position = this.getPosition();
        result = result * 59 + ($position == null ? 43 : $position.hashCode());
        String $entryDate = this.getEntryDate();
        result = result * 59 + ($entryDate == null ? 43 : $entryDate.hashCode());
        String $regularDate = this.getRegularDate();
        result = result * 59 + ($regularDate == null ? 43 : $regularDate.hashCode());
        String $leaveDate = this.getLeaveDate();
        result = result * 59 + ($leaveDate == null ? 43 : $leaveDate.hashCode());
        String $leaveReason = this.getLeaveReason();
        result = result * 59 + ($leaveReason == null ? 43 : $leaveReason.hashCode());
        Integer $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)$status).hashCode());
        String $statusText = this.getStatusText();
        result = result * 59 + ($statusText == null ? 43 : $statusText.hashCode());
        String $photoUrl = this.getPhotoUrl();
        result = result * 59 + ($photoUrl == null ? 43 : $photoUrl.hashCode());
        String $address = this.getAddress();
        result = result * 59 + ($address == null ? 43 : $address.hashCode());
        String $emergencyContact = this.getEmergencyContact();
        result = result * 59 + ($emergencyContact == null ? 43 : $emergencyContact.hashCode());
        String $emergencyPhone = this.getEmergencyPhone();
        result = result * 59 + ($emergencyPhone == null ? 43 : $emergencyPhone.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        String $createBy = this.getCreateBy();
        result = result * 59 + ($createBy == null ? 43 : $createBy.hashCode());
        String $createTime = this.getCreateTime();
        result = result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
        return result;
    }

    public String toString() {
        return "GetShkbEmployeeBo(id=" + this.getId() + ", code=" + this.getCode() + ", name=" + this.getName() + ", gender=" + this.getGender() + ", genderText=" + this.getGenderText() + ", idCard=" + this.getIdCard() + ", birthday=" + this.getBirthday() + ", nation=" + this.getNation() + ", nativePlace=" + this.getNativePlace() + ", politicalStatus=" + this.getPoliticalStatus() + ", education=" + this.getEducation() + ", major=" + this.getMajor() + ", graduateSchool=" + this.getGraduateSchool() + ", graduateDate=" + this.getGraduateDate() + ", phone=" + this.getPhone() + ", email=" + this.getEmail() + ", deptId=" + this.getDeptId() + ", deptName=" + this.getDeptName() + ", position=" + this.getPosition() + ", entryDate=" + this.getEntryDate() + ", regularDate=" + this.getRegularDate() + ", leaveDate=" + this.getLeaveDate() + ", leaveReason=" + this.getLeaveReason() + ", status=" + this.getStatus() + ", statusText=" + this.getStatusText() + ", photoUrl=" + this.getPhotoUrl() + ", address=" + this.getAddress() + ", emergencyContact=" + this.getEmergencyContact() + ", emergencyPhone=" + this.getEmergencyPhone() + ", description=" + this.getDescription() + ", createBy=" + this.getCreateBy() + ", createTime=" + this.getCreateTime() + ")";
    }
}


