-- ===== 经费使用表 =====
DROP TABLE IF EXISTS funding_usage;

CREATE TABLE funding_usage (
  usage_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id INT NOT NULL,
  used_amount DECIMAL(18,2) NOT NULL DEFAULT 0,
  usage_date DATETIME DEFAULT CURRENT_TIMESTAMP,
  description VARCHAR(255),
  FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE
);

-- ===== 测试数据 =====
-- 为项目1添加经费使用记录
INSERT INTO funding_usage (project_id, used_amount, description) VALUES
(1, 150000.00, '设备采购'),
(1, 20000.00, '差旅费');

-- 为项目2添加经费使用记录
INSERT INTO funding_usage (project_id, used_amount, description) VALUES
(2, 33300.00, '材料费'),
(2, 10000.00, '会议费');

-- 为项目3添加经费使用记录
INSERT INTO funding_usage (project_id, used_amount, description) VALUES
(3, 3000.00, '资料费');

-- 为项目4添加经费使用记录
INSERT INTO funding_usage (project_id, used_amount, description) VALUES
(4, 300.00, '办公费');
