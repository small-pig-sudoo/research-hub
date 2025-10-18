package com.univ.researchhub.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.univ.researchhub.entity.Paper;

import java.util.Date;
import java.util.List;

/**
 * 论文业务逻辑接口
 * 处理论文的新增、查询、关联项目等业务
 */
public interface PaperService extends IService<Paper> {
    // 1. 新增论文（带业务校验：作者存在、标题非空等）
    boolean addPaper(Paper paper);

    // 2. 根据作者ID查询论文
    List<Paper> getPapersByAuthorId(Integer authorId);

    // 3. 根据项目ID查询关联的论文
    List<Paper> getPapersByProjectId(Integer projectId);

    // 4. 根据论文级别查询
    List<Paper> getPapersByLevel(String level);

    // 5. 更新论文关联的项目
    boolean updatePaperProject(Integer paperId, Integer projectId);

    // 6. 查询指定日期范围内的论文
    List<Paper> getPapersByDateRange(Date startDate, Date endDate);
}
