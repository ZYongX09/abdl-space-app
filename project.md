# project.md — ABDL Space Android App 项目概况

> 最后更新：2026-05-30

---

## 项目简介

ABDL Space 是一个纸尿裤爱好者社区平台，提供纸尿裤数据库、用户评分、论坛、AI 推荐等功能。本项目是其原生 Android 应用。

## 技术栈

| 项目 | 技术 |
|------|------|
| 语言 | Kotlin |
| UI 框架 | Jetpack Compose |
| 设计系统 | Material 3 Expressive (alpha) |
| 网络 | Retrofit + OkHttp |
| 本地缓存 | Room |
| 图片加载 | Coil 2.x |
| 依赖注入 | Koin |
| 分页 | Paging 3 |
| Token 存储 | EncryptedSharedPreferences |
| 验证码 | Cloudflare Turnstile Android SDK |

## 项目结构

```
abdl-space-app/
├── app/
│   ├── src/main/
│   │   ├── java/top/abdl/space/
│   │   │   ├── App.kt                    # Application
│   │   │   ├── MainActivity.kt           # 主 Activity
│   │   │   ├── navigation/               # 导航
│   │   │   ├── ui/                       # UI 层
│   │   │   │   ├── theme/                # 主题
│   │   │   │   ├── components/           # 通用组件
│   │   │   │   ├── home/                 # 首页
│   │   │   │   ├── diapers/              # 纸尿裤模块
│   │   │   │   ├── forum/                # 论坛模块
│   │   │   │   ├── auth/                 # 认证模块
│   │   │   │   ├── profile/              # 个人中心
│   │   │   │   └── settings/             # 设置
│   │   │   ├── data/                     # 数据层
│   │   │   │   ├── api/                  # Retrofit API
│   │   │   │   ├── repository/           # 数据仓库
│   │   │   │   ├── model/                # 数据模型
│   │   │   │   ├── local/                # Room 数据库
│   │   │   │   └── datastore/            # Token 存储
│   │   │   └── util/                     # 工具类
│   │   └── res/                          # 资源文件
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── PROMPT.md                             # 详细开发规范
├── AGENTS.md                             # 开发约定
├── MEMORY.md                             # 长期记忆
└── project.md                            # 本文件
```

## 模块开发状态

| 模块 | 状态 | 备注 |
|------|------|------|
| 项目初始化 | ✅ 已完成 | Gradle、依赖、主题、导航、Logo |
| 通用组件 | ✅ 已完成 | AppCard、AppButton、AppTextField、LoadingAnimation、EmptyState、ErrorView |
| 网络层 | ✅ 已完成 | Retrofit API、Token 拦截器、错误处理、数据模型 |
| 认证模块 | ✅ 已完成 | 登录、注册、忘记密码、AuthViewModel |
| Feed 页 | ✅ 已完成 | 帖子列表、发帖、点赞、评论、ForumViewModel |
| 个人主页 | ✅ 已完成 | 我的主页、他人主页、关注/取关、ProfileViewModel |
| 搜索 | ✅ 已完成 | 帖子和纸尿裤搜索、SearchViewModel |
| 通知 | ✅ 已完成 | 通知列表、已读、NotificationViewModel |
| 纸尿裤评分 | ✅ 已完成 | 列表、详情、提交评分、DiaperViewModel |
| 设置 | ✅ 已完成 | 主题切换、关于页、SettingsViewModel |
| 深色模式 | ✅ 已完成 | 跟随系统或手动切换 |

## API 信息

- **Base URL**：`https://api.abdl-space.top`
- **认证方式**：`Authorization: Bearer <access_token>`
- **完整文档**：`/home/ZYongX/projects/git/abdl-space/API.md`

## 相关项目

| 项目 | 本地路径 | 用途 |
|------|---------|------|
| 后端 API | `/home/ZYongX/projects/git/abdl-space` | Cloudflare Worker + Hono + D1 |
| 主站前端 | `/home/ZYongX/projects/abdl-space-v2` | React 18 + Vite 5 |
| 移动端 Web | `/home/ZYongX/projects/abdl-space-mobile` | React 18 + Vite 5 |
