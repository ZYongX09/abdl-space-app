# ABDL Space Android App — AI 开发提示词

> 使用 OpenCode + Xiaomi Mimo-V2.5-Pro 开发
> 最后更新：2026-05-29

---

## 一、项目概述

你正在为 **ABDL Space** 项目开发原生 Android 应用。ABDL Space 是一个纸尿裤爱好者社区平台，提供纸尿裤数据库、用户评分、论坛、AI 推荐等功能。

### 现有项目

| 项目 | 本地路径 | GitHub 仓库 | 域名 | 技术栈 |
|------|---------|-------------|------|--------|
| 后端 API | `/home/ZYongX/projects/git/abdl-space` | zhx589/abdl-space | `api.abdl-space.top` | Cloudflare Worker + Hono + D1 |
| 主站前端 | `/home/ZYongX/projects/abdl-space-v2` | ZYongX09/ABDL-Space-V2 | `abdl-space.top` | React 18 + Vite 5 + Tailwind CSS |
| 移动端 Web | `/home/ZYongX/projects/abdl-space-mobile` | ZYongX09/abdl-space-mobile | `m.abdl-space.top` | React 18 + Vite 5 + Tailwind CSS |
| 开放平台 | `/home/ZYongX/projects/abdl-space-open-platform` | ZYongX09/abdl-space-open-platform | `open.abdl-space.top` | React 18 + Vite |
| 图床 | — | — | `img.abdl-space.top` | Cloudflare ImgBed |

### Android 应用定位

- **仓库名**：`abdl-space-app`（在 GitHub `ZYongX09` 账户下新建）
- **包名**：`top.abdl.space`
- **最低 SDK**：API 26 (Android 8.0) — 覆盖 95%+ 设备
- **目标 SDK**：API 36 (Android 16) — Google Play 2026 年 8 月起强制要求，已稳定
- **编译 SDK**：API 36
- **技术栈**：Kotlin + Jetpack Compose + Material 3 Expressive

---

## 二、设计系统

### 主方案：Material 3 Expressive

**使用 Google Material 3 Expressive**（2025 年 Google I/O 发布，Jetpack Compose M3 1.5.0+ 支持）。

M3 Expressive 已经大幅改进了动画和表现力，包含：
- **弹性动画系统**：基于物理的 motion tokens，支持 spring/弹性过渡
- **新组件**：Button groups、FAB menu、Loading indicator、Split button、Toolbars
- **35 个新形状** + shape morph 动效
- **情感化设计**：更好的层级、更丰富的视觉表达

在 Jetpack Compose 中使用 `androidx.compose.material3` 包。

### 补充方案：MIUI/HyperOS 风格动画（可选参数）

在 M3 Expressive 基础上，提供一组 MIUI 风格的 spring 参数作为可选配置。不是独立系统，只是几个参数变体：

```kotlin
// MIUI 风格弹簧动画参数（比 M3 默认更弹）
val MiuiSpring = spring<Float>(
    dampingRatio = 0.65f,  // M3 默认 0.85f，MIUI 更弹
    stiffness = 280f
)

// 列表 stagger 入场（MIUI 设置页风格）
items.forEachIndexed { index, item ->
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInVertically(
            initialOffsetY = { it / 3 },
            animationSpec = tween(300, delayMillis = index * 40)
        )
    )
}
```

### 色彩系统（基于现有 Web 版 CSS 变量）

```kotlin
// 浅色主题
val LightColors = lightColorScheme(
    primary = Color(0xFF6AAEC8),        // --primary-dark
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDEEEFF), // --primary-light
    secondary = Color(0xFFFFB7C5),       // --accent
    background = Color(0xFFF5F8FC),      // --bg
    surface = Color.White,               // --bg-card
    surfaceVariant = Color(0xFFF5F8FC),  // --input-bg
    error = Color(0xFFE8837C),           // --danger
    outline = Color(0xFFE8F0F8),         // --border
    onBackground = Color(0xFF2C3E50),    // --text
    onSurfaceVariant = Color(0xFF7F8C9B) // --text-light
)

// 深色主题
val DarkColors = darkColorScheme(
    primary = Color(0xFFA8D8F0),
    onPrimary = Color(0xFF1A1D23),
    primaryContainer = Color(0xFF2A3F50),
    secondary = Color(0xFFF5989E),
    background = Color(0xFF1A1D23),
    surface = Color(0xFF252830),
    surfaceVariant = Color(0xFF2A2E35),
    error = Color(0xFFD35F5A),
    outline = Color(0xFF383C44),
    onBackground = Color(0xFFE0E4EA),
    onSurfaceVariant = Color(0xFF9BA1AC)
)
```

