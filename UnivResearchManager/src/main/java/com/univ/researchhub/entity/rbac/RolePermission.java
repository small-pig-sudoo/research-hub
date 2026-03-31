package com.univ.researchhub.entity.rbac;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("role_permission")
public class RolePermission {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer roleId;
    private String permKey;
}