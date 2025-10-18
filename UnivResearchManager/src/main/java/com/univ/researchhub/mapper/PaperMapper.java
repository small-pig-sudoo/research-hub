package com.univ.researchhub.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.univ.researchhub.entity.Paper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

@Mapper
public interface PaperMapper extends BaseMapper<Paper> {
    // 其他已有方法保持不变...
    @Select("SELECT * FROM paper WHERE author_id = #{authorId}")
    List<Paper> selectByAuthorId(Integer authorId);

    @Select("SELECT * FROM paper WHERE project_id = #{projectId}")
    List<Paper> selectByProjectId(Integer projectId);

    @Select("SELECT * FROM paper WHERE level = #{level}")
    List<Paper> selectByLevel(String level);

    // 关键修正：添加@Param注解明确参数名称，解决日期类型匹配问题
    @Select("SELECT * FROM paper WHERE publish_date BETWEEN #{startDate} AND #{endDate}")
    List<Paper> selectByPublishDateRange(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );
}