### 圆角规范

```kotlin
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(10.dp),      // --radius-sm
    medium = RoundedCornerShape(16.dp),     // --radius
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)
```

---

## 三、功能规划

### Phase 1 — 基础功能（本次开发）

| 模块 | 功能 | API 端点 | 对应 Web 页面 |
|------|------|---------|--------------|
| **认证** | 登录（用户名/邮箱+密码） | `POST /api/auth/login` | Login.jsx |
| | 注册 | `POST /api/auth/register` | Register.jsx |
| | 忘记密码 | `POST /api/auth/forgot-password` | ForgotPassword.jsx |
| | 重置密码 | `POST /api/auth/reset-password` | ForgotPassword.jsx |
| | 发送邮箱验证码 | `POST /api/auth/send-code` | VerificationInput.jsx |
| | 绑定/换绑邮箱 | `POST /api/auth/bind-email` | AccountPrivacy.jsx |
| | 获取当前用户 | `GET /api/auth/me` | — |
| **纸尿裤** | 列表（分页、筛选、排序） | `GET /api/diapers` | Diapers.jsx |
| | 详情（尺寸、图片、评分） | `GET /api/diapers/:id` | DiaperDetail.jsx |
| | 品牌列表 | `GET /api/diapers/brands` | — |
| | 尺寸列表 | `GET /api/diapers/sizes` | — |
| | 对比 | `GET /api/diapers/compare` | ComparePage.jsx |
| | 搜索纸尿裤 | `GET /api/search?q=&type=diaper` | — |
| **评分** | 提交评分（6 维度 1-10） | `POST /api/ratings` | DiaperDetail.jsx |
| | 查看纸尿裤评分 | `GET /api/diapers/:id/ratings` | DiaperDetail.jsx |
| | 查看我的评分 | `GET /api/ratings/me/:diaperId` | — |
| | 删除评分 | `DELETE /api/ratings/:id` | — |
| | 基准分参考 | — | BaseScoreRef.jsx |
| **使用感受** | 提交感受（5 维度 -5~5） | `POST /api/feelings` | DiaperDetail.jsx |
| | 查看感受 | `GET /api/diapers/:id/feelings` | DiaperDetail.jsx |
| **论坛** | 帖子列表（分页） | `GET /api/posts` | ForumFeed.jsx |
| | 帖子详情 + 评论 | `GET /api/posts/:id` | PostDetail.jsx |
| | 发帖（支持图片） | `POST /api/posts` | CreatePost.jsx |
| | 删除帖子 | `DELETE /api/posts/:id` | — |
| | 发评论 | `POST /api/posts/:id/comments` | PostDetail.jsx |
| | 点赞/取消 | `POST /api/likes` | PostDetail.jsx |
| | **图片上传** | `POST /api/images/upload` | ImageUploader.jsx |
| | **图片删除** | `POST /api/images/delete` | — |
| **排行榜** | 综合排行 | `GET /api/rankings` | Rankings.jsx |
| **搜索** | 全局搜索 | `GET /api/search?q=&type=all` | — |
| | 搜索用户 | `GET /api/users/search?q=` | — |
| **用户** | 个人主页 | `GET /api/users/:id` | Profile.jsx |
| | **他人主页** | `GET /api/users/:id` | UserPage.jsx |
| | 编辑资料 | `PATCH /api/users/me` | EditProfile.jsx |
| | 用户帖子 | `GET /api/users/:id/posts` | Profile.jsx |
| | 用户评分 | `GET /api/users/:id/ratings` | Profile.jsx |
| | **用户感受** | `GET /api/users/:id/feelings` | Profile.jsx |
| | **用户等级** | `GET /api/users/:id/level` | Profile.jsx |
| | **穿过的纸尿裤** | `GET /api/users/:id/worn` | Profile.jsx |
| | 关注/取关 | `POST/DELETE /api/follows` | Profile.jsx |
| | 粉丝/关注列表 | `GET /api/follows/:id/followers` | FollowersPage.jsx |
| **通知** | 通知列表 | `GET /api/notifications` | NotificationsPage.jsx |
| | 标记已读 | `PATCH /api/notifications/:id` | — |
| | **全部已读** | `POST /api/notifications/read-all` | — |
| **消息** | 对话列表 | `GET /api/messages/conversations` | MessagesPage.jsx |
| | 发送消息 | `POST /api/messages` | MessagesPage.jsx |
| | **新建对话** | `POST /api/messages` | NewConversation.jsx |
| **设置** | 主题切换（浅色/深色/多彩） | — | Settings.jsx |
| | NSFW 模糊开关 | — | Settings.jsx |
| **账户** | 账户与隐私管理 | — | AccountPrivacy.jsx |
| | 修改密码 | `POST /api/admin/reset/password` | AccountPrivacy.jsx |
| **验证码** | 风险评估 | `POST /api/captcha/risk` | VerifyModal.jsx |
| | 创建挑战 | `POST /api/captcha/challenge` | VerifyModal.jsx |
| | Quantum 验证 | `POST /api/captcha/verify` | VerifyModal.jsx |
| | Turnstile 验证 | `POST /api/captcha/turnstile/verify` | VerifyModal.jsx |
| **猜你喜欢** | 纯数据推荐（无 AI） | `GET /api/recommend/guess` | Recommendations.jsx |
| **举报** | 举报帖子/评论 | `POST /api/reports` | ReportModal.jsx |
| **法律** | 隐私政策 | — | PrivacyPolicy.jsx |
| | 用户协议 | — | TermsOfService.jsx |
| | 未成年人保护 | — | MinorProtection.jsx |
| **其他** | 关于页面（版本/更新日志） | — | About.jsx |
| | 官方认证徽章 | — | OfficialBadge.jsx |
| | 富文本渲染（URL 识别） | — | RichContent.jsx |
| | 图片网格展示 | — | ImageGrid.jsx |
| | 下拉刷新 | — | PullToRefresh.jsx |
| | 返回顶部 | — | BackToTop.jsx |
| | 邮箱验证码输入 | — | VerificationInput.jsx |

