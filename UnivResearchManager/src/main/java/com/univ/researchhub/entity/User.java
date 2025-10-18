package com.univ.researchhub.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user")  // 指定对应数据库表名（必须与数据库表名一致）
public class User {
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;  // 改为userId，与数据库字段名一致，避免后续调用混淆

    @TableField(insertStrategy = com.baomidou.mybatisplus.annotation.FieldStrategy.NOT_NULL)
    private String username;  // 登录账号（非空）

    @TableField(insertStrategy = com.baomidou.mybatisplus.annotation.FieldStrategy.NOT_NULL)
    private String password;  // 登录密码（加密存储，非空）

    @TableField(insertStrategy = com.baomidou.mybatisplus.annotation.FieldStrategy.NOT_NULL)
    private String role;      // 角色：ADMIN / TEACHER / STUDENT（非空）

    @TableField(insertStrategy = com.baomidou.mybatisplus.annotation.FieldStrategy.NOT_NULL)
    private String real_name;      // 真实姓名（非空，教师查看个人信息时需要）

    @TableField(insertStrategy = com.baomidou.mybatisplus.annotation.FieldStrategy.IGNORED)
    private String phone;     // 手机号（可选）

    @TableField(insertStrategy = com.baomidou.mybatisplus.annotation.FieldStrategy.IGNORED)
    private String email;     // 邮箱（可选）
}
