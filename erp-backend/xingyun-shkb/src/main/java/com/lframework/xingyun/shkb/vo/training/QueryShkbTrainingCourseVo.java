package com.lframework.xingyun.shkb.vo.training;

import com.lframework.starter.web.core.vo.PageVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="\u67e5\u8be2\u57f9\u8bad\u8bfe\u7a0b\u8bf7\u6c42\u53c2\u6570")
public class QueryShkbTrainingCourseVo
extends PageVo {
    @ApiModelProperty(value="\u5173\u952e\u5b57\uff08\u8bfe\u7a0b\u540d\u79f0\uff09")
    private String keyword;
    @ApiModelProperty(value="\u8bfe\u7a0b\u7c7b\u578b")
    private String courseType;
    @ApiModelProperty(value="\u72b6\u6001")
    private Integer status;

    public String getKeyword() {
        return this.keyword;
    }

    public String getCourseType() {
        return this.courseType;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof QueryShkbTrainingCourseVo)) {
            return false;
        }
        QueryShkbTrainingCourseVo other = (QueryShkbTrainingCourseVo)((Object)o);
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        String this$keyword = this.getKeyword();
        String other$keyword = other.getKeyword();
        if (this$keyword == null ? other$keyword != null : !this$keyword.equals(other$keyword)) {
            return false;
        }
        String this$courseType = this.getCourseType();
        String other$courseType = other.getCourseType();
        if (this$courseType == null ? other$courseType != null : !this$courseType.equals(other$courseType)) {
            return false;
        }
        Integer this$status = this.getStatus();
        Integer other$status = other.getStatus();
        return !(this$status == null ? other$status != null : !((Object)this$status).equals(other$status));
    }

    protected boolean canEqual(Object other) {
        return other instanceof QueryShkbTrainingCourseVo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        String $keyword = this.getKeyword();
        result = result * 59 + ($keyword == null ? 43 : $keyword.hashCode());
        String $courseType = this.getCourseType();
        result = result * 59 + ($courseType == null ? 43 : $courseType.hashCode());
        Integer $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)$status).hashCode());
        return result;
    }

    public String toString() {
        return "QueryShkbTrainingCourseVo(keyword=" + this.getKeyword() + ", courseType=" + this.getCourseType() + ", status=" + this.getStatus() + ")";
    }
}


