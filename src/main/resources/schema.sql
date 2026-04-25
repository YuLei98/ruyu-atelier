-- 用户认证表结构变更脚本
USE springboot_demo;

-- 添加 username 字段
ALTER TABLE users ADD COLUMN username VARCHAR(50) NOT NULL UNIQUE AFTER id;

-- 添加 password_hash 字段
ALTER TABLE users ADD COLUMN password_hash VARCHAR(255) NOT NULL AFTER username;

-- 添加 roles 字段
ALTER TABLE users ADD COLUMN roles VARCHAR(255) DEFAULT 'ROLE_USER' AFTER password_hash;

-- 添加 enabled 字段
ALTER TABLE users ADD COLUMN enabled TINYINT(1) DEFAULT 1 AFTER roles;

-- 添加 created_at 字段
ALTER TABLE users ADD COLUMN created_at DATETIME DEFAULT CURRENT_TIMESTAMP AFTER enabled;

-- 添加 updated_at 字段
ALTER TABLE users ADD COLUMN updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at;

-- 验证结果
DESCRIBE users;