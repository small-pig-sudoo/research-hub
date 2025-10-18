package com.univ.researchhub.service;

import com.univ.researchhub.ResearchHubApplication;
import com.univ.researchhub.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// 标记为SpringBoot测试类，指定启动类加载上下文
@SpringBootTest(classes = ResearchHubApplication.class)
class UserServiceTest {

    // 注入UserService（Spring自动注入实现类UserServiceImpl）
    @Autowired
    private UserService userService;

    // 测试“根据用户名查询用户”
    @Test
    void getByUsername() {
        // 假设数据库中已有用户名"admin"的用户
        User user = userService.getByUsername("admin");
        assertNotNull(user, "根据用户名查询失败，未找到用户"); // 断言用户不为null
        System.out.println("查询到的用户信息：" + user);
    }

    // 测试“校验用户名是否已存在”
    @Test
    void checkUsernameExists() {
        boolean exists = userService.checkUsernameExists("admin");
        assertTrue(exists, "用户名已存在，但校验结果为不存在"); // 断言结果为true

        boolean notExists = userService.checkUsernameExists("test_not_exists");
        assertFalse(notExists, "用户名不存在，但校验结果为存在"); // 断言结果为false
    }

    // 测试“根据角色查询用户列表”
    @Test
    void getUsersByRole() {
        // 假设查询角色为"ADMIN"的用户
        List<User> adminList = userService.getUsersByRole("ADMIN");
        assertFalse(adminList.isEmpty(), "未查询到角色为ADMIN的用户"); // 断言列表不为空
        System.out.println("角色为ADMIN的用户数量：" + adminList.size());
        adminList.forEach(admin -> System.out.println("管理员信息：" + admin));
    }
}