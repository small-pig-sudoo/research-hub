package com.univ.researchhub.controller;

import com.univ.researchhub.entity.User;
import com.univ.researchhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户接口层：提供 RESTful API，供前端调用
 * @RestController = @Controller + @ResponseBody（返回 JSON 格式数据）
 * @RequestMapping("/api/user")：所有用户接口的基础路径
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    // 注入 UserService，调用业务逻辑
    @Autowired
    private UserService userService;

    // 1. 根据用户名查询用户（GET 请求，路径参数）
    // 访问地址：http://localhost:8080/api/user/username/admin
    @GetMapping("/username/{username}")
    public User getUserByUsername(@PathVariable String username) {
        return userService.getByUsername(username);
    }

    // 2. 校验用户名是否已存在（GET 请求，请求参数）
    // 访问地址：http://localhost:8080/api/user/check?username=test
    @GetMapping("/check")
    public boolean checkUsername(@RequestParam String username) {
        return userService.checkUsernameExists(username);
    }

    // 3. 根据角色查询用户列表（GET 请求，请求参数）
    // 访问地址：http://localhost:8080/api/user/role?role=ADMIN
    @GetMapping("/role")
    public List<User> getUsersByRole(@RequestParam String role) {
        return userService.getUsersByRole(role);
    }

    // 4. 新增用户（POST 请求，请求体传 JSON 数据）
    // 访问地址：http://localhost:8080/api/user
    @PostMapping
    public boolean addUser(@RequestBody User user) {
        // 先校验用户名，不存在则新增
        if (userService.checkUsernameExists(user.getUsername())) {
            return false; // 用户名已存在，新增失败
        }
        return userService.save(user); // 调用 Service 新增用户
    }

    // 5. 删除用户（DELETE 请求，路径参数传用户 ID）
    // 访问地址：http://localhost:8080/api/user/1（1 是数据库中的 user_id）
    @DeleteMapping("/{id}")
    public boolean deleteUser(@PathVariable Long id) {
        return userService.removeById(id); // 根据 ID 删除用户
    }
}