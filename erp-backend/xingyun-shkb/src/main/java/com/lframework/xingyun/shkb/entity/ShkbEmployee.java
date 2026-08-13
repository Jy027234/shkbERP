package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 员工基本信息表
 * @TableName shkb_employee
 */
@TableName(value ="shkb_employee")
@Data
public class ShkbEmployee implements Serializable {
    /**
     * ID
     */
    @TableId
    private String id;

    /**
     * 员工工号
     */
    private String code;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别：0-未知 1-男 2-女
     */
    private Integer gender;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 出生日期
     */
    private Date birthday;

    /**
     * 民族
     */
    private String nation;

    /**
     * 籍贯
     */
    private String nativePlace;

    /**
     * 政治面貌
     */
    private String politicalStatus;

    /**
     * 学历
     */
    private String education;

    /**
     * 专业
     */
    private String major;

    /**
     * 毕业院校
     */
    private String graduateSchool;

    /**
     * 毕业日期
     */
    private Date graduateDate;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 现居住地址
     */
    private String address;

    /**
     * 紧急联系人
     */
    private String emergencyContact;

    /**
     * 紧急联系电话
     */
    private String emergencyPhone;

    /**
     * 部门ID
     */
    private String deptId;

    /**
     * 职位
     */
    private String position;

    /**
     * 入职日期
     */
    private Date entryDate;

    /**
     * 转正日期
     */
    private Date regularDate;

    /**
     * 离职日期
     */
    private Date leaveDate;

    /**
     * 离职原因
     */
    private String leaveReason;

    /**
     * 状态：0-离职 1-在职 2-试用期
     */
    private Integer status;

    /**
     * 照片URL
     */
    private String photoUrl;

    /**
     * 备注
     */
    private String description;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建人ID
     */
    private String createById;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 修改人ID
     */
    private String updateById;

    /**
     * 修改时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}