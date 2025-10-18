package com.univ.researchhub.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.univ.researchhub.entity.User;
import java.util.List;
/**
 * 用户业务接口：定义用户相关的业务方法
 * 继承IService<User>，直接获得MyBatis-Plus提供的批量查询、批量删除等增强CRUD方法
 */
public interface UserService extends IService<User> {
    // 1. 自定义业务：根据用户名查询用户（用于登录、用户信息查询）
    User getByUsername(String username);

    // 2. 自定义业务：新增用户时校验用户名是否已存在（避免重复注册）
    boolean checkUsernameExists(String username);

    // 3. 自定义业务：根据用户角色查询用户列表（如查询所有管理员、所有学生）
    List<User> getUsersByRole(String role);
}