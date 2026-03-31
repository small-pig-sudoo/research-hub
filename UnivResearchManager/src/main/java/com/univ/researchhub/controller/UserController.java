package com.univ.researchhub.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.univ.researchhub.common.Result;
import com.univ.researchhub.entity.User;
import com.univ.researchhub.entity.rbac.UserRole;
import com.univ.researchhub.mapper.rbac.UserRoleMapper;
import com.univ.researchhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleMapper userRoleMapper;

    // ✅ 保留你原有分页接口（如果你已有同名方法，就只保留一个）
    @GetMapping("/page")
    public Result<?> pageUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role
    ) {
        // 这里依赖你现有的 userService.pageUsers(...)，如果你的方法名不同告诉我我再对齐
        Object pageData = userService.pageUsers(page, size, keyword, role);
        return Result.success(pageData);
    }

    // ✅ 创建用户：默认密码 123456
    @PostMapping
    public Result<?> create(@RequestBody User user) {
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            user.setPassword("123456");
        }
        boolean ok = userService.save(user);
        return ok ? Result.success(user) : Result.error("创建用户失败");
    }

    // ✅ 查询用户详情
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        User u = userService.getById(id);
        return u != null ? Result.success(u) : Result.error("用户不存在");
    }

    // ✅ 更新用户（不允许这里改密码）
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody User user) {
        user.setUserId(id);
        user.setPassword(null); // 避免误覆盖密码
        boolean ok = userService.updateById(user);
        return ok ? Result.success(true) : Result.error("更新失败");
    }

    // ✅ 重置密码：默认 123456
    @PostMapping("/{id}/reset-password")
    public Result<?> resetPassword(@PathVariable Long id,
                                   @RequestParam(defaultValue = "123456") String password) {
        User u = new User();
        u.setUserId(id);
        u.setPassword(password);
        boolean ok = userService.updateById(u);
        return ok ? Result.success(true) : Result.error("重置失败");
    }

    // ✅ 删除用户：先删除 user_role 关联
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, id));
        boolean ok = userService.removeById(id);
        return ok ? Result.success(true) : Result.error("删除失败");
    }
}
