package com.univ.researchhub.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.univ.researchhub.entity.User;
import com.univ.researchhub.entity.dto.AddTeacherDTO;
import com.univ.researchhub.mapper.UserMapper;
import com.univ.researchhub.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private UserMapper userMapper;

    /**
     * 管理员添加教师接口
     * 注意：生产环境需加权限控制（仅ADMIN角色可调用）
     * Postman测试：POST请求，Body传JSON格式的教师信息
     */
    @PostMapping("/addTeacher")
    public Map<String, Object> addTeacher(@RequestBody AddTeacherDTO addTeacherDTO) {
        // 1. 校验参数
        if (addTeacherDTO.getUsername() == null || addTeacherDTO.getPassword() == null) {
            return ResultUtils.fail("用户名或初始密码不能为空");
        }

        // 2. 校验用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, addTeacherDTO.getUsername());
        User existUser = userMapper.selectOne(wrapper);
        if (existUser != null) {
            return ResultUtils.fail("用户名已存在，请更换");
        }

        // 3. 组装User对象（角色固定为TEACHER）
        User teacher = new User();
        teacher.setUsername(addTeacherDTO.getUsername());
        teacher.setPassword(addTeacherDTO.getPassword()); // 生产环境需加密（如BCrypt）
        teacher.setRole("TEACHER"); // 角色：教师
        teacher.setReal_name(addTeacherDTO.getName());
        teacher.setPhone(addTeacherDTO.getPhone());
        teacher.setEmail(addTeacherDTO.getEmail());

        // 4. 插入数据库
        userMapper.insert(teacher);

        return ResultUtils.success("教师添加成功");
    }
}