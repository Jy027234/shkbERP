package com.lframework.xingyun.shkb.bo.training;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class TrainingStatisticsBo
implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value="\u57f9\u8bad\u603b\u6570")
    private Integer total;
    @ApiModelProperty(value="\u5df2\u5b8c\u6210\u57f9\u8bad\u6570")
    private Integer completed;
    @ApiModelProperty(value="\u8fdb\u884c\u4e2d\u57f9\u8bad\u6570")
    private Integer inProgress;
    @ApiModelProperty(value="\u5f85\u57f9\u8bad\u6570")
    private Integer pending;

    public Integer getTotal() {
        return this.total;
    }

    public Integer getCompleted() {
        return this.completed;
    }

    public Integer getInProgress() {
        return this.inProgress;
    }

    public Integer getPending() {
        return this.pending;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public void setCompleted(Integer completed) {
        this.completed = completed;
    }

    public void setInProgress(Integer inProgress) {
        this.inProgress = inProgress;
    }

    public void setPending(Integer pending) {
        this.pending = pending;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TrainingStatisticsBo)) {
            return false;
        }
        TrainingStatisticsBo other = (TrainingStatisticsBo)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Integer this$total = this.getTotal();
        Integer other$total = other.getTotal();
        if (this$total == null ? other$total != null : !((Object)this$total).equals(other$total)) {
            return false;
        }
        Integer this$completed = this.getCompleted();
        Integer other$completed = other.getCompleted();
        if (this$completed == null ? other$completed != null : !((Object)this$completed).equals(other$completed)) {
            return false;
        }
        Integer this$inProgress = this.getInProgress();
        Integer other$inProgress = other.getInProgress();
        if (this$inProgress == null ? other$inProgress != null : !((Object)this$inProgress).equals(other$inProgress)) {
            return false;
        }
        Integer this$pending = this.getPending();
        Integer other$pending = other.getPending();
        return !(this$pending == null ? other$pending != null : !((Object)this$pending).equals(other$pending));
    }

    protected boolean canEqual(Object other) {
        return other instanceof TrainingStatisticsBo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $total = this.getTotal();
        result = result * 59 + ($total == null ? 43 : ((Object)$total).hashCode());
        Integer $completed = this.getCompleted();
        result = result * 59 + ($completed == null ? 43 : ((Object)$completed).hashCode());
        Integer $inProgress = this.getInProgress();
        result = result * 59 + ($inProgress == null ? 43 : ((Object)$inProgress).hashCode());
        Integer $pending = this.getPending();
        result = result * 59 + ($pending == null ? 43 : ((Object)$pending).hashCode());
        return result;
    }

    public String toString() {
        return "TrainingStatisticsBo(total=" + this.getTotal() + ", completed=" + this.getCompleted() + ", inProgress=" + this.getInProgress() + ", pending=" + this.getPending() + ")";
    }
}