### Phase 2 — 预留位置（UI 先占位，功能后期添加）

| 模块 | 预留说明 | 对应 Web 页面 | API |
|------|---------|--------------|-----|
| AI 推荐 | 首页顶部预留 "AI 推荐" 卡片区域 | Recommendations.jsx | `POST /api/recommend` |
| NBW 第三方登录 | 登录页预留"宝宝新天地登录"按钮位置 | NBWCallback.jsx | `/api/auth/nbw/*` |
| NBW 账号绑定 | 账户页预留 NBW 绑定/解绑 | AccountPrivacy.jsx | `/api/auth/nbw/bind` |
| Wiki/术语百科 | 纸尿裤详情页预留 "Wiki" Tab | TermWiki.jsx | `/api/pages/*`, `/api/terms` |
| 比较 | 纸尿裤列表预留"比较"按钮 | ComparePage.jsx | `GET /api/diapers/compare` |
| 等级系统 | 个人主页预留等级展示 | Profile.jsx | `GET /api/users/:id/level` |
| 私信增强 | 消息页增加新建对话、搜索用户 | MessagesPage.jsx | `/api/messages/*` |
| 开放平台 | "我的"页面预留"开放平台"入口 | — | `/api/oauth/*` |
| OAuth 应用管理 | 设置页预留 | OAuthClientsPage.jsx | `/api/oauth/clients` |
| 多账号切换 | 侧边栏/设置预留 | AccountSwitcher.jsx | — |
| NSFW 防护 | 设置页预留 NSFW 开关 | NsfwGuard.jsx | — |
| 举报管理 | 帖子详情页长按菜单 | ReportModal.jsx | `POST /api/reports` |
| 管理后台 | 仅 admin 可见入口 | AdminPage.jsx | `/api/admin/*` |
| 滚动进度条 | 帖子详情页顶部 | ScrollProgress.jsx | — |
| 动画角色 | 登录页装饰 | AnimatedCharacters.jsx | — |
| 内容 API | 开发者文档入口 | — | `/api/v1/content/*` |
| Key Split | 开发者文档入口 | — | `/api/key-split/*` |

---

## 四、API 接口详细信息

### Base URL
```
https://api.abdl-space.top
```

### 认证方式
```
Authorization: Bearer <access_token>
```
- 登录后获取 access_token（JWT）
- 支持 Cookie 方式（`credentials: 'include'`）
- Token 过期后用 refresh_token 刷新

