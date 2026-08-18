package com.lframework.xingyun.shkb.vo.participant;

import com.lframework.starter.web.core.vo.PageVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="\u67e5\u8be2\u57f9\u8bad\u5b66\u5458\u8bf7\u6c42\u53c2\u6570")
public class QueryTrainingParticipantVo
extends PageVo {
    @ApiModelProperty(value="\u57f9\u8bad\u5b9e\u65bdID")
    private String implementationId;

    public String getImplementationId() {
        return this.implementationId;
    }

    public void setImplementationId(String implementationId) {
        this.implementationId = implementationId;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof QueryTrainingParticipantVo)) {
            return false;
        }
        QueryTrainingParticipantVo other = (QueryTrainingParticipantVo)((Object)o);
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        String this$implementationId = this.getImplementationId();
        String other$implementationId = other.getImplementationId();
        return !(this$implementationId == null ? other$implementationId != null : !this$implementationId.equals(other$implementationId));
    }

    protected boolean canEqual(Object other) {
        return other instanceof QueryTrainingParticipantVo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        String $implementationId = this.getImplementationId();
        result = result * 59 + ($implementationId == null ? 43 : $implementationId.hashCode());
        return result;
    }

    public String toString() {
        return "QueryTrainingParticipantVo(implementationId=" + this.getImplementationId() + ")";
    }
}


