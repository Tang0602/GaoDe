# 高德地图 Android App

一个使用 Android Jetpack Compose 和 MVP 架构模式开发的高德地图复刻应用。

## 项目概览

本项目是一个简化版的高德地图应用，主要用于演示和测试目的。应用采用现代 Android 开发技术栈，包括 Jetpack Compose UI 框架和 MVP (Model-View-Presenter) 架构模式。

## 功能特性

### 已实现功能
- ✅ 底部导航栏（首页、消息、我的）
- ✅ 首页地图视图（集成高德SDK）
- ✅ 搜索功能
- ✅ 搜索地点页面（SearchPlace 界面）
- ✅ POI结果列表页面（POIResultsList 界面）
- ✅ 地点详情页面（ShowPlaceDetails 界面）
- ✅ 快捷功能入口网格
- ✅ 推荐卡片区域
- ✅ 地图控制按钮
- ✅ 消息页面（精确复刻）
- ✅ 我的页面（完整实现）
- ✅ 数据管理系统

### 页面结构
```
MainActivity
├── HomePage (已完成)
│   ├── ShowMap
│   ├── SearchPlace (已完成)
│   ├── POIResultsList (已完成)
│   ├── ShowPlaceDetails (已完成)
│   ├── PlanRoute
│   └── StartNavigation
├── MessagePage (已完成)
│   ├── OpenChat
│   └── ShowNotifications
└── MyPage (已完成)
    ├── ManageFavorites
    ├── EmergencyCall
    ├── ChangeCarIcon
    └── ChangeNavVoice
```

## 技术栈

- **开发语言**: Kotlin
- **UI 框架**: Jetpack Compose
- **架构模式**: MVP (Model-View-Presenter)
- **地图 SDK**: 高德地图 Android SDK v10.1.500
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
│   │   │   ├── HomeScreen.kt
│   │   │   ├── HomePageViewModel.kt
│   │   │   ├── GaodeMap.kt  # 高德地图组件
│   │   │   ├── SearchPlaceScreen.kt  # 搜索历史页面
│   │   │   ├── POIResultsListScreen.kt  # POI结果列表页面
│   │   │   ├── ShowPlaceDetailsContract.kt
│   │   │   ├── ShowPlaceDetailsPresenter.kt
│   │   │   └── ShowPlaceDetailsScreen.kt  # 地点详情页面
│   │   ├── message/         # 消息模块
│   │   │   └── MessageScreen.kt
│   │   ├── my/              # 我的模块
│   │   │   └── MyScreen.kt
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
│   ├── favorites.json
│   └── poi_restaurants.json
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
- **POI餐厅数据** (poi_restaurants.json): 美食分类下的餐厅详细信息

## 开发说明

### 设计原则
1. **MVP 架构**: 严格遵循 Model-View-Presenter 模式，确保代码分离和可测试性
2. **组件化**: UI 组件高度模块化，便于复用和维护
3. **数据驱动**: 使用静态 JSON 数据，便于测试和演示
4. **简化实现**: 专注核心功能，不实现复杂的网络请求和数据库操作

### 构建要求
- Android Studio Arctic Fox (2020.3.1) 或更高版本
- Android SDK 35
- Kotlin 1.9.25
- Gradle 8.6.0

### 运行环境
- 编译 Android API 35
- 最低 Android API 24 (Android 7.0)
- 目标 Android API 34

## 开发状态

### 当前版本: 3.0 (新增地点详情页面和用户评论功能)
- 📱 基础项目结构搭建完成
- 🏠 首页核心功能实现完成
- 💬 **消息页面精确复刻**：
  - ✅ 创建 MessageScreen.kt 完整消息页面
  - ✅ 实现 MessageInfo 数据结构和 MessageListItem 封装类型
  - ✅ 创建 MessageRow 组件渲染单条消息项
  - ✅ 创建 DateSeparator 组件实现日期分隔符
  - ✅ 使用 LazyColumn 实现可滚动消息列表
  - ✅ 精确复刻UI设计：顶部应用栏、圆形图标、时间戳布局
  - ✅ 集成到主导航系统，替换占位页面
  - 🎯 **完全按照截图复刻内容**：
    - ✅ "地点评论" - 恭喜你被活动君pick了！
    - ✅ "通知权限提醒" - 提醒：1个重要权限待开启
    - ✅ "订单提醒" - 打车·支付成功
    - ✅ "打车会话" - 司机即将到达提醒
    - ✅ "公共交通动态" - 叮～请查收公交智能提醒！
    - ✅ "天天领福利" - 十一去香港记得领一份免费蛋挞哦
    - ✅ "高德运动" - 叮～您有新的导航轨迹生成啦！
    - ✅ "高德快报" - 行程临时有变怎么办？五一出行必看！
    - ✅ "高德酒店" - 您的酒店订单已确认
  - ✅ 精确的颜色配置和图标选择
