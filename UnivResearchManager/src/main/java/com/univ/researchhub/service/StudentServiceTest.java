package com.univ.researchhub.service;

import com.univ.researchhub.ResearchHubApplication;
import com.univ.researchhub.entity.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ResearchHubApplication.class)
class StudentServiceTest {

    @Autowired
    private StudentService studentService;

    // 测试新增学生
    @Test
    void testAddStudent() {
        Student student = new Student();
        student.setUserId(3); // 假设user表存在user_id=3的用户
        student.setStudentNo("S2023001");
        student.setGrade("2023级");
        student.setMajor("计算机科学与技术");
        student.setDepartment("计算机学院");

        boolean success = studentService.addStudent(student);
        assertTrue(success, "新增学生失败（用户不存在或学号重复）");
        System.out.println("新增学生ID：" + student.getStudentId());
    }

    // 测试根据专业查询
    @Test
    void testGetByMajor() {
        List<Student> students = studentService.getByMajor("计算机科学与技术");
        assertFalse(students.isEmpty(), "未查询到该专业学生");
        students.forEach(s -> System.out.println("学生学号：" + s.getStudentNo()));
    }

    // 测试根据年级和院系查询
    @Test
    void testGetByGradeAndDept() {
        List<Student> students = studentService.getByGradeAndDept("2023级", "计算机学院");
        assertFalse(students.isEmpty(), "未查询到该年级和院系的学生");
        students.forEach(s -> System.out.println("学生信息：" + s));
    }
}