### 通用响应格式
```json
// 成功 — 直接返回数据
{ "id": 1, "name": "..." }

// 成功 — 列表
[{ "id": 1 }, { "id": 2 }]

// 错误
{ "error": "错误信息" }
```

### 分页参数
```
GET /api/posts?page=1&limit=20
```
响应包含 `total`、`page`、`limit` 字段。

### 完整 API 文档
参见项目文件：`/home/ZYongX/projects/git/abdl-space/API.md`（约 1365 行，覆盖所有端点的请求/响应格式）

---

## 五、数据库表结构（参考）

共 22 张表：

| 表名 | 说明 | 关键字段 |
|------|------|---------|
| `users` | 用户 | id, email, username, avatar, role, bio, age, region |
| `diapers` | 纸尿裤 | id, brand, model, product_type, thickness, absorbency, is_baby_diaper |
| `diaper_sizes` | 尺码 | diaper_id, label, waist_min/max, hip_min/max |
| `ratings` | 评分 | user_id, diaper_id, absorption/fit/comfort/thickness/appearance/value_score |
| `feelings` | 使用感受 | user_id, diaper_id, size, looseness/softness/dryness/odor_control/quietness |
| `posts` | 帖子 | id, user_id, content, diaper_id, pinned |
| `post_comments` | 评论 | id, post_id, user_id, parent_id, content |
| `likes` | 点赞 | user_id, target_type, target_id |
| `wiki_pages` | Wiki | id, slug, title, content, author_id, diaper_id |
| `terms` | 术语 | id, term, abbreviation, definition, category |
| `messages` | 私信 | id, sender_id, receiver_id, content, read |
| `follows` | 关注 | follower_id, following_id |
| `notifications` | 通知 | id, user_id, type, message, related_id, read |
| `experience` | 经验值 | user_id, current_exp, total_exp, current_level |
| `captcha_sessions` | 验证码会话 | session_id, type, ip, answer_hash, attempts, used |
| `email_verifications` | 邮箱验证 | email, code_hash, type, used |
| `rate_limits` | 限速 | key, count, window_start, expires_at |
| `ks_channels` | KS 渠道 | id, owner_id, name, base_url, api_key_enc |
| `ks_sub_keys` | KS 子 Key | id, key_hash, key_prefix, channel_ids, quota_tokens |
| `ks_usage_logs` | KS 用量 | sub_key_id, channel_id, model, tokens, status |

完整 Schema：`/home/ZYongX/projects/git/abdl-space/schemas/schema.sql`

---

## 六、资源文件

### Logo（必须嵌入 App，不要联网加载）

下载后放入 `app/src/main/res/drawable-*` 和 `mipmap-*` 目录：

```
网站 icon (SVG)：https://img.abdl-space.top/file/1779879250278_ABDL_icon.svg
横版 logo (PNG)：https://img.abdl-space.top/file/1779879241082_ABDL.png
竖版 logo (SVG)：https://img.abdl-space.top/file/1779879267209_ABDL_logo_word.svg
```

### 图标
- 使用 Material Symbols Rounded 图标集（Compose 内置）
- 自定义图标放入 `res/drawable/` 作为 XML vector drawable

### 字体
- 使用系统默认字体（Roboto / Noto Sans CJK）
- 不要额外下载字体文件

### 图片加载
- 网络图片（纸尿裤图片、用户头像）使用 **Coil** 库加载
- Coil 自带内存缓存 + 磁盘缓存
- App 自身的 Logo/Icon 必须内置，不运行时加载

---

## 七、GitHub 操作指南

### 仓库信息
- **账户**：`ZYongX09`
- **仓库名**：`abdl-space-app`
- **创建命令**：
```bash
gh repo create ZYongX09/abdl-space-app --public --description "ABDL Space Android App"
```

### Git 操作
```bash
# 初始化
git init
git add -A
git commit -m "feat: initial project setup"
git remote add origin https://github.com/ZYongX09/abdl-space-app.git
git push -u origin main

# 日常提交
git add -A && git commit -m "type(scope): description" && git push
```

### 提交规范
- `feat`: 新功能
- `fix`: 修复
- `ui`: 界面调整
- `refactor`: 重构
- `docs`: 文档

---

## 八、Cloudflare 操作指南

### 相关 CF 资源
- **CF 账户**：朋友的账户（ZhX589@outlook.com）
- **后端 Worker**：`abdl-space-api`
- **D1 数据库**：`abdl-space-db`（ID: 159f81ba-ea32-4667-a3ce-d72cb1659d93）
- **前端 Pages**：`ABDL-Space-V2`、`abdl-space-mobile`、`abdl-space-open-platform`

