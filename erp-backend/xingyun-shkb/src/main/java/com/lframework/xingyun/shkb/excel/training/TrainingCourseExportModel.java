package com.lframework.xingyun.shkb.excel.training;

import com.alibaba.excel.annotation.ExcelProperty;
import com.lframework.starter.web.core.components.excel.ExcelModel;
import com.lframework.xingyun.shkb.entity.ShkbTrainingCourse;
import java.time.format.DateTimeFormatter;

public class TrainingCourseExportModel
implements ExcelModel {
    @ExcelProperty(value={"\u8bfe\u7a0b\u540d\u79f0"})
    private String courseName;
    @ExcelProperty(value={"\u8bfe\u7a0b\u7c7b\u578b"})
    private String courseType;
    @ExcelProperty(value={"\u5b9e\u65bd\u95f4\u9694"})
    private String implementationInterval;
    @ExcelProperty(value={"\u521d\u8bad\u65f6\u957f(h)"})
    private Integer initialTrainingHours;
    @ExcelProperty(value={"\u590d\u8bad\u65f6\u957f(h)"})
    private Integer retrainingHours;
    @ExcelProperty(value={"\u6559\u5b66\u65b9\u5f0f"})
    private String teachingMethod;
    @ExcelProperty(value={"\u53c2\u8bad\u4eba\u5458"})
    private String participants;
    @ExcelProperty(value={"\u6559\u5458"})
    private String instructor;
    @ExcelProperty(value={"\u8003\u6838\u65b9\u5f0f"})
    private String assessmentMethod;
    @ExcelProperty(value={"\u57f9\u8bad\u63d0\u7eb2"})
    private String trainingOutline;
    @ExcelProperty(value={"\u72b6\u6001"})
    private String status;
    @ExcelProperty(value={"\u521b\u5efa\u65f6\u95f4"})
    private String createTime;

    public TrainingCourseExportModel() {
    }

    public TrainingCourseExportModel(ShkbTrainingCourse entity) {
        this.courseName = entity.getCourseName();
        this.courseType = entity.getCourseType();
        this.implementationInterval = entity.getImplementationInterval() + " " + (entity.getIntervalUnit() != null && entity.getIntervalUnit().equals("month") ? "\u4e2a\u6708" : "\u5e74");
        this.initialTrainingHours = entity.getInitialTrainingHours();
        this.retrainingHours = entity.getRetrainingHours();
        this.teachingMethod = entity.getTeachingMethod();
        this.participants = entity.getParticipants();
        this.instructor = entity.getInstructor();
        this.assessmentMethod = entity.getAssessmentMethod();
        this.trainingOutline = entity.getTrainingOutline();
        String string = this.status = entity.getStatus() != null && entity.getStatus() == 1 ? "\u542f\u7528" : "\u7981\u7528";
        if (entity.getCreateTime() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.createTime = entity.getCreateTime().format(formatter);
        } else {
            this.createTime = "";
        }
    }

    public String getCourseName() {
        return this.courseName;
    }

    public String getCourseType() {
        return this.courseType;
    }

    public String getImplementationInterval() {
        return this.implementationInterval;
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

    public String getStatus() {
        return this.status;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public void setImplementationInterval(String implementationInterval) {
        this.implementationInterval = implementationInterval;
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

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TrainingCourseExportModel)) {
            return false;
        }
        TrainingCourseExportModel other = (TrainingCourseExportModel)o;
        if (!other.canEqual(this)) {
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
        String this$implementationInterval = this.getImplementationInterval();
        String other$implementationInterval = other.getImplementationInterval();
        if (this$implementationInterval == null ? other$implementationInterval != null : !this$implementationInterval.equals(other$implementationInterval)) {
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
        String this$status = this.getStatus();
        String other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
            return false;
        }
        String this$createTime = this.getCreateTime();
        String other$createTime = other.getCreateTime();
        return !(this$createTime == null ? other$createTime != null : !this$createTime.equals(other$createTime));
    }

    protected boolean canEqual(Object other) {
        return other instanceof TrainingCourseExportModel;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $courseName = this.getCourseName();
        result = result * 59 + ($courseName == null ? 43 : $courseName.hashCode());
        String $courseType = this.getCourseType();
        result = result * 59 + ($courseType == null ? 43 : $courseType.hashCode());
        String $implementationInterval = this.getImplementationInterval();
        result = result * 59 + ($implementationInterval == null ? 43 : $implementationInterval.hashCode());
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
        String $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        String $createTime = this.getCreateTime();
        result = result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
        return result;
    }

    public String toString() {
        return "TrainingCourseExportModel(courseName=" + this.getCourseName() + ", courseType=" + this.getCourseType() + ", implementationInterval=" + this.getImplementationInterval() + ", initialTrainingHours=" + this.getInitialTrainingHours() + ", retrainingHours=" + this.getRetrainingHours() + ", teachingMethod=" + this.getTeachingMethod() + ", participants=" + this.getParticipants() + ", instructor=" + this.getInstructor() + ", assessmentMethod=" + this.getAssessmentMethod() + ", trainingOutline=" + this.getTrainingOutline() + ", status=" + this.getStatus() + ", createTime=" + this.getCreateTime() + ")";
    }
}


