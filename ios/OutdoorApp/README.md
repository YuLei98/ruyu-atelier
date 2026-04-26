# OutdoorApp - 山野记

iOS 户外活动记录应用，支持活动管理、轨迹记录、装备管理和队友管理。

## 功能特性

### 活动管理
- 创建、查看、编辑、删除户外活动
- 支持多种活动类型：徒步、露营、登山、骑行
- 活动收藏功能
- 活动详情展示（封面图标、位置、时间、距离、海拔等）

### 轨迹记录
- 实时 GPS 定位跟踪
- MapKit 地图展示
- 轨迹绘制和统计（距离、时长、海拔）
- 轨迹点存储

### 装备管理
- 户外装备清单管理
- 装备分类（背包、帐篷、睡袋等）

### 队友管理
- 活动参与者管理
- 队友信息记录

## 项目结构

```
ios/OutdoorApp/
├── Sources/
│   ├── App/
│   │   ├── AppDelegate.swift          # 应用入口
│   │   └── SceneDelegate.swift        # 场景生命周期管理
│   ├── Models/
│   │   ├── Activity.swift             # 活动模型
│   │   ├── TrackPoint.swift           # 轨迹点模型
│   │   ├── Equipment.swift            # 装备模型
│   │   └── Partner.swift              # 队友模型
│   ├── Services/
│   │   └── APIService.swift           # API 服务封装
│   └── ViewControllers/
│       ├── MainTabBarController.swift # 底部导航控制器
│       ├── HomeViewController.swift   # 首页（活动列表）
│       ├── ActivityCell.swift         # 活动列表单元格
│       ├── ActivityDetailViewController.swift  # 活动详情
│       ├── CreateActivityViewController.swift  # 创建活动
│       ├── TrackViewController.swift  # 轨迹记录
│       ├── EquipmentViewController.swift       # 装备管理
│       └── ProfileViewController.swift        # 个人中心
├── Resources/
│   ├── Info.plist                     # 应用配置
│   └── Assets.xcassets/              # 图片资源
└── project.yml                        # XcodeGen 配置
```

## 技术栈

- **UI 框架**: UIKit
- **布局**: SnapKit (Auto Layout)
- **地图**: MapKit + CoreLocation
- **网络**: URLSession
- **JSON 解析**: Codable
- **构建工具**: XcodeGen

## 环境要求

- iOS 15.0+
- Xcode 15.0+
- Swift 5.9

## 构建和运行

### 1. 安装依赖

```bash
# 安装 XcodeGen (如果未安装)
brew install xcodegen

# 安装 SnapKit (通过 CocoaPods)
cd ios/OutdoorApp
pod install
```

### 2. 生成 Xcode 项目

```bash
cd ios/OutdoorApp
xcodegen generate
```

### 3. 打开并运行

```bash
open OutdoorApp.xcworkspace
```

在 Xcode 中选择模拟器或真机，点击运行。

### 4. 命令行构建

```bash
xcodebuild -project OutdoorApp.xcworkspace \
  -scheme OutdoorApp \
  -configuration Debug \
  -destination 'platform=iOS Simulator,name=iPhone 16' build
```

## 后端 API 对接

应用默认连接本地后端服务：

- **Base URL**: `http://localhost:8000/api`
- **用户标识**: `X-User-Id` 请求头

### API 接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/activities` | GET | 获取活动列表 |
| `/activities` | POST | 创建活动 |
| `/activities/{id}` | DELETE | 删除活动 |
| `/activities/{id}/track` | POST | 添加轨迹点 |
| `/equipments` | GET | 获取装备列表 |
| `/equipments` | POST | 创建装备 |
| `/partners` | GET | 获取队友列表 |
| `/partners` | POST | 创建队友 |

### 修改 API 地址

在 `Sources/Services/APIService.swift` 中修改 `baseURL` 属性：

```swift
private let baseURL = "http://your-server:8000/api"
```

## 权限说明

应用需要以下系统权限：

| 权限 | 用途 |
|------|------|
| 位置 (Location When In Use) | 记录活动轨迹 |
| 相机 (Camera) | 拍摄活动照片 |
| 相册 (Photo Library) | 保存活动照片 |

## 应用配置

### Bundle 信息

- **Bundle ID**: `com.ruiyu.outdoorapp`
- **应用名称**: 山野记
- **最低版本**: iOS 15.0

### 主题色

| 用途 | 色值 |
|------|------|
| 主色调 | `#10B981` (RGB: 16, 185, 129) |
| 错误/停止 | `#EF4444` (RGB: 239, 68, 68) |
| 背景色 | `#F8FAFC` (RGB: 248, 250, 252) |

### 活动类型颜色

| 类型 | 颜色 |
|------|------|
| 徒步 (hiking) | `#10B981` |
| 露营 (camping) | `#F59E0B` |
| 登山 (climbing) | `#6366F1` |
| 骑行 (cycling) | `#EC4899` |

## 开发指南

### 添加新的 Model

1. 在 `Sources/Models/` 目录创建新的 Swift 文件
2. 实现 `Codable` 协议
3. 在 `APIService.swift` 中添加对应的 API 方法

### 添加新的 ViewController

1. 在 `Sources/ViewControllers/` 目录创建新的 Swift 文件
2. 在 `MainTabBarController.swift` 中注册 Tab

### 使用 SnapKit 布局

```swift
import SnapKit

view.addSubview(label)
label.snp.makeConstraints { make in
    make.top.equalTo(view.safeAreaLayoutGuide).offset(16)
    make.left.right.equalTo(view).inset(20)
}
```

## 相关文档

- [后端 API 文档](../../CLAUDE.md#outdoor-业务模块)
- [数据库表结构](../../src/main/java/com/ruiyu/outdoor/sql/outdoor.sql)