### 部署命令
```bash
# 后端部署（在 /home/ZYongX/projects/git/abdl-space 目录）
npx wrangler deploy

# D1 查询
npx wrangler d1 execute abdl-space-db --command "SELECT ..." --remote
```

### ⚠️ 敏感操作（必须经过我确认）
以下操作**必须先告知我并获得同意**后才能执行：
1. `wrangler deploy`（部署后端）
2. `wrangler secret put`（设置密钥）
3. `git push` 到 main 分支
4. 修改 `wrangler.jsonc` 配置
5. D1 数据库写操作（INSERT/UPDATE/DELETE）
6. GitHub 仓库设置变更
7. 任何涉及 API Key、Secret 的操作

---

## 九、技术约束

### ✅ 必须做到
1. **纯原生**：使用 Jetpack Compose，不要 WebView 嵌套
2. **资源内置**：图标、Logo、默认图片全部打入 APK，不运行时联网加载
3. **离线优先**：列表数据本地缓存（Room 数据库），无网络时显示缓存
4. **流畅动画**：所有页面切换、列表加载、按钮交互都要有动效
5. **深色模式**：支持 Dark Theme，跟随系统设置
6. **适配**：支持手机和平板（600dp 以上用平板布局）
7. **错误处理**：所有 API 调用必须有 try-catch，显示用户友好的错误信息
8. **Token 管理**：使用 EncryptedSharedPreferences 存储，过期自动刷新

### ❌ 不要做
1. ❌ 不要用 WebView 嵌套网页
2. ❌ 不要运行时加载远程图标/Logo
3. ❌ 不要使用已废弃的 API
4. ❌ 不要在主线程执行网络请求
5. ❌ 不要硬编码 API Key（使用 BuildConfig 或 Secrets Manager）
6. ❌ 不要跳过错误处理
7. ❌ 不要使用过时的 Support Library（用 AndroidX）

---

## 十、项目结构

