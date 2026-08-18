package com.lframework.xingyun.shkb.vo.training;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;

@ApiModel(value="\u4fee\u6539\u57f9\u8bad\u8bfe\u7a0b\u8bf7\u6c42\u53c2\u6570")
public class UpdateShkbTrainingCourseVo {
    @ApiModelProperty(value="ID")
    @NotBlank(message="ID\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="ID\u4e0d\u80fd\u4e3a\u7a7a") String id;
    @ApiModelProperty(value="\u8bfe\u7a0b\u540d\u79f0")
    @NotBlank(message="\u8bfe\u7a0b\u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u8bfe\u7a0b\u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a") String courseName;
    @ApiModelProperty(value="\u8bfe\u7a0b\u7c7b\u578b")
    private String courseType;
    @ApiModelProperty(value="\u5b9e\u65bd\u95f4\u9694\u6570\u503c")
    private Integer implementationInterval;
    @ApiModelProperty(value="\u95f4\u9694\u5355\u4f4d")
    private String intervalUnit;
    @ApiModelProperty(value="\u8bfe\u7a0b\u63cf\u8ff0")
    private String description;
    @ApiModelProperty(value="\u521d\u8bad\u65f6\u957f(h)")
    private Integer initialTrainingHours;
    @ApiModelProperty(value="\u590d\u8bad\u65f6\u957f(h)")
    private Integer retrainingHours;
    @ApiModelProperty(value="\u6559\u5b66\u65b9\u5f0f")
    private String teachingMethod;
    @ApiModelProperty(value="\u53c2\u8bad\u4eba\u5458")
    private String participants;
    @ApiModelProperty(value="\u6559\u5458")
    private String instructor;
    @ApiModelProperty(value="\u8003\u6838\u65b9\u5f0f")
    private String assessmentMethod;
    @ApiModelProperty(value="\u57f9\u8bad\u63d0\u7eb2")
    private String trainingOutline;
    @ApiModelProperty(value="\u72b6\u6001")
    private Integer status;

    public String getId() {
        return this.id;
    }

    public String getCourseName() {
        return this.courseName;
    }

    public String getCourseType() {
        return this.courseType;
    }

    public Integer getImplementationInterval() {
        return this.implementationInterval;
    }

    public String getIntervalUnit() {
        return this.intervalUnit;
    }

    public String getDescription() {
        return this.description;
    }

    public Integer getInitialTrainingHours() {
        return this.initialTrainingHours;
    }

    public Integer getRetrainingHours() {
        return this.retrainingHours;
    }

    public String getTeachingMethod() {
        return this.teachingMethod;
    }

    public String getParticipants() {
        return this.participants;
    }

    public String getInstructor() {
        return this.instructor;
    }

    public String getAssessmentMethod() {
        return this.assessmentMethod;
    }

    public String getTrainingOutline() {
        return this.trainingOutline;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public void setImplementationInterval(Integer implementationInterval) {
        this.implementationInterval = implementationInterval;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setInitialTrainingHours(Integer initialTrainingHours) {
        this.initialTrainingHours = initialTrainingHours;
    }

    public void setRetrainingHours(Integer retrainingHours) {
        this.retrainingHours = retrainingHours;
    }

    public void setTeachingMethod(String teachingMethod) {
        this.teachingMethod = teachingMethod;
    }

    public void setParticipants(String participants) {
        this.participants = participants;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public void setAssessmentMethod(String assessmentMethod) {
        this.assessmentMethod = assessmentMethod;
    }

    public void setTrainingOutline(String trainingOutline) {
        this.trainingOutline = trainingOutline;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UpdateShkbTrainingCourseVo)) {
            return false;
        }
        UpdateShkbTrainingCourseVo other = (UpdateShkbTrainingCourseVo)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$id = this.getId();
        String other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
            return false;
        }
        String this$courseName = this.getCourseName();
        String other$courseName = other.getCourseName();
        if (this$courseName == null ? other$courseName != null : !this$courseName.equals(other$courseName)) {
            return false;
        }
        String this$courseType = this.getCourseType();
        String other$courseType = other.getCourseType();
        if (this$courseType == null ? other$courseType != null : !this$courseType.equals(other$courseType)) {
            return false;
        }
        Integer this$implementationInterval = this.getImplementationInterval();
        Integer other$implementationInterval = other.getImplementationInterval();
        if (this$implementationInterval == null ? other$implementationInterval != null : !((Object)this$implementationInterval).equals(other$implementationInterval)) {
            return false;
        }
        String this$intervalUnit = this.getIntervalUnit();
        String other$intervalUnit = other.getIntervalUnit();
        if (this$intervalUnit == null ? other$intervalUnit != null : !this$intervalUnit.equals(other$intervalUnit)) {
            return false;
        }
        String this$description = this.getDescription();
        String other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description)) {
            return false;
        }
        Integer this$initialTrainingHours = this.getInitialTrainingHours();
        Integer other$initialTrainingHours = other.getInitialTrainingHours();
        if (this$initialTrainingHours == null ? other$initialTrainingHours != null : !((Object)this$initialTrainingHours).equals(other$initialTrainingHours)) {
            return false;
        }
        Integer this$retrainingHours = this.getRetrainingHours();
        Integer other$retrainingHours = other.getRetrainingHours();
        if (this$retrainingHours == null ? other$retrainingHours != null : !((Object)this$retrainingHours).equals(other$retrainingHours)) {
            return false;
        }
        String this$teachingMethod = this.getTeachingMethod();
        String other$teachingMethod = other.getTeachingMethod();
        if (this$teachingMethod == null ? other$teachingMethod != null : !this$teachingMethod.equals(other$teachingMethod)) {
            return false;
        }
        String this$participants = this.getParticipants();
        String other$participants = other.getParticipants();
        if (this$participants == null ? other$participants != null : !this$participants.equals(other$participants)) {
            return false;
        }
        String this$instructor = this.getInstructor();
        String other$instructor = other.getInstructor();
        if (this$instructor == null ? other$instructor != null : !this$instructor.equals(other$instructor)) {
            return false;
        }
        String this$assessmentMethod = this.getAssessmentMethod();
        String other$assessmentMethod = other.getAssessmentMethod();
        if (this$assessmentMethod == null ? other$assessmentMethod != null : !this$assessmentMethod.equals(other$assessmentMethod)) {
            return false;
        }
        String this$trainingOutline = this.getTrainingOutline();
        String other$trainingOutline = other.getTrainingOutline();
        if (this$trainingOutline == null ? other$trainingOutline != null : !this$trainingOutline.equals(other$trainingOutline)) {
            return false;
        }
        Integer this$status = this.getStatus();
        Integer other$status = other.getStatus();
        return !(this$status == null ? other$status != null : !((Object)this$status).equals(other$status));
    }

    protected boolean canEqual(Object other) {
        return other instanceof UpdateShkbTrainingCourseVo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $courseName = this.getCourseName();
        result = result * 59 + ($courseName == null ? 43 : $courseName.hashCode());
        String $courseType = this.getCourseType();
        result = result * 59 + ($courseType == null ? 43 : $courseType.hashCode());
        Integer $implementationInterval = this.getImplementationInterval();
        result = result * 59 + ($implementationInterval == null ? 43 : ((Object)$implementationInterval).hashCode());
        String $intervalUnit = this.getIntervalUnit();
        result = result * 59 + ($intervalUnit == null ? 43 : $intervalUnit.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        Integer $initialTrainingHours = this.getInitialTrainingHours();
        result = result * 59 + ($initialTrainingHours == null ? 43 : ((Object)$initialTrainingHours).hashCode());
        Integer $retrainingHours = this.getRetrainingHours();
        result = result * 59 + ($retrainingHours == null ? 43 : ((Object)$retrainingHours).hashCode());
        String $teachingMethod = this.getTeachingMethod();
        result = result * 59 + ($teachingMethod == null ? 43 : $teachingMethod.hashCode());
        String $participants = this.getParticipants();
        result = result * 59 + ($participants == null ? 43 : $participants.hashCode());
        String $instructor = this.getInstructor();
        result = result * 59 + ($instructor == null ? 43 : $instructor.hashCode());
        String $assessmentMethod = this.getAssessmentMethod();
        result = result * 59 + ($assessmentMethod == null ? 43 : $assessmentMethod.hashCode());
        String $trainingOutline = this.getTrainingOutline();
        result = result * 59 + ($trainingOutline == null ? 43 : $trainingOutline.hashCode());
        Integer $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)$status).hashCode());
        return result;
    }

    public String toString() {
        return "UpdateShkbTrainingCourseVo(id=" + this.getId() + ", courseName=" + this.getCourseName() + ", courseType=" + this.getCourseType() + ", implementationInterval=" + this.getImplementationInterval() + ", intervalUnit=" + this.getIntervalUnit() + ", description=" + this.getDescription() + ", initialTrainingHours=" + this.getInitialTrainingHours() + ", retrainingHours=" + this.getRetrainingHours() + ", teachingMethod=" + this.getTeachingMethod() + ", participants=" + this.getParticipants() + ", instructor=" + this.getInstructor() + ", assessmentMethod=" + this.getAssessmentMethod() + ", trainingOutline=" + this.getTrainingOutline() + ", status=" + this.getStatus() + ")";
    }
}


