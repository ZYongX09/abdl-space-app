# ABDL Space Android App

ABDL Space 是一个纸尿裤爱好者社区平台，提供纸尿裤数据库、用户评分、论坛、AI 推荐等功能。这是其原生 Android 应用。

## 技术栈

- **语言**：Kotlin
- **UI 框架**：Jetpack Compose
- **设计系统**：Material 3 Expressive
- **网络**：Retrofit + OkHttp
- **本地缓存：Room
- **图片加载**：Coil 2.x
- **依赖注入**：Koin
- **分页**：Paging 3

## 构建要求

- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17
- Android SDK 36

## 构建变体

- `devDebug` / `devRelease`：开发环境（本地 API）
- `stagingDebug` / `stagingRelease`：测试环境（本地 API）
- `prodDebug` / `prodRelease`：生产环境（api.abdl-space.top）

## 项目结构

```
app/src/main/java/top/abdl/space/
├── App.kt                    # Application
├── MainActivity.kt           # 主 Activity
├── navigation/               # 导航
├── ui/                       # UI 层
│   ├── theme/                # 主题
│   ├── components/           # 通用组件
│   ├── home/                 # 首页
│   ├── diapers/              # 纸尿裤模块
│   ├── forum/                # 论坛模块
│   ├── auth/                 # 认证模块
│   ├── profile/              # 个人中心
│   └── settings/             # 设置
├── data/                     # 数据层
│   ├── api/                  # Retrofit API
│   ├── repository/           # 数据仓库
│   ├── model/                # 数据模型
│   ├── local/                # Room 数据库
│   └── datastore/            # Token 存储
└── util/                     # 工具类
```

## 开发规范

详见 [PROMPT.md](PROMPT.md)

## 许可证

私有项目
