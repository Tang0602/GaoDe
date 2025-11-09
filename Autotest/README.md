# Android App Manual Testing with Script Verification

## 测试流程概述

这是一个"手动操作，脚本验证"的测试流程。测试人员在手机上手动操作App，App记录操作日志，然后通过Python脚本验证操作是否被正确记录。

## 架构设计

### App端
- **职责**: App负责在其私有内部存储中维护一个`1_message_history.json`日志文件
- **初始化逻辑**: App具有初始化逻辑，确保此文件在首次启动后即存在于 `/data/data/com.example.GaoDe/files/` 目录中
- **记录机制**: 当用户执行特定操作（如点击进入消息页面）时，App自动向JSON文件追加格式化的操作记录
- **存储位置**: 严格限定在私有内部存储 (`/data/data/com.example.GaoDe/files/1_message_history.json`)

### 测试端
- **职责**: Python脚本仅负责读取和验证操作日志，不包含任何UI交互
- **读取机制**: 通过 `adb run-as` 命令访问App的私有存储文件
- **验证逻辑**: 在内存中解析JSON数据，验证最后一条记录的操作是否符合预期

## 如何执行测试

### 步骤 1: 环境准备
确保安卓设备已连接并开启USB调试模式

### 步骤 2: 手动操作
在手机上手动执行指定操作：
- 打开App（包名：`com.example.GaoDe`）
- 点击底部导航栏的"消息"选项卡
- 进入消息页面

### 步骤 3: 进入测试目录
在PC上进入`Autotest`文件夹：
```bash
cd Autotest
```

### 步骤 4: 执行验证脚本
运行验证脚本：
```bash
python verify_navigation_log.py
```

### 步骤 5: 查看结果
观察终端输出的测试结果：
- `✓ PASS` - 验证成功
- `✗ FAIL` - 验证失败

## 文件说明

### `verify_navigation_log.py`
- **用途**: 读取设备私有日志并验证最后一条操作记录
- **功能**: 
  - 通过ADB命令直接读取私有存储中的JSON文件
  - 解析JSON数据并验证最后一条记录的`action`字段
  - 输出详细的验证结果和调试信息
- **预期验证**: 验证操作为"点击消息页面"

### `1_message_history.json` (设备端)
- **位置**: `/data/data/com.example.GaoDe/files/1_message_history.json`
- **作用**: 存储App用户操作的历史记录
- **格式**: JSON数组，包含带有时间戳、操作和页面信息的记录
- **示例格式**:
```json
[
  {
    "timestamp": "2025-11-09 14:30:25",
    "action": "点击消息页面",
    "page": "MessageScreen"
  }
]
```

## 核心特性

1. **无UI自动化依赖**: 测试脚本不包含任何UI交互或检查
2. **职责分离**: App负责记录，脚本负责验证
3. **私有存储访问**: 通过ADB run-as命令安全访问App私有数据
4. **实时验证**: 支持立即验证用户刚执行的操作

## 故障排除

### 常见错误及解决方案

1. **ADB命令失败**
   - 检查设备是否正确连接
   - 确认USB调试模式已开启
   - 验证设备授权状态

2. **JSON文件不存在**
   - 确保App已启动过至少一次
   - 检查App是否正确初始化文件

3. **权限错误**
   - 确认使用的是正确的包名 `com.example.GaoDe`
   - 检查App是否为debug版本（run-as命令要求）

## 扩展说明

此测试框架可以轻松扩展以验证其他用户操作：
- 修改Python脚本中的`expected_action`参数
- 在App中添加更多操作记录点
- 支持验证操作序列而非单一操作