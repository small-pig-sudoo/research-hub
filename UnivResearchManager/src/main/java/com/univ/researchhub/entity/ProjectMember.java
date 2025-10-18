package com.univ.researchhub.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("project_member") // 对应数据库中的project_member表
public class ProjectMember {
    private Integer id; // 对应数据库中的id字段
    private Integer projectId; // 对应数据库中的project_id字段
    private Integer userId; // 对应数据库中的user_id字段
    private String roleInProject; // 对应数据库中的role_in_project字段
}