package com.lframework.xingyun.shkb.vo.authorization;

import com.lframework.starter.web.core.vo.PageVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="\u67e5\u8be2\u6388\u6743\u9879\u76ee\u8bf7\u6c42\u53c2\u6570")
public class QueryShkbAuthorizationProjectVo
extends PageVo {
    @ApiModelProperty(value="\u5173\u952e\u5b57\uff08\u5c97\u4f4d\u540d\u79f0\uff09")
    private String keyword;
    @ApiModelProperty(value="\u72b6\u6001")
    private Integer status;
    @ApiModelProperty(value="ID\u5217\u8868\uff0c\u591a\u4e2aID\u7528\u9017\u53f7\u5206\u9694")
    private String ids;

    public String getKeyword() {
        return this.keyword;
    }

    public Integer getStatus() {
        return this.status;
    }

    public String getIds() {
        return this.ids;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof QueryShkbAuthorizationProjectVo)) {
            return false;
        }
        QueryShkbAuthorizationProjectVo other = (QueryShkbAuthorizationProjectVo)((Object)o);
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
        Integer this$status = this.getStatus();
        Integer other$status = other.getStatus();
        if (this$status == null ? other$status != null : !((Object)this$status).equals(other$status)) {
            return false;
        }
        String this$ids = this.getIds();
        String other$ids = other.getIds();
        return !(this$ids == null ? other$ids != null : !this$ids.equals(other$ids));
    }

    protected boolean canEqual(Object other) {
        return other instanceof QueryShkbAuthorizationProjectVo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        String $keyword = this.getKeyword();
        result = result * 59 + ($keyword == null ? 43 : $keyword.hashCode());
        Integer $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)$status).hashCode());
        String $ids = this.getIds();
        result = result * 59 + ($ids == null ? 43 : $ids.hashCode());
        return result;
    }

    public String toString() {
        return "QueryShkbAuthorizationProjectVo(keyword=" + this.getKeyword() + ", status=" + this.getStatus() + ", ids=" + this.getIds() + ")";
    }
}


