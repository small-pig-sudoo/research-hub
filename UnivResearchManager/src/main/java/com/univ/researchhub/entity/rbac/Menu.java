package com.univ.researchhub.entity.rbac;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("menu")
public class Menu {
    @TableId(value = "menu_id", type = IdType.AUTO)
    private Long menuId;

    private Long parentId;
    private String name;
    private String path;
    private Integer sortNum;
    private Integer status; // 1???? 0????
}
