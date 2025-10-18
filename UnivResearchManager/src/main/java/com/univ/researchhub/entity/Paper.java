package com.univ.researchhub.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("paper") // 对应数据库中的paper表
public class Paper {
    @TableId(type = IdType.AUTO) // 主键自增（假设paper_id为自增主键）
    private Integer paperId; // 论文编号，对应数据库中的paper_id字段

    private String title; // 论文标题，对应数据库中的title字段

    private Integer authorId; // 作者编号（关联用户表或教师/学生表），对应数据库中的author_id字段

    private String journal; // 期刊名称，对应数据库中的journal字段

    private Date publishDate; // 发表日期，对应数据库中的publish_date字段

    private String level; // 论文级别（如核心、普刊等），对应数据库中的level字段

    private Integer projectId; // 关联项目编号（可选，若论文属于某个项目），对应数据库中的project_id字段
}
