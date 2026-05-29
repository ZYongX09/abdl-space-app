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
| 设计风格 | Twitter/Weibo 混合风格 — 简洁、内容优先 |
| 网络 | Retrofit + OkHttp |
| 本地缓存 | Room |
| 图片加载 | Coil 2.x |
| 依赖注入 | Koin |
| 分页 | Paging 3 |
| Token 存储 | EncryptedSharedPreferences |
| 验证码 | Cloudflare Turnstile Android SDK |

## 设计原则

- **内容优先**：Feed 流是首页，帖子直接展示
- **简洁克制**：减少渐变、减少装饰，留白为主
- **Stagger 入场**：列表项依次滑入，参考 MIUI 设置页
- **Twitter 风格**：无 Card 包裹的 Feed，分割线分隔
- **微博风格**：个人主页封面渐变 + 头像半浮
- **4 Tab 导航**：首页 / 发现 / 通知 / 我的

## 项目结构

```
abdl-space-app/
├── app/
│   ├── src/main/
│   │   ├── java/top/abdl/space/
│   │   │   ├── App.kt                    # Application
│   │   │   ├── MainActivity.kt           # 主 Activity
│   │   │   ├── navigation/               # 导航
│   │   │   │   └── AppNavigation.kt      # 4 Tab 导航
│   │   │   ├── ui/                       # UI 层
│   │   │   │   ├── theme/                # 主题
│   │   │   │   ├── components/           # 通用组件
│   │   │   │   │   ├── AppCard.kt
│   │   │   │   │   ├── AppButton.kt      # 修复 pressed 状态
│   │   │   │   │   ├── AppTextField.kt
│   │   │   │   │   ├── HeroCard.kt       # 简化渐变
│   │   │   │   │   ├── ImageGrid.kt      # 新增：图片网格
│   │   │   │   │   ├── RatingBar.kt      # 新增：评分进度条
│   │   │   │   │   ├── StaggerItem.kt    # 新增：Stagger 入场
│   │   │   │   │   ├── EmptyState.kt
│   │   │   │   │   ├── ErrorView.kt
│   │   │   │   │   └── LoadingAnimation.kt
│   │   │   │   ├── home/                 # 首页（Feed 流）
│   │   │   │   │   └── HomeScreen.kt     # Twitter 风格 Feed
│   │   │   │   ├── forum/                # 论坛模块
│   │   │   │   │   ├── ForumScreen.kt    # 发现页（纸尿裤浏览）
│   │   │   │   │   ├── PostDetailScreen.kt
│   │   │   │   │   ├── CreatePostScreen.kt
│   │   │   │   │   └── ForumViewModel.kt
│   │   │   │   ├── diapers/              # 纸尿裤模块
│   │   │   │   │   ├── DiaperDetailScreen.kt  # 使用 RatingBar
│   │   │   │   │   ├── DiaperListScreen.kt
│   │   │   │   │   ├── DiaperViewModel.kt
│   │   │   │   │   └── SubmitRatingScreen.kt
│   │   │   │   ├── auth/                 # 认证模块
│   │   │   │   │   ├── LoginScreen.kt    # 重写：品牌感
│   │   │   │   │   ├── RegisterScreen.kt
│   │   │   │   │   ├── ForgotPasswordScreen.kt
│   │   │   │   │   └── AuthViewModel.kt
│   │   │   │   ├── profile/              # 个人中心
│   │   │   │   │   ├── ProfileScreen.kt  # 重写：微博风格
│   │   │   │   │   ├── EditProfileScreen.kt
│   │   │   │   │   └── ProfileViewModel.kt
│   │   │   │   ├── search/               # 搜索
│   │   │   │   ├── notifications/        # 通知
│   │   │   │   ├── splash/               # 启动页
│   │   │   │   └── settings/             # 设置
│   │   │   ├── data/                     # 数据层
│   │   │   └── util/                     # 工具类
│   │   └── res/                          # 资源文件
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
├── PROMPT.md                             # 详细开发规范
├── AGENTS.md                             # 开发约定
├── MEMORY.md                             # 长期记忆
├── MOBILE_UI_REFERENCE.md               # 移动端 UI 参考
└── project.md                            # 本文件
```

## 模块开发状态

| 模块 | 状态 | 备注 |
|------|------|------|
| 项目初始化 | ✅ 已完成 | Gradle、依赖、主题、导航、Logo |
| 通用组件 | ✅ 已完成 | AppCard、AppButton、AppTextField、LoadingAnimation、EmptyState、ErrorView |
| 新增组件 | ✅ 已完成 | ImageGrid、RatingBar、StaggerItem |
| 网络层 | ✅ 已完成 | Retrofit API、Token 拦截器、错误处理、数据模型 |
| 认证模块 | ✅ 已完成 | 登录、注册、忘记密码、AuthViewModel |
| 首页 Feed | ✅ 已完成 | Twitter 风格 Feed 流（原 Forum 功能迁移到 Home） |
| 发现页 | ✅ 已完成 | 纸尿裤浏览 + 品牌筛选（ForumScreen 改造） |
| 个人主页 | ✅ 已完成 | 微博风格：封面 + 头像半浮 + 统计 |
| 帖子详情 | ✅ 已完成 | 评论 Stagger 入场 |
| 搜索 | ✅ 已完成 | 帖子和纸尿裤搜索 |
| 通知 | ✅ 已完成 | Stagger 入场动画 |
| 纸尿裤评分 | ✅ 已完成 | RatingBar 组件 + Stagger 入场 |
| 设置 | ✅ 已完成 | 主题切换、关于页 |
| 深色模式 | ✅ 已完成 | 跟随系统或手动切换 |

## 设计系统变更记录（2026-05-30）

### 配色
- Primary: `#6AAEC8` → `#4A9CC7`（更沉稳的蓝）
- Secondary: `#F5A0B8` → `#E8909A`（更克制的粉）
- 移除了多余的 Tertiary 色（紫色）
- Background: `#F8FAFB` → `#F7F8FA`（更中性的灰白）

### 圆角
- extraSmall: 6dp → 8dp
- medium: 14dp → 16dp
- large: 20dp → 24dp

### 导航
- 5 Tab → 4 Tab（首页/发现/通知/我的）
- 合并"首页"和"广场"为一个 Feed
- "设置"移入"我的"页面

### 新增组件
- `StaggerItem` — 列表 Stagger 入场动画
- `ImageGrid` — 帖子图片网格（1/2/3/4+ 张自适应）
- `RatingBar` — 横向评分进度条

### 修复
- `AppButton` pressed 状态不会重置的 bug
- `SplashScreen` 引用不存在的 `splash_logo` drawable

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
