package com.lframework.xingyun.shkb.vo.training;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;

public class CreateTrainingImplementationVo {
    @NotBlank(message="\u8bfe\u7a0bID\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u8bfe\u7a0bID\u4e0d\u80fd\u4e3a\u7a7a") String courseId;
    private LocalDate planStartDate;
    private LocalDate planEndDate;
    private String trainingLocation;
    private String instructor;
    private String description;

    public String getCourseId() {
        return this.courseId;
    }

    public LocalDate getPlanStartDate() {
        return this.planStartDate;
    }

    public LocalDate getPlanEndDate() {
        return this.planEndDate;
    }

    public String getTrainingLocation() {
        return this.trainingLocation;
    }

    public String getInstructor() {
        return this.instructor;
    }

    public String getDescription() {
        return this.description;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setPlanStartDate(LocalDate planStartDate) {
        this.planStartDate = planStartDate;
    }

    public void setPlanEndDate(LocalDate planEndDate) {
        this.planEndDate = planEndDate;
    }

    public void setTrainingLocation(String trainingLocation) {
        this.trainingLocation = trainingLocation;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CreateTrainingImplementationVo)) {
            return false;
        }
        CreateTrainingImplementationVo other = (CreateTrainingImplementationVo)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$courseId = this.getCourseId();
        String other$courseId = other.getCourseId();
        if (this$courseId == null ? other$courseId != null : !this$courseId.equals(other$courseId)) {
            return false;
        }
        LocalDate this$planStartDate = this.getPlanStartDate();
        LocalDate other$planStartDate = other.getPlanStartDate();
        if (this$planStartDate == null ? other$planStartDate != null : !((Object)this$planStartDate).equals(other$planStartDate)) {
            return false;
        }
        LocalDate this$planEndDate = this.getPlanEndDate();
        LocalDate other$planEndDate = other.getPlanEndDate();
        if (this$planEndDate == null ? other$planEndDate != null : !((Object)this$planEndDate).equals(other$planEndDate)) {
            return false;
        }
        String this$trainingLocation = this.getTrainingLocation();
        String other$trainingLocation = other.getTrainingLocation();
        if (this$trainingLocation == null ? other$trainingLocation != null : !this$trainingLocation.equals(other$trainingLocation)) {
            return false;
        }
        String this$instructor = this.getInstructor();
        String other$instructor = other.getInstructor();
        if (this$instructor == null ? other$instructor != null : !this$instructor.equals(other$instructor)) {
            return false;
        }
        String this$description = this.getDescription();
        String other$description = other.getDescription();
        return !(this$description == null ? other$description != null : !this$description.equals(other$description));
    }

    protected boolean canEqual(Object other) {
        return other instanceof CreateTrainingImplementationVo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $courseId = this.getCourseId();
        result = result * 59 + ($courseId == null ? 43 : $courseId.hashCode());
        LocalDate $planStartDate = this.getPlanStartDate();
        result = result * 59 + ($planStartDate == null ? 43 : ((Object)$planStartDate).hashCode());
        LocalDate $planEndDate = this.getPlanEndDate();
        result = result * 59 + ($planEndDate == null ? 43 : ((Object)$planEndDate).hashCode());
        String $trainingLocation = this.getTrainingLocation();
        result = result * 59 + ($trainingLocation == null ? 43 : $trainingLocation.hashCode());
        String $instructor = this.getInstructor();
        result = result * 59 + ($instructor == null ? 43 : $instructor.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        return result;
    }

    public String toString() {
        return "CreateTrainingImplementationVo(courseId=" + this.getCourseId() + ", planStartDate=" + this.getPlanStartDate() + ", planEndDate=" + this.getPlanEndDate() + ", trainingLocation=" + this.getTrainingLocation() + ", instructor=" + this.getInstructor() + ", description=" + this.getDescription() + ")";
    }
}