```
abdl-space-app/
├── app/
│   ├── src/main/
│   │   ├── java/top/abdl/space/
│   │   │   ├── App.kt                    # Application
│   │   │   ├── MainActivity.kt           # 主 Activity
│   │   │   ├── navigation/               # 导航图
│   │   │   │   └── AppNavigation.kt
│   │   │   ├── ui/
│   │   │   │   ├── theme/                # 主题
│   │   │   │   │   ├── Color.kt
│   │   │   │   │   ├── Type.kt
│   │   │   │   │   ├── Shape.kt
│   │   │   │   │   └── Theme.kt
│   │   │   │   ├── components/           # 通用组件
│   │   │   │   │   ├── AppCard.kt
│   │   │   │   │   ├── AppButton.kt
│   │   │   │   │   ├── AppTextField.kt
│   │   │   │   │   ├── LoadingAnimation.kt
│   │   │   │   │   ├── EmptyState.kt
│   │   │   │   │   └── ErrorView.kt
│   │   │   │   ├── home/                 # 首页
│   │   │   │   ├── diapers/              # 纸尿裤模块
│   │   │   │   │   ├── DiaperListScreen.kt
│   │   │   │   │   ├── DiaperDetailScreen.kt
│   │   │   │   │   └── DiaperViewModel.kt
│   │   │   │   ├── forum/                # 论坛模块
│   │   │   │   │   ├── ForumScreen.kt
│   │   │   │   │   ├── PostDetailScreen.kt
│   │   │   │   │   ├── CreatePostScreen.kt
│   │   │   │   │   └── ForumViewModel.kt
│   │   │   │   ├── auth/                 # 认证模块
│   │   │   │   │   ├── LoginScreen.kt
│   │   │   │   │   ├── RegisterScreen.kt
│   │   │   │   │   └── AuthViewModel.kt
│   │   │   │   ├── profile/              # 个人中心
│   │   │   │   │   ├── ProfileScreen.kt
│   │   │   │   │   ├── EditProfileScreen.kt
│   │   │   │   │   └── ProfileViewModel.kt
│   │   │   │   ├── rankings/             # 排行榜
│   │   │   │   ├── messages/             # 消息
│   │   │   │   ├── notifications/        # 通知
│   │   │   │   └── settings/             # 设置
│   │   │   ├── data/
│   │   │   │   ├── api/                  # Retrofit API
│   │   │   │   │   ├── ApiService.kt
│   │   │   │   │   ├── AuthApi.kt
│   │   │   │   │   ├── DiaperApi.kt
│   │   │   │   │   ├── ForumApi.kt
│   │   │   │   │   └── UserApi.kt
│   │   │   │   ├── repository/           # 数据仓库
│   │   │   │   ├── model/                # 数据模型
│   │   │   │   ├── local/                # Room 数据库
│   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   │   ├── dao/
│   │   │   │   │   └── entity/
│   │   │   │   └── datastore/            # Token 存储
│   │   │   └── util/                     # 工具类
│   │   │       ├── NetworkUtils.kt
│   │   │       ├── DateUtils.kt
│   │   │       └── Extensions.kt
│   │   └── res/
│   │       ├── drawable/                 # 图标、Logo
│   │       ├── mipmap/                   # App 图标
│   │       ├── values/                   # 字符串、颜色
│   │       └── font/                     # 字体（如有）
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

---

## 十一、依赖清单

```kotlin
// build.gradle.kts (app)
dependencies {
    // Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2025.05.00")
    implementation(composeBom)
    
    // Material 3
    implementation("androidx.compose.material3:material3")
    
    // Compose UI
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.animation:animation")
    debugImplementation("androidx.compose.ui:ui-tooling")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.9.0")
    
    // Lifecycle + ViewModel
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0")
    
    // Retrofit + OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Room (本地缓存)
    implementation("androidx.room:room-runtime:2.7.1")
    implementation("androidx.room:room-ktx:2.7.1")
    ksp("androidx.room:room-compiler:2.7.1")
    
    // Coil 2.x (图片加载，纯 Android 足够)
    implementation("io.coil-kt:coil-compose:2.7.0")
    
    // Koin (轻量 DI)
    implementation("io.insert-koin:koin-android:4.0.2")
    implementation("io.insert-koin:koin-androidx-compose:4.0.2")
    
    // Paging 3 (列表分页)
    implementation("androidx.paging:paging-runtime-ktx:3.3.6")
    implementation("androidx.paging:paging-compose:3.3.6")
    
    // DataStore (Token 存储)
    implementation("androidx.datastore:datastore-preferences:1.1.4")
    
    // Kotlin Parcelize
    implementation("org.jetbrains.kotlin:kotlin-parcelize-runtime")
    
    // Cloudflare Turnstile Android SDK
    implementation("com.cloudflare:turnstile-android:1.3.0")
    
    // Firebase Crashlytics (可选，监控)
    // implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    // implementation("com.google.firebase:firebase-crashlytics-ktx")
}
```

---

## 十二、开发顺序

1. **项目初始化**：Gradle 项目、依赖配置、主题系统（M3 Expressive 色彩/字体/形状/动画）
2. **通用组件**：AppCard、AppButton、AppTextField、LoadingAnimation、EmptyState、ErrorView
3. **网络层**：Retrofit API 接口、Token 拦截器、错误处理
4. **认证模块**：登录 → 注册 → 忘记密码 → Token 管理
5. **首页**：Tab 布局 + 纸尿裤推荐卡片 + 预留 AI 区域
6. **纸尿裤模块**：列表（分页/筛选/排序）→ 详情 → 评分 → 感受
7. **论坛模块**：帖子列表 → 详情 → 发帖 → 评论 → 点赞
8. **个人中心**：资料 → 编辑 → 关注 → 粉丝
9. **排行榜 + 搜索**
10. **通知 + 消息**
11. **深色模式适配 + 平板适配**

---

## 十三、关键实现细节

### 验证码集成
```kotlin
// 1. 先评估风险
val risk = api.captchaRisk()  // POST /api/captcha/risk
// 返回 { risk: "low"|"high", flow: "turnstile"|"quantum"|"both" }

// 2. 根据 flow 决定验证方式
when (risk.flow) {
    "turnstile" -> {
        // 使用 Cloudflare Turnstile Android SDK
        TurnstileChallenge.init(siteKey).onSuccess { token ->
            api.verifyTurnstile(sessionId, token)
        }
    }
    "quantum" -> {
        // 调用 /api/captcha/challenge 获取 Quantum 挑战
        // 渲染节点序列让用户按顺序点击
        val challenge = api.createChallenge("quantum")
        // 用户完成后提交答案
        api.verifyCaptcha(sessionId, userAnswer)
    }
    "both" -> {
        // 先 Turnstile，再 Quantum
    }
}
```

### Token 管理
```kotlin
// 使用 EncryptedSharedPreferences
val prefs = EncryptedSharedPreferences.create(
    "auth_prefs",
    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
    context,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
)

