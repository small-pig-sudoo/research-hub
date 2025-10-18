package com.univ.researchhub.entity.dto;

import lombok.Data;

@Data
public class AddTeacherDTO {
    private String username; // 教师用户名（唯一）
    private String password; // 初始密码（如123456，建议后续让教师自行修改）
    private String name; // 教师真实姓名
    private String phone; // 手机号（可选）
    private String email; // 邮箱（可选）
}