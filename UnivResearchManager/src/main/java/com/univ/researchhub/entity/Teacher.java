package com.univ.researchhub.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("teacher") // 对应数据库中的teacher表
public class Teacher {
    @TableId(type = IdType.AUTO) // 主键自增（需确保数据库中teacher_id字段设置了AUTO_INCREMENT）
    private Long teacherId; // 教师表主键，对应数据库中的teacher_id字段

    private Integer userId; // 关联用户表的user_id（外键），对应数据库中的user_id字段

    private String employeeNo; // 员工编号，对应数据库中的employee_no字段

    private String department; // 所属部门，对应数据库中的department字段

    private String title; // 职称（如教授、副教授），对应数据库中的title字段

    private String researchDirection; // 研究方向，对应数据库中的research_direction字段
}