// OkHttp 拦截器自动附加 Token
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Chain): Response {
        val token = prefs.getString("access_token", null)
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}
```

### 离线缓存策略
```kotlin
// Room 数据库缓存纸尿裤列表
@Dao
interface DiaperDao {
    @Query("SELECT * FROM diapers ORDER BY brand")
    fun getAll(): Flow<List<DiaperEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(diapers: List<DiaperEntity>)
}

// Repository 模式：先返回缓存，后台刷新
class DiaperRepository(
    private val api: DiaperApi,
    private val dao: DiaperDao
) {
    fun getDiapers(): Flow<List<Diaper>> = flow {
        emit(dao.getAll().first().map { it.toDomain() })  // 缓存
        try {
            val remote = api.getDiapers()  // 网络
            dao.insertAll(remote.map { it.toEntity() })  // 更新缓存
            emit(remote)
        } catch (e: Exception) {
            // 网络失败，已有缓存数据
        }
    }
}
```

---

## 十四、注意事项

1. **不要网页嵌套**：所有页面用 Compose 原生实现，不要用 WebView
2. **资源内置**：Logo、Icon、默认头像等全部放在 `res/` 目录，APK 打包
3. **图片加载**：纸尿裤图片、用户头像等网络图片用 Coil（自带缓存）
4. **动画优先**：页面切换用 `AnimatedNavHost`，列表用 stagger 入场，按钮用弹性动画
5. **错误统一**：所有 API 错误拦截后显示 Toast/Snackbar，不要崩溃
6. **分页加载**：论坛帖子、纸尿裤列表使用 Paging 3 库
7. **验证码**：登录/注册需要先调 `/api/captcha/risk` 判断风险
8. **深色模式**：主题跟随系统，使用 M3 的 `darkColorScheme` / `lightColorScheme`
9. **平板适配**：600dp 以上使用 NavigationRail 替代 BottomNavigation
10. **Deep Link**：帖子/纸尿裤详情页支持 Deep Link（`https://abdl-space.top/post/:id` → App 打开）

---

## 十五、错误重试策略

| 状态码 | 策略 |
|--------|------|
| 401 | 自动刷新 token 后重试 1 次（用 Mutex 防并发刷新） |
| 429 | 读取 `Retry-After` 头，等待后重试 |
| 5xx | 不重试，显示错误信息 |
| 网络超时 | 自动重试 2 次，指数退避 |

### Token 刷新并发控制

```kotlin
private val refreshTokenMutex = Mutex()

suspend fun refreshToken(): String = refreshTokenMutex.withLock {
    // 检查是否已被其他请求刷新
    val currentToken = prefs.getString("access_token", null)
    if (currentToken != null && !isTokenExpired(currentToken)) {
        return currentToken
    }
    // 执行刷新
    val response = authApi.refreshToken(refreshToken)
    prefs.saveTokens(response.accessToken, response.refreshToken)
    return response.accessToken
}
```

---

## 十六、Build Variant

```kotlin
// build.gradle.kts
android {
    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            buildConfigField("String", "API_BASE", '"http://10.0.2.2:8787"')
        }
        create("staging") {
            dimension = "environment"
            buildConfigField("String", "API_BASE", '"https://api-staging.abdl-space.top"')
        }
        create("prod") {
            dimension = "environment"
            buildConfigField("String", "API_BASE", '"https://api.abdl-space.top"')
        }
    }
}
```

使用：
```kotlin
val api = Retrofit.Builder()
    .baseUrl(BuildConfig.API_BASE)
    .build()
```

---

## 十七、Room Schema 迁移策略

```kotlin
// 每次修改 Entity 都要增加版本号 + 写 Migration
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE diapers ADD COLUMN new_field TEXT DEFAULT ''")
    }
}

val db = Room.databaseBuilder(context, AppDatabase::class.java, "abdl-space")
    .addMigrations(MIGRATION_1_2)
    .build()
```

---

*此提示词基于 ABDL Space 项目实际情况编写，扫描了后端（31 路由）、主站前端（34 页面 + 33 组件）、移动端 Web（27 页面 + 29 组件）的完整代码。经三方 Agent 辩论达成共识后更新。*
