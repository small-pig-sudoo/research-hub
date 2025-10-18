package com.univ.researchhub.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.univ.researchhub.entity.Teacher;

import java.util.List;

public interface TeacherService extends IService<Teacher> {
    // 1. 新增教师（带业务校验：员工编号唯一、关联用户存在）
    boolean addTeacher(Teacher teacher);

    // 2. 根据部门查询教师列表（复用Mapper的自定义方法）
    List<Teacher> getTeachersByDept(String department);

    // 3. 根据研究方向模糊查询（支持多个关键词，如“数据挖掘,人工智能”）
    List<Teacher> getTeachersByResearchDir(String... directions);

    // 4. 根据用户ID删除教师（级联校验：确保无关联项目再删除）
    boolean removeTeacherByUserId(Integer userId);
}