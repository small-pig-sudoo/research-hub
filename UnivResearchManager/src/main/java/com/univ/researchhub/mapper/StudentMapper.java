package com.univ.researchhub.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.univ.researchhub.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {
    // 继承BaseMapper后，已包含基础CRUD方法：selectById、insert、updateById、deleteById等

    // 自定义方法1：根据专业查询学生
    @Select("SELECT * FROM student WHERE major = #{major}")
    List<Student> selectByMajor(String major);

    // 自定义方法2：根据年级和院系查询学生
    @Select("SELECT * FROM student WHERE grade = #{grade} AND department = #{department}")
    List<Student> selectByGradeAndDept(String grade, String department);

    // 自定义方法3：根据用户ID查询学生信息
    @Select("SELECT * FROM student WHERE user_id = #{userId}")
    Student selectByUserId(Integer userId);
}