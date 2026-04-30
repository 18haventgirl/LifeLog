# 📱 LifeLog — 生活回顾与时间追踪 Android APP

> **帮你从"今天好像啥也没干"的浑浊状态，变成"今天原来做了这些事"的清醒状态。**

<p align="center">
  <img src="https://img.shields.io/badge/Android-8.0%2B-brightgreen" />
  <img src="https://img.shields.io/badge/Kotlin-1.9-blue" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-M3-purple" />
  <img src="https://img.shields.io/badge/License-MIT-yellow" />
  <img src="https://img.shields.io/badge/Version-1.0.0-orange" />
</p>

---


## 📸 功能概览

### 📝 记录页面
| 功能 | 说明 |
|------|------|
| 活动选择器 | 14 个预设分类横向滚动 + 活动网格，支持自定义分类和活动 |
| 计时器 | 开始 → 暂停 → 继续 → 停止，前台服务保活 |
| 手动补录 | 选择活动 + DatePicker/TimePicker 选择时间段 + 备注 + 心情 + 标签 |
| 快速打点 | 一键记录当前活动，自动衔接时间 |
| 今日统计 | 总时长 + 记录次数实时更新 |
| 时间线 | 今日所有记录列表，点击查看详情，支持删除 |
| 记录详情 | 弹窗展示分类、时间、时长、方式、备注、心情、标签 |

### 📅 日报页面
| 功能 | 说明 |
|------|------|
| 日期切换 | 左右切换日期，一键回到今天 |
| 统计卡片 | 总时长、记录次数、最多分类 |
| 甜甜圈饼图 | 时间分布可视化 + 图例 + 百分比 |
| 甘特时间线 | 24 小时时间轴，直观展示活动分布 |
| 心情统计 | emoji 心情计数展示 |
| AI 总结语 | 根据数据自动生成每日总结 |
| 实时更新 | 新增记录后数据即时刷新 |

### 📊 周报页面
| 功能 | 说明 |
|------|------|
| 周切换 | 左右切换周 |
| 堆叠柱状图 | 每日分类时间分布 |
| 分类排行 | 本周各分类总时长 + 百分比 |

### 📈 月报页面
| 功能 | 说明 |
|------|------|
| 月切换 | 左右切换月份 |
| 活跃热力图 | GitHub 风格热力图，直观展示每日活跃度 |
| 分类排行 | 本月各分类统计 |

### 🎯 目标系统
| 功能 | 说明 |
|------|------|
| 创建目标 | 设置"至少/最多"分钟数 + "每天/每周"周期 |
| 启用/禁用 | 开关控制目标状态 |
| 删除目标 | 带确认弹窗 |

### 📂 分类管理
| 功能 | 说明 |
|------|------|
| 自定义大类 | 创建新分类（名称 + emoji 图标 + 颜色选择） |
| 自定义小类 | 每个分类下添加/编辑/删除活动 |
| 系统预设 | 14 个预设分类不可删除，自定义分类可删除 |

### ⚙️ 设置页面
| 功能 | 说明 |
|------|------|
| 深色模式 | 一键切换 |
| 通知提醒 | 日报提醒开关 |
| 导出数据 | 导出为 JSON 文件并分享 |
| 备份/恢复 | 数据库级别备份与恢复 |

---

## 🏗️ 技术栈

| 层级 | 技术 | 说明 |
|------|------|------|
| 语言 | **Kotlin** | 官方推荐，简洁安全 |
| UI 框架 | **Jetpack Compose** | 声明式 UI，动画丝滑 |
| 设计系统 | **Material Design 3** | 动态取色、圆角卡片 |
| 数据库 | **Room** | SQLite ORM，Flow 响应式查询 |
| 偏好存储 | **DataStore** | 类型安全的键值存储 |
| 架构模式 | **MVVM + Clean Architecture** | ViewModel + Repository |
| 依赖注入 | **Hilt** | 官方推荐 DI 框架 |
| 导航 | **Compose Navigation** | 声明式路由 |
| 图表 | **自定义 Canvas** | 饼图、柱状图、甘特图、热力图 |
| 后台服务 | **Foreground Service** | 计时器保活 |
| 异步 | **Coroutines + Flow** | 响应式数据流 |
| JSON | **Gson** | 数据导出序列化 |
| 构建 | **Gradle (Kotlin DSL)** | AGP 8.2 + KSP |

---

## 📁 项目结构

```
app/src/main/java/com/lifelog/app/
├── LifeLogApp.kt                          # Application（Hilt 入口）
├── MainActivity.kt                        # 唯一 Activity
├── data/
│   ├── local/
│   │   ├── LifeLogDatabase.kt             # Room 数据库
│   │   ├── dao/                           # CategoryDao, ActivityDao, RecordDao, GoalDao
│   │   ├── entity/                        # CategoryEntity, ActivityEntity, RecordEntity, GoalEntity
│   │   ├── converter/Converters.kt        # Room 类型转换器
│   │   └── datastore/SettingsDataStore.kt # DataStore 偏好存储
│   ├── repository/                        # 接口 + 实现（Category, Activity, Record, Goal, Settings）
│   └── model/                             # 领域模型 + 预设数据
├── domain/
│   ├── usecase/data/                      # ExportDataUseCase, BackupDataUseCase
│   └── util/                              # TimeUtils, DateUtils, SummaryGenerator
├── ui/
│   ├── theme/                             # Color, Type, Shape, Theme（M3 浅色/深色）
│   ├── navigation/                        # Screen 路由, NavGraph
│   ├── components/                        # 通用组件（ActivityPicker, PieChart, BarChart, GanttChart, HeatMap 等）
│   └── screen/
│       ├── record/                        # 记录页 + ViewModel + 弹窗
│       ├── report/                        # 日报/周报/月报 + ViewModel
│       ├── goal/                          # 目标管理
│       ├── category/                      # 分类管理
│       └── settings/                      # 设置页
├── service/                               # TimerService, BootReceiver
└── di/                                    # DatabaseModule, RepositoryModule, AppModule
```

---
## 🚀 快速开始

### 环境要求

- **JDK**: 17+
- **Android SDK**: API 34
- **Android Studio**: Ladybug 或更高版本（推荐）

## 📦 APK 下载

前往 [Releases](https://github.com/18haventgirl/LifeLog/releases) 页面下载最新 APK。

---
## 📄 License

```
MIT License

Copyright (c) 2026 LifeLog

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
