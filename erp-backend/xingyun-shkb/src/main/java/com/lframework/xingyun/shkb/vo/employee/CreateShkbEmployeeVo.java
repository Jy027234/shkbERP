package com.lframework.xingyun.shkb.vo.employee;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

@ApiModel(value="\u521b\u5efa\u5458\u5de5\u8bf7\u6c42\u53c2\u6570")
public class CreateShkbEmployeeVo {
    @ApiModelProperty(value="\u5458\u5de5\u5de5\u53f7")
    @NotBlank(message="\u5458\u5de5\u5de5\u53f7\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u5458\u5de5\u5de5\u53f7\u4e0d\u80fd\u4e3a\u7a7a") String code;
    @ApiModelProperty(value="\u59d3\u540d")
    @NotBlank(message="\u59d3\u540d\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u59d3\u540d\u4e0d\u80fd\u4e3a\u7a7a") String name;
    @ApiModelProperty(value="\u6027\u522b")
    @NotNull(message="\u6027\u522b\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotNull(message="\u6027\u522b\u4e0d\u80fd\u4e3a\u7a7a") Integer gender;
    @ApiModelProperty(value="\u8eab\u4efd\u8bc1\u53f7")
    private String idCard;
    @ApiModelProperty(value="\u51fa\u751f\u65e5\u671f")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    private Date birthday;
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
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    private Date graduateDate;
    @ApiModelProperty(value="\u8054\u7cfb\u7535\u8bdd")
    private String phone;
    @ApiModelProperty(value="\u7535\u5b50\u90ae\u7bb1")
    private String email;
    @ApiModelProperty(value="\u73b0\u5c45\u4f4f\u5730\u5740")
    private String address;
    @ApiModelProperty(value="\u7d27\u6025\u8054\u7cfb\u4eba")
    private String emergencyContact;
    @ApiModelProperty(value="\u7d27\u6025\u8054\u7cfb\u7535\u8bdd")
    private String emergencyPhone;
    @ApiModelProperty(value="\u90e8\u95e8ID")
    private String deptId;
    @ApiModelProperty(value="\u804c\u4f4d")
    private String position;
    @ApiModelProperty(value="\u5165\u804c\u65e5\u671f")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    private Date entryDate;
    @ApiModelProperty(value="\u8f6c\u6b63\u65e5\u671f")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    private Date regularDate;
    @ApiModelProperty(value="\u72b6\u6001")
    @NotNull(message="\u72b6\u6001\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotNull(message="\u72b6\u6001\u4e0d\u80fd\u4e3a\u7a7a") Integer status;
    @ApiModelProperty(value="\u7167\u7247URL")
    private String photoUrl;
    @ApiModelProperty(value="\u5907\u6ce8")
    private String description;

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

    public Integer getStatus() {
        return this.status;
    }

    public String getPhotoUrl() {
        return this.photoUrl;
    }

    public String getDescription() {
        return this.description;
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

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CreateShkbEmployeeVo)) {
            return false;
        }
        CreateShkbEmployeeVo other = (CreateShkbEmployeeVo)o;
        if (!other.canEqual(this)) {
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
        return !(this$description == null ? other$description != null : !this$description.equals(other$description));
    }

    protected boolean canEqual(Object other) {
        return other instanceof CreateShkbEmployeeVo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
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
        Integer $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)$status).hashCode());
        String $photoUrl = this.getPhotoUrl();
        result = result * 59 + ($photoUrl == null ? 43 : $photoUrl.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        return result;
    }

    public String toString() {
        return "CreateShkbEmployeeVo(code=" + this.getCode() + ", name=" + this.getName() + ", gender=" + this.getGender() + ", idCard=" + this.getIdCard() + ", birthday=" + this.getBirthday() + ", nation=" + this.getNation() + ", nativePlace=" + this.getNativePlace() + ", politicalStatus=" + this.getPoliticalStatus() + ", education=" + this.getEducation() + ", major=" + this.getMajor() + ", graduateSchool=" + this.getGraduateSchool() + ", graduateDate=" + this.getGraduateDate() + ", phone=" + this.getPhone() + ", email=" + this.getEmail() + ", address=" + this.getAddress() + ", emergencyContact=" + this.getEmergencyContact() + ", emergencyPhone=" + this.getEmergencyPhone() + ", deptId=" + this.getDeptId() + ", position=" + this.getPosition() + ", entryDate=" + this.getEntryDate() + ", regularDate=" + this.getRegularDate() + ", status=" + this.getStatus() + ", photoUrl=" + this.getPhotoUrl() + ", description=" + this.getDescription() + ")";
    }
}


