package com.univ.researchhub.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {

    @TableId(value = "user_id", type = IdType.AUTO)
    @JsonProperty("user_id") // 前端用 user_id
    private Long userId;

    private String username;

    private String password;

    private String role;

    // ✅ 数据库列 real_name -> Java realName，但输出给前端仍然是 real_name
    @TableField("real_name")
    @JsonProperty("real_name")
    private String realName;

    private String phone;

    private String email;

    // ✅ 数据库列 created_at -> Java createdAt，但输出给前端仍然是 created_at
    @TableField("created_at")
    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createdAt;
}
