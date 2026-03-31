-- ===== RBAC tables =====
DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS role_menu;
DROP TABLE IF EXISTS menu;
DROP TABLE IF EXISTS role;

CREATE TABLE role (
  role_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_code VARCHAR(50) NOT NULL UNIQUE,
  role_name VARCHAR(100) NOT NULL,
  description VARCHAR(255),
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE menu (
  menu_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  parent_id BIGINT DEFAULT 0,
  name VARCHAR(100) NOT NULL,
  path VARCHAR(200) NOT NULL,
  sort_num INT DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 1
);

CREATE TABLE role_menu (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_id BIGINT NOT NULL,
  menu_id BIGINT NOT NULL,
  UNIQUE KEY uk_role_menu (role_id, menu_id)
);

CREATE TABLE user_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  UNIQUE KEY uk_user_role (user_id, role_id)
);

-- ===== init roles =====
INSERT INTO role (role_code, role_name, description, status) VALUES
('ADMIN','管理员','系统管理员',1),
('TEACHER','教师','教师角色',1),
('STUDENT','学生','学生角色',1);

-- ===== init menus (paths must match frontend router/layout) =====
INSERT INTO menu (parent_id, name, path, sort_num, status) VALUES
(0,'仪表盘','/dashboard',10,1),

(0,'项目管理','/projects',20,1),
(2,'项目列表','/projects',21,1),
(2,'创建项目','/projects/create',22,1),

(0,'用户管理','/users',30,1),
(5,'用户列表','/users',31,1),
(5,'角色权限','/users/roles',32,1),
(5,'部门管理','/users/departments',33,1),

(0,'成员管理','/members',40,1),
(9,'成员列表','/members',41,1),

(0,'数据统计','/statistics/projects',50,1),
(11,'项目统计','/statistics/projects',51,1),

(0,'系统设置','/system/basic',60,1),
(13,'基础设置','/system/basic',61,1);

-- ===== grant admin all menus =====
INSERT INTO role_menu (role_id, menu_id)
SELECT r.role_id, m.menu_id
FROM role r, menu m
WHERE r.role_code = 'ADMIN';

-- ===== teacher default =====
INSERT INTO role_menu (role_id, menu_id)
SELECT r.role_id, m.menu_id
FROM role r
JOIN menu m ON m.path IN ('/dashboard','/projects','/projects/create','/members','/statistics/projects')
WHERE r.role_code = 'TEACHER';

-- ===== student default =====
INSERT INTO role_menu (role_id, menu_id)
SELECT r.role_id, m.menu_id
FROM role r
JOIN menu m ON m.path IN ('/dashboard','/projects')
WHERE r.role_code = 'STUDENT';