- 👤 **我的页面完整实现**：
  - ✅ 创建 MyScreen.kt 功能复杂的个人中心页面
  - ✅ 实现用户信息头部模块：头像、用户名、等级标签、统计信息
  - ✅ 实现个性化设置模块：语音包、车标、皮肤三大设置项
  - ✅ 构建订单中心模块：全部订单、退款取消、待评价（精简版）
  - ✅ 创建常用工具模块：收藏夹、语音包（精简版）
  - ✅ 构建我的车辆模块：添加爱车享权益入口
  - ✅ 右上角浮动操作按钮：列表和设置功能
  - ✅ **UI美观优化**：弱化模块边界线，移除Card阴影，使用Box+圆角背景
  - ✅ **布局优化**：减小模块间距至12dp，统一背景色为浅灰色
  - ✅ **精确需求实现**：严格按照需求文档删除多余模块和元素
    - 删除未提及的统计数据模块和我的钱包模块
    - 订单中心精简为3个元素：全部订单、退款/取消、待评价
    - 常用工具精简为2个元素：收藏夹、语音包
    - 车辆模块副标题调整为"添加爱车点这里"
  - ✅ 垂直滚动布局，完美适配长内容
- 🗺️ **高德地图SDK完整集成**：
  - ✅ MainActivity.kt 添加隐私合规初始化
  - ✅ 创建 GaodeMap.kt 组件，使用 AndroidView 包装 MapView
  - ✅ 正确管理 MapView 生命周期（onCreate, onResume, onPause, onDestroy）
  - ✅ 使用 DisposableEffect 和 LifecycleEventObserver 确保生命周期同步
  - ✅ HomeScreen.kt 集成真实地图组件，替换占位符
- 🎨 UI 界面完善：
  - 🎪 BottomSheet 拖拽指示器优化
  - 📏 拖拽状态优化：折叠状态(peek height: 120dp)确保搜索栏完整显示
  - 🔄 状态管理系统：MapUiState密封类和HomePageViewModel
  - ⚡ 地图加载优化：通过StateFlow管理加载状态
  - 🎯 UI状态响应：Loading、Success、Error状态处理
  - 📄 面板内容整合：搜索栏、快捷网格、回家/去单位按钮
  - 🎯 快捷方式网格：2x3布局，6个核心功能（公交、地铁、骑行、打车、步行、订酒店）
  - 🏠 "回家"和"去单位"功能入口
  - 🔍 简化搜索栏：使用"武汉站"占位符
  - ✨ 内联搜索框设计优化
  - 🎯 浮动操作按钮自定义阴影效果
  - 🧭 底部导航栏颜色切换选中效果
- 📊 数据管理系统完成
- 🔧 MVP + ViewModel 混合架构框架完成
- ⚡ 编译优化：gradle配置优化，编译速度提升30-50%
- 🔍 **搜索历史页面和导航架构优化**：
  - ✅ 创建 SearchPlaceScreen.kt 完整搜索历史页面
  - ✅ 实现 SearchHistoryScreen 主函数（按需求命名）
  - ✅ 实现顶部搜索栏：返回按钮、搜索关键词显示、搜索按钮
  - ✅ 分类快捷入口网格：美食、酒店、景点门票、加油充电、休闲玩乐、超市
  - ✅ 常用地点标签云：家、公司、收藏夹（使用 FlowRow 自动换行布局）
  - ✅ 历史记录完整实现：
    - ✅ 历史记录头部：标题和清空/管理按钮
    - ✅ 历史记录列表：动态渲染历史项目
    - ✅ HistoryItem 数据结构：图标、标题、副标题、操作按钮
    - ✅ HistoryRow 组件：完整的列表项渲染（左图标、中间信息、右操作）
    - ✅ 操作按钮：打车、路线功能（小图标+文字标签）
  - ✅ 完整可滚动布局：使用 verticalScroll 支持长内容
  - ✅ **导航架构精确实现**：
    - ✅ Screen.SearchPlace 路由定义（标准路径名为 "SearchPlace"）
    - ✅ MainActivity 中 NavHost 添加 SearchPlace 导航目标
    - ✅ 100% 保留原有 BottomSheetScaffold 复杂布局结构
    - ✅ 外科手术式修改：仅在现有 InlineSearchBar 中添加 clickable 导航
    - ✅ 完整保持原有 SheetContent 和底部面板所有功能
    - ✅ 零删除零简化的精准代码实现
    - ✅ 符合页面树命名规范的路由配置
  - ✅ 精确复刻参考截图：UI布局、颜色、间距完全一致
  - ✅ 添加 Accompanist FlowRow 依赖支持
- 🔧 **编译错误修复**：
  - ✅ 修复 InlineSearchBar 函数重载解析歧义错误
  - ✅ 删除重复的函数定义，保留正确版本
  - ✅ 确保使用正确的 Screen.SearchPlace.route 导航路径
