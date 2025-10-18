package com.univ.researchhub.entity.dto;

import lombok.Data;

/**
 * 登录请求参数DTO：接收前端传入的用户名和密码
 */
@Data // Lombok注解，自动生成getter/setter
public class LoginDTO {
    private String username; // 用户名（对应数据库user表的username字段）
    private String password; // 密码（对应数据库user表的password字段）
}