# Sleep

Sleep 是一款面向 Android 的极简睡眠记录应用，用于快速记录入睡与起床时间，并通过趋势图、日历、热力图和月度/年度报告帮助用户回顾自己的睡眠习惯。

应用采用本地优先设计：睡眠记录和主题设置保存在设备本地，当前版本不包含应用内账号体系或云同步功能。

## 主要功能

### 睡眠记录

- 一键完成“睡觉打卡”和“起床打卡”。
- 自动计算睡眠时长，并阻止重复创建尚未完成的睡眠记录。
- 支持手动补录、编辑和删除历史记录。
- 可记录做梦、夜醒次数、噩梦以及醒来感受。
- 区分夜间睡眠与午睡；应用可以自动判断，也允许用户手动指定。
- 首页展示最近一次夜间睡眠、睡眠状态和最近 5 条记录。

### 统计与回顾

- 查看入睡时间、起床时间和睡眠时长趋势。
- 按年份查看做梦、夜醒、噩梦和醒来状态热力图。
- 单独统计午睡次数、平均时长及相关睡眠状态。
- 在日历中按入睡时间、起床时间、睡眠时长或午睡四种模式浏览记录。
- 导出包含汇总、趋势、分布、日历、热力图和建议的月度睡眠报告长图。
- 导出当前年份的年度睡眠报告图片。

### 个性化与数据管理

- 内置“晨雾蓝”“薄荷绿”“墨白”三套主题。
- 支持通过十六进制颜色创建和删除自定义主题。
- 提供横向、竖向两种桌面小组件，可直接在桌面完成睡觉或起床打卡。
- 可将数据库和主题设置导出为 `.sleepbackup` 备份文件。
- 导入前会校验备份版本并进行二次确认；导入失败时会尝试回滚到原数据。

## 使用方法

1. 在首页点击“睡觉打卡”开始一条睡眠记录。
2. 起床后点击“起床打卡”，应用会补全起床时间并计算睡眠时长。
3. 点击最近记录可补充做梦、夜醒、噩梦和醒来感受等信息；向左滑动记录可删除。
4. 如果忘记打卡，可使用“手动补录”填写入睡和起床日期及时间。
5. 在“统计”和“日历”页面查看历史规律。
6. 在“设置”中切换主题、导入或导出数据，以及生成月度和年度报告。

午睡的自动判断规则为：入睡与起床发生在同一自然日、睡眠不足 4 小时，且入睡时间位于 09:00—20:59。用户手动设置的睡眠类型优先于自动判断。

## 技术栈

| 类别 | 实现 |
| --- | --- |
| 开发语言 | Kotlin 1.9.24、Java 17 |
| UI | Jetpack Compose、Material 3 |
| 状态管理 | ViewModel、StateFlow、Kotlin Coroutines |
| 本地存储 | Room 2.6.1、SharedPreferences |
| 桌面组件 | Jetpack Glance |
| 构建系统 | Gradle 8.7、Android Gradle Plugin 8.5.2、KSP |
| Android 版本 | minSdk 26、targetSdk 34、compileSdk 34 |

项目采用单 Activity 的 Compose 架构，主要数据流如下：

```text
Compose 页面
    │
SleepViewModel
    │
SleepRepository
    │
Room DAO ── sleep_database
```

页面层通过 `StateFlow` 订阅数据库变化；记录新增、修改或删除后，首页、统计和日历会自动刷新。

## 项目结构

```text
Sleep/
├─ app/
│  ├─ src/main/
│  │  ├─ java/com/amethamor/sleep/
│  │  │  ├─ backup/       # 备份、恢复、校验与回滚
│  │  │  ├─ data/         # Room 实体、DAO、数据库和仓库
│  │  │  ├─ ui/
│  │  │  │  ├─ calendar/  # 日历模型、计算与组件
│  │  │  │  ├─ report/    # 月度、年度报告计算和图片导出
│  │  │  │  ├─ screens/   # 首页、统计、日历和设置页面
│  │  │  │  ├─ statistics/# 统计计算、图表和热力图
│  │  │  │  └─ theme/     # 内置及自定义主题
│  │  │  ├─ util/         # 日期、时长计算和应用重启工具
│  │  │  └─ widget/       # 横向、竖向桌面小组件
│  │  └─ res/             # 图标、主题、字符串和组件配置
│  └─ src/test/            # 仓库、日期、统计和时长计算测试
├─ gradle/libs.versions.toml
└─ settings.gradle.kts
```

核心数据表为 `sleep_records`，主要字段包括记录日期、入睡时间、起床时间、时长、做梦状态、夜醒次数、噩梦状态、醒来感受及睡眠类型。当前数据库版本为 3。

## 开发与构建

### 环境要求

- Android Studio
- JDK 17（可直接使用 Android Studio 自带的 JBR）
- Android SDK 34

### 构建命令

Windows：

```powershell
.\gradlew.bat assembleDebug
```

macOS 或 Linux：

```bash
./gradlew assembleDebug
```

调试 APK 默认生成在：

```text
app/build/outputs/apk/debug/app-debug.apk
```

运行本地单元测试：

```powershell
.\gradlew.bat testDebugUnitTest
```

运行设备测试前，需要先连接真机或启动模拟器：

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

### Gradle Wrapper 注意事项

当前 `gradle/wrapper/gradle-wrapper.properties` 使用 Gradle 官方公开分发地址：

```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.7-bin.zip
```

首次构建时，Gradle Wrapper 会自动下载 Gradle 8.7。

## Releases

正式版本 APK 将通过 GitHub Releases 发布。

## 数据与隐私

- 应用清单未声明网络权限，应用自身不会将睡眠记录上传到服务器。
- 记录保存在 Room 数据库中，主题及相关设置保存在本地设置文件中。
- 手动导出的 `.sleepbackup` 文件包含全部睡眠记录和主题设置，请妥善保管。
- 导入备份会覆盖当前数据库和主题设置；虽然应用提供导入前回滚备份，仍建议在导入前额外导出当前数据。
- Android 系统级自动备份或换机迁移行为由设备系统及项目中的备份规则共同决定。

## 当前版本与限制

- 当前应用版本：1.0。
- 仅支持 Android 8.0（API 26）及以上设备。
- 暂无账号登录、跨设备云同步和在线数据分析。
- 睡眠统计用于个人记录和趋势回顾，不构成医学诊断或治疗建议。
- 当前包名为 `com.amethamor.sleep`。

## 许可证

本项目采用 GNU General Public License v3.0（GPL-3.0-only），详见 [`LICENSE`](LICENSE)。
