package com.univ.researchhub.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.univ.researchhub.entity.Student;

import java.util.List;

/**
 * 学生业务逻辑接口
 * 与StudentServiceImpl实现类的方法保持一致
 */
public interface StudentService extends IService<Student> {

    /**
     * 新增学生（带业务校验）
     * @param student 学生实体对象
     * @return 新增成功返回true，失败返回false
     */
    boolean addStudent(Student student);

    /**
     * 根据专业查询学生列表
     * @param major 专业名称
     * @return 符合条件的学生列表
     */
    List<Student> getByMajor(String major);

    /**
     * 根据年级和院系查询学生
     * @param grade 年级
     * @param department 院系
     * @return 符合条件的学生列表
     */
    List<Student> getByGradeAndDept(String grade, String department);

    /**
     * 根据用户ID删除学生信息
     * @param userId 用户ID
     * @return 删除成功返回true，失败返回false
     */
    boolean removeByUserId(Integer userId);
}
