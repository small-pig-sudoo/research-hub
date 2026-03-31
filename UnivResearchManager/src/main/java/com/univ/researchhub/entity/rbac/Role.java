package com.univ.researchhub.entity.rbac;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("role")
public class Role {
    @TableId(value = "role_id", type = IdType.AUTO)
    private Long roleId;

    private String roleCode;
    private String roleName;
    private String description;
    private Integer status; // 1启用 0禁用
    private LocalDateTime createdAt;
}
