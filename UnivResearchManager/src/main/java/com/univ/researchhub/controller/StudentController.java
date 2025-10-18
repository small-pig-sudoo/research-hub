package com.univ.researchhub.controller;

import com.univ.researchhub.entity.Student;
import com.univ.researchhub.mapper.StudentMapper;
import com.univ.researchhub.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // 直接注入StudentMapper，用于调用自定义方法
    @Autowired
    private StudentMapper studentMapper;

    // 1. 新增学生（POST请求）
    @PostMapping
    public String addStudent(@RequestBody Student student) {
        boolean success = studentService.addStudent(student);
        return success ? "新增学生成功！学生ID：" + student.getStudentId() : "新增失败（用户不存在或学号重复）";
    }

    // 2. 根据专业查询学生（GET请求）
    @GetMapping("/major/{major}")
    public List<Student> getByMajor(@PathVariable String major) {
        return studentService.getByMajor(major);
    }

    // 3. 根据年级和院系查询学生（GET请求）
    @GetMapping("/grade-dept")
    public List<Student> getByGradeAndDept(
            @RequestParam String grade,
            @RequestParam String department) {
        return studentService.getByGradeAndDept(grade, department);
    }

    // 4. 根据用户ID删除学生（DELETE请求）
    @DeleteMapping("/user/{userId}")
    public String removeByUserId(@PathVariable Integer userId) {
        boolean success = studentService.removeByUserId(userId);
        return success ? "删除学生成功" : "删除失败（学生不存在）";
    }

    // 5. 根据学生ID查询详情（GET请求）
    @GetMapping("/{studentId}")
    public Student getById(@PathVariable Integer studentId) {
        return studentService.getById(studentId);
    }

    // 6. 根据用户ID查询学生信息（GET请求）
    // 修正：通过注入的StudentMapper调用自定义方法selectByUserId
    @GetMapping("/user/{userId}")
    public Student getByUserId(@PathVariable Integer userId) {
        return studentMapper.selectByUserId(userId); // 直接使用StudentMapper的自定义方法
    }
}
