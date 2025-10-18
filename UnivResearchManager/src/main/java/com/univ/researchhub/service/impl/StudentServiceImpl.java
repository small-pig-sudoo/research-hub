package com.univ.researchhub.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.univ.researchhub.entity.Student;
import com.univ.researchhub.entity.User;
import com.univ.researchhub.mapper.StudentMapper;
import com.univ.researchhub.mapper.UserMapper;
import com.univ.researchhub.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private UserMapper userMapper; // 注入UserMapper，用于校验用户存在性

    // 新增学生（带业务校验）
    @Override
    public boolean addStudent(Student student) {
        // 校验1：关联的用户是否存在
        User existUser = userMapper.selectById(student.getUserId());
        if (existUser == null) {
            System.out.println("错误：用户ID不存在，无法新增学生");
            return false;
        }

        // 校验2：学号是否已存在
        Student existStudent = lambdaQuery()
                .eq(Student::getStudentNo, student.getStudentNo())
                .one();
        if (existStudent != null) {
            System.out.println("错误：学号已存在，无法新增");
            return false;
        }

        // 校验通过，执行新增
        return save(student);
    }

    // 根据专业查询学生
    @Override
    public List<Student> getByMajor(String major) {
        return studentMapper.selectByMajor(major); // 假设StudentMapper有selectByMajor方法
    }

    // 根据年级和院系查询学生
    @Override
    public List<Student> getByGradeAndDept(String grade, String department) {
        return studentMapper.selectByGradeAndDept(grade, department); // 假设StudentMapper有该方法
    }

    // 根据用户ID删除学生
    @Override
    public boolean removeByUserId(Integer userId) {
        return lambdaUpdate()
                .eq(Student::getUserId, userId)
                .remove();
    }

}