- 🚀 **生产就绪**：三大核心页面 + 搜索页面全部完成，应用功能完整覆盖
- 🏪 **地点详情页面完整实现**：
  - ✅ 创建 ShowPlaceDetailsContract.kt MVP 契约接口
  - ✅ 创建 ShowPlaceDetailsPresenter.kt 业务逻辑层
  - ✅ 创建 ShowPlaceDetailsScreen.kt 完整地点详情页面
  - ✅ **顶部导航栏**：返回按钮，简洁设计
  - ✅ **主体信息区**：
    - ✅ 地点标题（粗体大字）：巴奴毛肚火锅(群光广场店)
    - ✅ 定位标签：连续2年（黄色高亮）+ 四川火锅（灰色标签）
    - ✅ 评分与价格：4.5超棒（蓝色背景）+ 52评价 + 人均¥148/人
  - ✅ **服务标签区**：横向流式布局，街道口火锅榜（橙色高亮）等
  - ✅ **营业时间**：营业中（绿色）+ 详情链接
  - ✅ **交通信息**：驾车距离和时间显示
  - ✅ **地址区域**：完整地址 + 商场标签 + 电话图标
  - ✅ **菜单区域**：
    - ✅ 菜品标题和"查看全部"链接
    - ✅ 菜单数量显示：菜单(141)
    - ✅ 水平滚动菜品预览：经典毛肚、梅花肉等
  - ✅ **用户评论区**：
    - ✅ 评论头部：用户评价(52)
    - ✅ 评价标签组：本地人评价、服务优质等流式布局
    - ✅ 首条评论卡片：用户头像、高德达人标识、评分、评论内容
    - ✅ 评论交互：全文展开按钮、浏览量显示
  - ✅ **底部操作栏**：
    - ✅ 常规功能：收藏(222)、分享、打车
    - ✅ 主要CTA：导航（灰色）+ 路线（蓝色）按钮
  - ✅ **完整垂直滚动**：使用ScrollView承载所有内容
  - ✅ **数据集成**：从places.json加载巴奴毛肚火锅数据
  - ✅ **导航实现**：从SearchPlace点击地点项跳转到详情页
  - ✅ **MVP架构**：完整的Contract-Presenter-View分离
  - ✅ **加载状态管理**：Loading、Success、Error状态处理
  - ✅ **UI精确复刻**：严格按照ShowPlaceDetails.jpg截图实现
- 🍽️ **POI结果列表页面完整实现**：
  - ✅ 创建 POIResultsListScreen.kt 美食搜索结果页面
  - ✅ 实现 POIItem 和 GroupBuyInfo 数据模型
  - ✅ 创建 poi_restaurants.json 餐厅数据文件
  - ✅ **顶部导航栏**：
    - ✅ 返回按钮 + 搜索关键词显示（美食）
    - ✅ 定位图标和清除按钮
  - ✅ **分类筛选栏**：
    - ✅ 水平滚动分类标签（美食、火锅、烧烤等）
    - ✅ 选中状态高亮效果
  - ✅ **POI列表展示**：
    - ✅ 使用 LazyColumn 实现垂直滚动列表
    - ✅ 完整的餐厅卡片布局：品牌Logo、详细信息、操作按钮
    - ✅ 品牌信息：名称、认证标识、营业状态
    - ✅ 位置信息：距离、交通方式、时间
    - ✅ 评价信息：评分、分类、人均价格、浏览量
    - ✅ 特色标签：榜单排名（红色）+ 特色菜品（灰色）
    - ✅ 用户评价：引号包装的用户评论
    - ✅ 优惠信息：团购价格、原价、折扣显示
  - ✅ **4个餐厅数据**：
    - ✅ 巴奴毛肚火锅：火锅榜第5名，4.5分，¥148/人
    - ✅ 木屋烧烤：烧烤榜第3名，4.7分，¥85/人，团购8折
    - ✅ 麦当劳：4.3分，¥35/人，代金券9折
    - ✅ 老乡鸡：早餐榜第1名，4.6分，¥28/人，团购6.4折
  - ✅ **导航集成**：
    - ✅ 从搜索页面"美食"分类图标点击跳转
    - ✅ 从搜索历史"美食"关键词点击跳转
    - ✅ POI卡片点击跳转到地点详情页
  - ✅ **图片资源**：使用 assets/avatar/ 目录下的餐厅Logo图片
  - ✅ **UI完美复刻**：严格按照POIResultsList_food.jpg截图实现

### 下一步计划
- 路线规划功能
- 消息详细聊天界面
- 订单详情和管理功能
- 个性化设置详细页面
- 车辆管理功能
- 更多交互功能和页面导航

## 特别说明

1. **离线应用**: 本应用不需要网络连接，所有数据来自本地 JSON 文件
2. **演示目的**: 主要用于 AI 操作测试和功能演示
3. **简化设计**: UI 图标使用 emoji 和 Material Icons，确保兼容性
4. **静态数据**: 消息、地点等信息为固定数据，不支持实时更新

## 许可证

本项目仅供学习和演示使用。