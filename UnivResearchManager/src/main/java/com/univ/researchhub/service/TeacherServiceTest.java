package com.univ.researchhub.service;

import com.univ.researchhub.ResearchHubApplication;
import com.univ.researchhub.entity.Teacher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ResearchHubApplication.class)
class TeacherServiceTest {

    @Autowired
    private TeacherService teacherService;

    // 测试新增教师（带校验）
    @Test
    void testAddTeacher() {
        Teacher teacher = new Teacher();
        teacher.setUserId(2); // 已存在的用户ID
        teacher.setEmployeeNo("T" + System.currentTimeMillis()); // 动态唯一编号
        teacher.setDepartment("计算机科学与技术学院");
        teacher.setTitle("副教授");
        teacher.setResearchDirection("人工智能与数据挖掘");

        boolean result = teacherService.addTeacher(teacher);
        assertTrue(result, "新增教师失败（业务校验未通过）");
        System.out.println("新增教师ID：" + teacher.getTeacherId());
    }

    // 测试根据部门查询
    @Test
    void testGetTeachersByDept() {
        List<Teacher> teachers = teacherService.getTeachersByDept("计算机科学与技术学院");
        assertFalse(teachers.isEmpty(), "未查询到该部门教师");
        System.out.println("计算机学院教师列表：");
        // 修改：使用getEmployeeNo()和getTitle()替代getName()
        teachers.forEach(t -> System.out.println(t.getEmployeeNo() + " - " + t.getTitle()));
    }

    // 测试多关键词研究方向查询
    @Test
    void testGetTeachersByResearchDir() {
        // 查询包含“人工智能”或“数据挖掘”的教师
        List<Teacher> teachers = teacherService.getTeachersByResearchDir("人工智能", "数据挖掘");
        assertFalse(teachers.isEmpty(), "未查询到匹配研究方向的教师");
        System.out.println("匹配研究方向的教师：");
        // 修改：使用getEmployeeNo()替代getName()
        teachers.forEach(t -> System.out.println(t.getEmployeeNo() + " - " + t.getResearchDirection()));
    }
}
