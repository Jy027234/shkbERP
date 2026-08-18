package com.lframework.xingyun.shkb.excel.employee;

import com.alibaba.excel.annotation.ExcelProperty;
import com.lframework.starter.web.core.components.excel.ExcelModel;
import com.lframework.xingyun.shkb.bo.employee.GetShkbEmployeeBo;
import com.lframework.xingyun.shkb.bo.employee.QueryShkbEmployeeBo;

public class EmployeeExportModel
implements ExcelModel {
    @ExcelProperty(value={"\u5de5\u53f7"})
    private String code;
    @ExcelProperty(value={"\u59d3\u540d"})
    private String name;
    @ExcelProperty(value={"\u6027\u522b"})
    private String genderText;
    @ExcelProperty(value={"\u8eab\u4efd\u8bc1\u53f7"})
    private String idCard;
    @ExcelProperty(value={"\u51fa\u751f\u65e5\u671f"})
    private String birthday;
    @ExcelProperty(value={"\u6c11\u65cf"})
    private String nation;
    @ExcelProperty(value={"\u7c4d\u8d2f"})
    private String nativePlace;
    @ExcelProperty(value={"\u653f\u6cbb\u9762\u8c8c"})
    private String politicalStatus;
    @ExcelProperty(value={"\u5b66\u5386"})
    private String education;
    @ExcelProperty(value={"\u4e13\u4e1a"})
    private String major;
    @ExcelProperty(value={"\u6bd5\u4e1a\u9662\u6821"})
    private String graduateSchool;
    @ExcelProperty(value={"\u6bd5\u4e1a\u65e5\u671f"})
    private String graduateDate;
    @ExcelProperty(value={"\u8054\u7cfb\u7535\u8bdd"})
    private String phone;
    @ExcelProperty(value={"\u7535\u5b50\u90ae\u7bb1"})
    private String email;
    @ExcelProperty(value={"\u90e8\u95e8"})
    private String deptName;
    @ExcelProperty(value={"\u804c\u4f4d"})
    private String position;
    @ExcelProperty(value={"\u5165\u804c\u65e5\u671f"})
    private String entryDate;
    @ExcelProperty(value={"\u8f6c\u6b63\u65e5\u671f"})
    private String regularDate;
    @ExcelProperty(value={"\u72b6\u6001"})
    private String statusText;
    @ExcelProperty(value={"\u79bb\u804c\u65e5\u671f"})
    private String leaveDate;
    @ExcelProperty(value={"\u79bb\u804c\u539f\u56e0"})
    private String leaveReason;
    @ExcelProperty(value={"\u73b0\u5c45\u4f4f\u5730\u5740"})
    private String address;
    @ExcelProperty(value={"\u7d27\u6025\u8054\u7cfb\u4eba"})
    private String emergencyContact;
    @ExcelProperty(value={"\u7d27\u6025\u8054\u7cfb\u7535\u8bdd"})
    private String emergencyPhone;

    public EmployeeExportModel() {
    }

    public EmployeeExportModel(QueryShkbEmployeeBo bo) {
        this.code = bo.getCode();
        this.name = bo.getName();
        this.genderText = bo.getGenderText();
        this.idCard = bo.getIdCard();
        this.birthday = bo.getBirthday();
        this.nation = bo.getNation();
        this.nativePlace = bo.getNativePlace();
        this.politicalStatus = bo.getPoliticalStatus();
        this.education = bo.getEducation();
        this.major = bo.getMajor();
        this.graduateSchool = bo.getGraduateSchool();
        this.graduateDate = bo.getGraduateDate();
        this.phone = bo.getPhone();
        this.email = bo.getEmail();
        this.deptName = bo.getDeptName();
        this.position = bo.getPosition();
        this.entryDate = bo.getEntryDate();
        this.statusText = bo.getStatusText();
        this.leaveDate = bo.getLeaveDate();
        this.leaveReason = bo.getLeaveReason();
    }

    public EmployeeExportModel(GetShkbEmployeeBo bo) {
        this.code = bo.getCode();
        this.name = bo.getName();
        this.genderText = bo.getGenderText();
        this.idCard = bo.getIdCard();
        this.birthday = bo.getBirthday();
        this.nation = bo.getNation();
        this.nativePlace = bo.getNativePlace();
        this.politicalStatus = bo.getPoliticalStatus();
        this.education = bo.getEducation();
        this.major = bo.getMajor();
        this.graduateSchool = bo.getGraduateSchool();
        this.graduateDate = bo.getGraduateDate();
        this.phone = bo.getPhone();
        this.email = bo.getEmail();
        this.deptName = bo.getDeptName();
        this.position = bo.getPosition();
        this.entryDate = bo.getEntryDate();
        this.regularDate = bo.getRegularDate();
        this.statusText = bo.getStatusText();
        this.leaveDate = bo.getLeaveDate();
        this.leaveReason = bo.getLeaveReason();
        this.address = bo.getAddress();
        this.emergencyContact = bo.getEmergencyContact();
        this.emergencyPhone = bo.getEmergencyPhone();
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
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

    public String getStatusText() {
        return this.statusText;
    }

    public String getLeaveDate() {
        return this.leaveDate;
    }

    public String getLeaveReason() {
        return this.leaveReason;
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

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public void setLeaveDate(String leaveDate) {
        this.leaveDate = leaveDate;
    }

    public void setLeaveReason(String leaveReason) {
        this.leaveReason = leaveReason;
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

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EmployeeExportModel)) {
            return false;
        }
        EmployeeExportModel other = (EmployeeExportModel)o;
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
        String this$statusText = this.getStatusText();
        String other$statusText = other.getStatusText();
        if (this$statusText == null ? other$statusText != null : !this$statusText.equals(other$statusText)) {
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
        return !(this$emergencyPhone == null ? other$emergencyPhone != null : !this$emergencyPhone.equals(other$emergencyPhone));
    }

    protected boolean canEqual(Object other) {
        return other instanceof EmployeeExportModel;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $code = this.getCode();
        result = result * 59 + ($code == null ? 43 : $code.hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
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
        String $deptName = this.getDeptName();
        result = result * 59 + ($deptName == null ? 43 : $deptName.hashCode());
        String $position = this.getPosition();
        result = result * 59 + ($position == null ? 43 : $position.hashCode());
        String $entryDate = this.getEntryDate();
        result = result * 59 + ($entryDate == null ? 43 : $entryDate.hashCode());
        String $regularDate = this.getRegularDate();
        result = result * 59 + ($regularDate == null ? 43 : $regularDate.hashCode());
        String $statusText = this.getStatusText();
        result = result * 59 + ($statusText == null ? 43 : $statusText.hashCode());
        String $leaveDate = this.getLeaveDate();
        result = result * 59 + ($leaveDate == null ? 43 : $leaveDate.hashCode());
        String $leaveReason = this.getLeaveReason();
        result = result * 59 + ($leaveReason == null ? 43 : $leaveReason.hashCode());
        String $address = this.getAddress();
        result = result * 59 + ($address == null ? 43 : $address.hashCode());
        String $emergencyContact = this.getEmergencyContact();
        result = result * 59 + ($emergencyContact == null ? 43 : $emergencyContact.hashCode());
        String $emergencyPhone = this.getEmergencyPhone();
        result = result * 59 + ($emergencyPhone == null ? 43 : $emergencyPhone.hashCode());
        return result;
    }

    public String toString() {
        return "EmployeeExportModel(code=" + this.getCode() + ", name=" + this.getName() + ", genderText=" + this.getGenderText() + ", idCard=" + this.getIdCard() + ", birthday=" + this.getBirthday() + ", nation=" + this.getNation() + ", nativePlace=" + this.getNativePlace() + ", politicalStatus=" + this.getPoliticalStatus() + ", education=" + this.getEducation() + ", major=" + this.getMajor() + ", graduateSchool=" + this.getGraduateSchool() + ", graduateDate=" + this.getGraduateDate() + ", phone=" + this.getPhone() + ", email=" + this.getEmail() + ", deptName=" + this.getDeptName() + ", position=" + this.getPosition() + ", entryDate=" + this.getEntryDate() + ", regularDate=" + this.getRegularDate() + ", statusText=" + this.getStatusText() + ", leaveDate=" + this.getLeaveDate() + ", leaveReason=" + this.getLeaveReason() + ", address=" + this.getAddress() + ", emergencyContact=" + this.getEmergencyContact() + ", emergencyPhone=" + this.getEmergencyPhone() + ")";
    }
}


