package com.univ.researchhub.controller;

import com.univ.researchhub.entity.User;
import com.univ.researchhub.entity.dto.LoginDTO;
import com.univ.researchhub.mapper.UserMapper;
import com.univ.researchhub.utils.JwtUtils;
import com.univ.researchhub.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器：处理登录、注销等认证相关接口
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 登录接口
     * 访问路径：POST /api/auth/login
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginDTO loginDTO) {
        // 1. 校验请求参数
        if (loginDTO.getUsername() == null || loginDTO.getUsername().trim().isEmpty()) {
            return ResultUtils.fail("用户名不能为空");
        }
        if (loginDTO.getPassword() == null || loginDTO.getPassword().trim().isEmpty()) {
            return ResultUtils.fail("密码不能为空");
        }

        // 2. 根据用户名查询用户信息
        User user = userMapper.selectByUsername(loginDTO.getUsername().trim());
        if (user == null) {
            return ResultUtils.fail("用户名不存在");
        }

        // 3. 校验密码（生产环境需用BCrypt加密比对）
        if (!user.getPassword().equals(loginDTO.getPassword().trim())) {
            return ResultUtils.fail("密码错误");
        }

        // 4. 生成JWT token
        String token = jwtUtils.generateToken(user.getUserId(), user.getRole());

        // 5. 组装返回数据（修正：name对应数据库real_name字段）
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getUserId());
        data.put("username", user.getUsername());
        data.put("role", user.getRole());
        data.put("name", user.getReal_name()); // 已通过User实体映射到real_name字段

        // 6. 返回成功响应
        return ResultUtils.success(data);
    }
}
