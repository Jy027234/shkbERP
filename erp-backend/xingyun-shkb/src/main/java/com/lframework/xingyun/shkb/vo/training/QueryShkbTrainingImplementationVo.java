package com.lframework.xingyun.shkb.vo.training;

import com.lframework.starter.web.core.vo.PageVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="\u67e5\u8be2\u57f9\u8bad\u5b9e\u65bd\u8ba1\u5212\u8bf7\u6c42\u53c2\u6570")
public class QueryShkbTrainingImplementationVo
extends PageVo {
    @ApiModelProperty(value="\u8bfe\u7a0bID")
    private String courseId;
    @ApiModelProperty(value="\u72b6\u6001")
    private Integer status;
    @ApiModelProperty(value="\u8bfe\u7a0b\u540d\u79f0")
    private String courseName;

    public String getCourseId() {
        return this.courseId;
    }

    public Integer getStatus() {
        return this.status;
    }

    public String getCourseName() {
        return this.courseName;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof QueryShkbTrainingImplementationVo)) {
            return false;
        }
        QueryShkbTrainingImplementationVo other = (QueryShkbTrainingImplementationVo)((Object)o);
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        String this$courseId = this.getCourseId();
        String other$courseId = other.getCourseId();
        if (this$courseId == null ? other$courseId != null : !this$courseId.equals(other$courseId)) {
            return false;
        }
        Integer this$status = this.getStatus();
        Integer other$status = other.getStatus();
        if (this$status == null ? other$status != null : !((Object)this$status).equals(other$status)) {
            return false;
        }
        String this$courseName = this.getCourseName();
        String other$courseName = other.getCourseName();
        return !(this$courseName == null ? other$courseName != null : !this$courseName.equals(other$courseName));
    }

    protected boolean canEqual(Object other) {
        return other instanceof QueryShkbTrainingImplementationVo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        String $courseId = this.getCourseId();
        result = result * 59 + ($courseId == null ? 43 : $courseId.hashCode());
        Integer $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)$status).hashCode());
        String $courseName = this.getCourseName();
        result = result * 59 + ($courseName == null ? 43 : $courseName.hashCode());
        return result;
    }

    public String toString() {
        return "QueryShkbTrainingImplementationVo(courseId=" + this.getCourseId() + ", status=" + this.getStatus() + ", courseName=" + this.getCourseName() + ")";
    }
}


