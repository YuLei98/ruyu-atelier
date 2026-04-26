-- 户外活动记录数据库初始化脚本
-- 业务库：outdoor

-- 创建业务库
CREATE DATABASE IF NOT EXISTS outdoor DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE outdoor;


-- 创建活动表
CREATE TABLE IF NOT EXISTS activities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '活动名称',
    type VARCHAR(20) COMMENT '活动类型: hiking, camping, climbing, cycling',
    location VARCHAR(200) COMMENT '活动地点',
    start_date DATE COMMENT '开始日期',
    duration VARCHAR(50) COMMENT '预计时长',
    description TEXT COMMENT '活动描述',
    total_distance DECIMAL(10,2) COMMENT '总距离(公里)',
    total_elevation INT COMMENT '总爬升(米)',
    cover_icon VARCHAR(10) COMMENT '封面图标',
    user_id INT NOT NULL COMMENT '用户ID',
    favorite BOOLEAN DEFAULT FALSE COMMENT '是否收藏',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='户外活动表';

-- 创建轨迹点表
CREATE TABLE IF NOT EXISTS track_points (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    latitude DOUBLE NOT NULL COMMENT '纬度',
    longitude DOUBLE NOT NULL COMMENT '经度',
    altitude DOUBLE COMMENT '海拔(米)',
    speed DOUBLE COMMENT '速度(km/h)',
    timestamp DATETIME NOT NULL COMMENT '定位时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='GPS轨迹点表';

-- 创建活动照片表
CREATE TABLE IF NOT EXISTS activity_photos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    url VARCHAR(500) NOT NULL COMMENT '照片URL',
    description VARCHAR(200) COMMENT '照片描述',
    taken_at DATETIME COMMENT '拍摄时间',
    latitude DOUBLE COMMENT '拍摄地点纬度',
    longitude DOUBLE COMMENT '拍摄地点经度',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动照片表';

-- 创建装备表
CREATE TABLE IF NOT EXISTS equipments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '装备名称',
    category VARCHAR(50) COMMENT '装备类型: backpack, shoes, clothing, tools',
    icon VARCHAR(10) COMMENT '装备图标',
    quantity INT DEFAULT 1 COMMENT '数量',
    remark VARCHAR(200) COMMENT '备注',
    user_id INT NOT NULL COMMENT '所属用户ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='装备表';

-- 创建伙伴表
CREATE TABLE IF NOT EXISTS partners (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(50) NOT NULL COMMENT '伙伴名称',
    avatar_color VARCHAR(20) COMMENT '头像颜色',
    contact VARCHAR(100) COMMENT '联系方式',
    remark VARCHAR(200) COMMENT '备注',
    user_id INT NOT NULL COMMENT '所属用户ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='伙伴表';

-- 创建活动装备关联表
CREATE TABLE IF NOT EXISTS activity_equipments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    equipment_id BIGINT NOT NULL COMMENT '装备ID',
    packed BOOLEAN DEFAULT FALSE COMMENT '是否已打包',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动装备关联表';

-- 创建活动伙伴关联表
CREATE TABLE IF NOT EXISTS activity_partners (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    partner_id BIGINT NOT NULL COMMENT '伙伴ID',
    role VARCHAR(20) DEFAULT 'member' COMMENT '角色: organizer, member',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动伙伴关联表';

-- 创建索引
CREATE INDEX idx_activities_user_id ON activities(user_id);
CREATE INDEX idx_activities_created_at ON activities(created_at);
CREATE INDEX idx_track_points_activity_id ON track_points(activity_id);
CREATE INDEX idx_activity_photos_activity_id ON activity_photos(activity_id);
CREATE INDEX idx_equipments_user_id ON equipments(user_id);
CREATE INDEX idx_partners_user_id ON partners(user_id);
CREATE INDEX idx_activity_equipments_activity_id ON activity_equipments(activity_id);
CREATE INDEX idx_activity_partners_activity_id ON activity_partners(activity_id);
