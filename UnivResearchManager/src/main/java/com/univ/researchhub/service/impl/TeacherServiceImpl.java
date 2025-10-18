package com.univ.researchhub.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.univ.researchhub.entity.Teacher;
import com.univ.researchhub.entity.User;
import com.univ.researchhub.mapper.TeacherMapper;
import com.univ.researchhub.mapper.UserMapper;
import com.univ.researchhub.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service // 标记为Spring服务类，可被Controller注入
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private UserMapper userMapper; // 注入UserMapper，用于关联校验

    // 1. 新增教师（带业务校验）
    @Override
    public boolean addTeacher(Teacher teacher) {
        // 校验1：关联的用户是否存在
        User existUser = userMapper.selectById(teacher.getUserId());
        if (existUser == null) {
            System.out.println("错误：关联的用户ID不存在");
            return false;
        }

        // 校验2：员工编号是否已存在
        Teacher existTeacher = lambdaQuery().eq(Teacher::getEmployeeNo, teacher.getEmployeeNo()).one();
        if (existTeacher != null) {
            System.out.println("错误：员工编号已存在");
            return false;
        }

        // 校验通过，执行新增
        return save(teacher);
    }

    // 2. 根据部门查询教师列表
    @Override
    public List<Teacher> getTeachersByDept(String department) {
        // 调用Mapper的自定义方法
        return teacherMapper.selectByDepartment(department);
    }

    // 3. 根据研究方向模糊查询（支持多个关键词）
    @Override
    public List<Teacher> getTeachersByResearchDir(String... directions) {
        List<Teacher> result = new ArrayList<>();
        for (String dir : directions) {
            // 调用Mapper的模糊查询方法，合并结果（去重）
            List<Teacher> teachers = teacherMapper.selectByResearchDirectionLike(dir);
            for (Teacher t : teachers) {
                if (!result.contains(t)) {
                    result.add(t);
                }
            }
        }
        return result;
    }

    // 4. 根据用户ID删除教师（级联校验）
    @Override
    public boolean removeTeacherByUserId(Integer userId) {
        // 这里可添加复杂校验，比如“该教师是否关联了科研项目，若有关联则不允许删除”
        // 示例：假设暂不允许删除有项目的教师（实际需关联ProjectMapper查询）
        boolean hasProject = false; // 实际场景：查询project表是否有该教师的关联记录
        if (hasProject) {
            System.out.println("错误：该教师关联了科研项目，不允许删除");
            return false;
        }

        // 校验通过，删除该用户关联的教师记录
        return lambdaUpdate().eq(Teacher::getUserId, userId).remove();
    }
}