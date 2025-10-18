# 高德地图 Android App

一个使用 Android Jetpack Compose 和 MVP 架构模式开发的高德地图复刻应用。

## 项目概览

本项目是一个简化版的高德地图应用，主要用于演示和测试目的。应用采用现代 Android 开发技术栈，包括 Jetpack Compose UI 框架和 MVP (Model-View-Presenter) 架构模式。

## 功能特性

### 已实现功能
- ✅ 底部导航栏（首页、消息、我的）
- ✅ 首页地图视图
- ✅ 搜索功能
- ✅ 快捷功能入口网格
- ✅ 推荐卡片区域
- ✅ 地图控制按钮
- ✅ 数据管理系统

### 页面结构
```
MainActivity
├── HomePage (已完成)
│   ├── ShowMap
│   ├── SearchPlace  
│   ├── ShowPlaceDetails
│   ├── PlanRoute
│   └── StartNavigation
├── MessagePage (占位页面)
│   ├── OpenChat
│   └── ShowNotifications
└── MyPage (占位页面)
    ├── ManageFavorites
    ├── EmergencyCall
    ├── ChangeCarIcon
    └── ChangeNavVoice
```

## 技术栈

- **开发语言**: Kotlin
- **UI 框架**: Jetpack Compose
- **架构模式**: MVP (Model-View-Presenter)
- **导航**: Navigation Compose
- **数据存储**: JSON 文件 (assets)
- **JSON 解析**: Gson
- **构建工具**: Gradle

## 项目结构

```
app/src/main/
├── java/com/example/GaoDe/
│   ├── base/                 # MVP 基础类
│   │   ├── BaseView.kt
│   │   └── BasePresenter.kt
│   ├── data/                 # 数据管理
│   │   └── DataManager.kt
│   ├── model/               # 数据模型
│   │   ├── Place.kt
│   │   ├── Message.kt
│   │   ├── Notification.kt
│   │   ├── UserPreferences.kt
│   │   ├── Route.kt
│   │   └── Favorite.kt
│   ├── ui/
│   │   ├── home/            # 首页模块
│   │   │   ├── HomeContract.kt
│   │   │   ├── HomePresenter.kt
│   │   │   └── HomeScreen.kt
│   │   └── theme/           # UI 主题
│   │       ├── Theme.kt
│   │       └── Type.kt
│   └── MainActivity.kt
├── assets/data/             # 初始数据
│   ├── places.json
│   ├── messages.json
│   ├── notifications.json
│   ├── user_preferences.json
│   ├── routes.json
│   └── favorites.json
└── res/                     # Android 资源文件
```

## 数据结构

应用使用 JSON 文件存储预设数据，包括：

- **地点信息** (places.json): 包含地点名称、地址、坐标、分类等
- **消息数据** (messages.json): 聊天消息记录
- **通知数据** (notifications.json): 系统通知和提醒
- **用户偏好** (user_preferences.json): 用户个性化设置
- **路线数据** (routes.json): 路线规划和导航信息
- **收藏数据** (favorites.json): 用户收藏的地点

## 开发说明

### 设计原则
1. **MVP 架构**: 严格遵循 Model-View-Presenter 模式，确保代码分离和可测试性
2. **组件化**: UI 组件高度模块化，便于复用和维护
3. **数据驱动**: 使用静态 JSON 数据，便于测试和演示
4. **简化实现**: 专注核心功能，不实现复杂的网络请求和数据库操作

### 构建要求
- Android Studio Arctic Fox (2020.3.1) 或更高版本
- Android SDK 34
- Kotlin 1.9.10
- Gradle 8.2.1

### 运行环境
- 最低 Android API 24 (Android 7.0)
- 目标 Android API 34

## 开发状态

### 当前版本: 1.2
- 📱 基础项目结构搭建完成
- 🏠 首页核心功能实现完成
- 🎨 UI 界面目标化重构完成：
  - 🗺️ 地图背景集成：移除纯色背景，使用渐变地图占位符
  - 🎯 快捷方式网格重构：改为2x3布局，6个核心功能（公交、地铁、骑行、打车、步行、订酒店）
  - 🏷️ 移除"订酒店"角标标识，简化视觉设计
  - 🏠 新增"回家"和"去单位"功能入口，水平排列
  - 🔍 简化搜索栏：移除麦克风图标，使用"武汉站"占位符
  - 🗑️ 移除"打车去南湖山庄"推荐卡片
  - ✨ 全新内联搜索框设计，背景色更柔和
  - 🎯 浮动操作按钮添加自定义阴影效果
  - 📄 底部面板重新设计：全宽布局、自定义阴影、拖拽手柄
  - 🧭 底部导航栏移除胶囊指示器，采用颜色切换的选中效果
- 📊 数据管理系统完成
- 🔧 MVP 架构基础框架完成

### 下一步计划
- 消息页面实现
- 我的页面实现
- 地点详情页面
- 路线规划功能
- 更多交互功能

## 特别说明

1. **离线应用**: 本应用不需要网络连接，所有数据来自本地 JSON 文件
2. **演示目的**: 主要用于 AI 操作测试和功能演示
3. **简化设计**: UI 图标使用 emoji 和 Material Icons，确保兼容性
4. **静态数据**: 消息、地点等信息为固定数据，不支持实时更新

## 许可证

本项目仅供学习和演示使用。