package com.lframework.xingyun.shkb.bo.certificate;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class CertificateStatisticsBo
implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value="\u8bc1\u4e66\u603b\u6570")
    private Integer total;
    @ApiModelProperty(value="\u6709\u6548\u8bc1\u4e66\u6570")
    private Integer valid;
    @ApiModelProperty(value="\u5373\u5c06\u8fc7\u671f\u6570")
    private Integer expiring;
    @ApiModelProperty(value="\u5df2\u8fc7\u671f\u6570")
    private Integer expired;

    public Integer getTotal() {
        return this.total;
    }

    public Integer getValid() {
        return this.valid;
    }

    public Integer getExpiring() {
        return this.expiring;
    }

    public Integer getExpired() {
        return this.expired;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public void setValid(Integer valid) {
        this.valid = valid;
    }

    public void setExpiring(Integer expiring) {
        this.expiring = expiring;
    }

    public void setExpired(Integer expired) {
        this.expired = expired;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CertificateStatisticsBo)) {
            return false;
        }
        CertificateStatisticsBo other = (CertificateStatisticsBo)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Integer this$total = this.getTotal();
        Integer other$total = other.getTotal();
        if (this$total == null ? other$total != null : !((Object)this$total).equals(other$total)) {
            return false;
        }
        Integer this$valid = this.getValid();
        Integer other$valid = other.getValid();
        if (this$valid == null ? other$valid != null : !((Object)this$valid).equals(other$valid)) {
            return false;
        }
        Integer this$expiring = this.getExpiring();
        Integer other$expiring = other.getExpiring();
        if (this$expiring == null ? other$expiring != null : !((Object)this$expiring).equals(other$expiring)) {
            return false;
        }
        Integer this$expired = this.getExpired();
        Integer other$expired = other.getExpired();
        return !(this$expired == null ? other$expired != null : !((Object)this$expired).equals(other$expired));
    }

    protected boolean canEqual(Object other) {
        return other instanceof CertificateStatisticsBo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $total = this.getTotal();
        result = result * 59 + ($total == null ? 43 : ((Object)$total).hashCode());
        Integer $valid = this.getValid();
        result = result * 59 + ($valid == null ? 43 : ((Object)$valid).hashCode());
        Integer $expiring = this.getExpiring();
        result = result * 59 + ($expiring == null ? 43 : ((Object)$expiring).hashCode());
        Integer $expired = this.getExpired();
        result = result * 59 + ($expired == null ? 43 : ((Object)$expired).hashCode());
        return result;
    }

    public String toString() {
        return "CertificateStatisticsBo(total=" + this.getTotal() + ", valid=" + this.getValid() + ", expiring=" + this.getExpiring() + ", expired=" + this.getExpired() + ")";
    }
}


