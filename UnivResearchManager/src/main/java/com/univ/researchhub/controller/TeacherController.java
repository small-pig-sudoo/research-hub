package com.univ.researchhub.controller;

import com.univ.researchhub.entity.User;
import com.univ.researchhub.mapper.UserMapper;
import com.univ.researchhub.utils.JwtUtils;
import com.univ.researchhub.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 教师查看个人信息接口
     * Postman测试：GET请求，在Headers中添加Authorization=Bearer {token}
     */
    @GetMapping("/info")
    public Map<String, Object> getTeacherInfo(@RequestHeader("Authorization") String authorization) {
        // 1. 解析token（去掉Bearer前缀，如"Bearer eyJhbGciOiJIUzI1NiJ9..." → 截取后半部分）
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResultUtils.fail("token格式错误", 401); // 401：未授权
        }
        String token = authorization.substring(7);

        // 2. 校验token是否过期
        if (jwtUtils.isTokenExpired(token)) {
            return ResultUtils.fail("token已过期，请重新登录", 401);
        }

        // 3. 从token中获取用户ID，查询教师信息
        Long userId = jwtUtils.getUserIdFromToken(token);
        User teacher = userMapper.selectById(userId);
        if (teacher == null) {
            return ResultUtils.fail("教师不存在", 404);
        }

        // 4. 过滤敏感信息（不返回密码）
        teacher.setPassword(null);

        return ResultUtils.success(teacher);
    }
}