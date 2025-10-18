package com.univ.researchhub.mapper;

import com.univ.researchhub.ResearchHubApplication;
import com.univ.researchhub.entity.User;
import com.univ.researchhub.mapper.UserMapper;
import org.testng.annotations.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

// 指定启动类，确保测试环境加载正确
@SpringBootTest(classes = ResearchHubApplication.class)
class UserMapperTest {

    // 自动注入 UserMapper（MyBatis-Plus 会生成代理实现类）
    @Autowired
    private UserMapper userMapper;

    @Test
    void testSelectAll() {
        // 调用 BaseMapper 的 selectList 方法，传 null 表示查询所有
        List<User> userList = userMapper.selectList(null);

        // 遍历打印结果
        for (User user : userList) {
            System.out.println("用户信息：" + user);
        }
    }
}