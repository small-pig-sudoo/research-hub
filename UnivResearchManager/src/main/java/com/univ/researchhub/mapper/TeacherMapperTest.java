package com.univ.researchhub.mapper;

import com.univ.researchhub.ResearchHubApplication;
import com.univ.researchhub.entity.Teacher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ResearchHubApplication.class)
class TeacherMapperTest {

    @Autowired
    private TeacherMapper teacherMapper;

    private UserMapper userMapper;
    // 测试查询所有教师
    @Test

    void testSelectList() {
        List<Teacher> teachers = teacherMapper.selectList(null);
        assertFalse(teachers.isEmpty(), "教师表为空，请先插入测试数据");
        teachers.forEach(teacher -> System.out.println("教师信息：" + teacher));
    }

    // 测试根据部门查询
    @Test
    void testSelectByDepartment() {
        List<Teacher> teachers = teacherMapper.selectByDepartment("计算机科学与技术学院");
        assertFalse(teachers.isEmpty(), "未查询到该部门的教师");
        teachers.forEach(teacher -> System.out.println("计算机学院教师：" + teacher));
    }

    // 测试根据研究方向模糊查询
    @Test
    void testSelectByResearchDirectionLike() {
        List<Teacher> teachers = teacherMapper.selectByResearchDirectionLike("计算机科学与技术");
        assertFalse(teachers.isEmpty(), "未查询到研究方向包含'信息科学与工程学院'的教师");
        teachers.forEach(teacher -> System.out.println("匹配的教师：" + teacher));
    }

    // 测试新增教师
    @Test
    void testInsert() {
        Teacher teacher = new Teacher();
        // 关键：使用user表中已存在的user_id=2（对应teacher1用户）
        teacher.setUserId(2);
        // 员工编号需唯一，避免与表中已有数据重复
        teacher.setEmployeeNo("T2023009");
        // 可根据实际需求调整部门、职称等信息
        teacher.setDepartment("计算机科学与技术学院");
        teacher.setTitle("讲师");
        teacher.setResearchDirection("数据挖掘与智能分析");

        // 执行插入并验证结果
        int rows = teacherMapper.insert(teacher);
        assertEquals(1, rows, "新增教师失败（外键或唯一约束可能冲突）");
        System.out.println("新增教师成功！教师ID：" + teacher.getTeacherId());
    }
}
