package com.univ.researchhub.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.univ.researchhub.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {
    // 继承BaseMapper后，已包含 selectById、insert、updateById、deleteById 等基础方法

    // 自定义方法1：根据部门查询教师列表
    @Select("SELECT * FROM teacher WHERE department = #{department}")
    List<Teacher> selectByDepartment(String department);

    // 自定义方法2：根据研究方向模糊查询
    @Select("SELECT * FROM teacher WHERE research_direction LIKE CONCAT('%', #{direction}, '%')")
    List<Teacher> selectByResearchDirectionLike(String direction);

    // 自定义方法3：根据用户ID查询教师信息（通过user_id关联）
    @Select("SELECT * FROM teacher WHERE user_id = #{userId}")
    Teacher selectByUserId(Integer userId);
}
