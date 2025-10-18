package com.univ.researchhub.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("student") // 对应数据库中的student表
public class Student {
    private Integer studentId; // 对应数据库中的student_id字段
    private Integer userId; // 对应数据库中的user_id字段
    private String studentNo; // 对应数据库中的student_no字段
    private String grade; // 对应数据库中的grade字段
    private String major; // 对应数据库中的major字段
    private String department; // 对应数据库中的department字段
}