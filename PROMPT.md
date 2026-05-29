# ABDL Space Android App — AI 开发提示词

> 使用 OpenCode + Xiaomi Mimo-V2.5-Pro 开发

---

## 一、项目概述

你正在为 **ABDL Space** 项目开发原生 Android 应用。ABDL Space 是一个纸尿裤爱好者社区平台，提供纸尿裤数据库、用户评分、论坛、AI 推荐等功能。

### 现有项目

| 项目 | 仓库 | 域名 | 技术栈 |
|------|------|------|--------|
| 后端 API | `git/abdl-space` (zhx589/abdl-space) | `api.abdl-space.top` | Cloudflare Worker + Hono + D1 |
| 主站前端 | `abdl-space-v2` (ZYongX09/ABDL-Space-V2) | `abdl-space.top` | React 18 + Vite 5 + Tailwind CSS |
| 移动端 Web | `abdl-space-mobile` (ZYongX09/abdl-space-mobile) | `m.abdl-space.top` | React 18 + Vite 5 + Tailwind CSS |
| 开放平台 | `abdl-space-open-platform` (ZYongX09/abdl-space-open-platform) | `open.abdl-space.top` | React 18 + Vite |
| 图床 | — | `img.abdl-space.top` | Cloudflare ImgBed |

### Android 应用定位

- **仓库名**：`abdl-space-app`（在 GitHub `ZYongX09` 账户下新建）
- **包名**：`top.abdl.space`
- **最低 SDK**：API 26 (Android 8.0)
- **目标 SDK**：API 34 (Android 14)
- **技术栈**：Kotlin + Jetpack Compose + Material 3

---

## 二、核心设计原则

### 🎨 设计风格：MIUI / HyperOS 风格

**不要使用标准 Material Design 3 的默认样式。** 我需要的是类似 MIUI 12 / Xiaomi HyperOS 的设计语言：

#### MIUI 12 设计特点
1. **大圆角卡片**：16-24dp 圆角，轻投影，卡片间留白 12-16dp
2. **柔和渐变**：背景使用微妙的渐变色（如浅蓝→浅紫），而非纯白
3. **弹性动画**：
   - 过渡动画使用 `spring` 物理弹簧（dampingRatio=0.7, stiffness=300）
   - 列表项入场使用 stagger 动画（依次出现，每项延迟 50ms）
   - 按钮点击使用缩放弹性（scale 0.95→1.0，带 overshoot）
   - 页面切换使用 shared element transition + 淡入淡出
4. **毛玻璃效果**：顶部导航栏和底部导航栏使用模糊背景
5. **图标风格**：圆润线性图标，2dp 描边，带微动画
6. **字体层级**：标题用粗体（700），正文用常规（400），次要信息用浅色
7. **色彩系统**：
   - 主色：`#4361EE`（明亮蓝紫）
   - 成功：`#06D6A0`（薄荷绿）
   - 警告：`#F59E0B`（琥珀）
   - 错误：`#EF4444`（红色）
   - 背景：`#F8F9FE`（极浅蓝灰）
   - 卡片：`#FFFFFF`（白色，带 4dp 圆角投影）

#### 动画规范
```kotlin
// 弹簧动画参数
val SpringSpec = spring<Float>(
    dampingRatio = 0.7f,  // 弹性阻尼（0=无限弹, 1=无弹）
    stiffness = 300f       // 刚度（越大越快）
)

// 列表 stagger 入场
items.forEachIndexed { index, item ->
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = tween(durationMillis = 300, delayMillis = index * 50)
        )
    )
}

// 按钮点击弹性
val scale by animateFloatAsState(
    targetValue = if (pressed) 0.95f else 1f,
    animationSpec = SpringSpec
)
```

#### 参考实现
- 主站前端的 CSS 变量：`client/src/styles/global.css`
- 移动端 Web 的动效：`abdl-space-mobile/src/styles/`
- 设计灵感：MIUI 12 设置页、HyperOS 通知中心、iOS 音乐 app 的弹性滚动

### 📱 布局规范
- **底部导航**：5 个 Tab（首页、纸尿裤、广场、消息、我的），使用自定义带动画的底部栏
- **顶部栏**：大标题模式（标题从大变小的 collapsible effect）
- **列表**：使用 LazyColumn + stagger 入场动画
- **详情页**：共享元素过渡（图片、标题）
- **空状态**：使用 Lottie 动画或自定义 SVG，带引导文案

---

## 三、功能规划（分阶段）

### Phase 1 — 基础功能（本次开发）

| 模块 | 功能 | API 端点 |
|------|------|---------|
| **认证** | 登录（用户名/邮箱+密码） | `POST /api/auth/login` |
| | 注册 | `POST /api/auth/register` |
| | 忘记密码 | `POST /api/auth/forgot-password` |
| | Token 刷新 | `POST /api/auth/refresh` |
| **纸尿裤** | 列表（分页、筛选、排序） | `GET /api/diapers` |
| | 详情（尺寸、图片、评分） | `GET /api/diapers/:id` |
| | 搜索 | `GET /api/search?q=&type=diaper` |
| **评分** | 提交评分（6 维度） | `POST /api/ratings` |
| | 查看评分 | `GET /api/ratings/diaper/:id` |
| **论坛** | 帖子列表 | `GET /api/posts` |
| | 帖子详情 + 评论 | `GET /api/posts/:id` |
| | 发帖 | `POST /api/posts` |
| | 点赞 | `POST /api/likes` |
| **用户** | 个人主页 | `GET /api/users/:id` |
| | 编辑资料 | `PUT /api/users/me` |
| | 关注/取关 | `POST/DELETE /api/follows` |
| **排行榜** | 综合排行 | `GET /api/rankings` |
| **通知** | 通知列表 | `GET /api/notifications` |
| **验证码** | 安全验证（Turnstile + Quantum） | `POST /api/captcha/risk` + `/challenge` + `/verify` |

### Phase 2 — 预留位置（UI 先占位）

| 模块 | 预留说明 |
|------|---------|
| AI 推荐 | 首页顶部预留 "AI 推荐" 卡片区域 |
| 私信 | 底部 Tab 已有"消息"，内部暂显示通知 |
| Wiki | 纸尿裤详情页预留 "Wiki" Tab |
| 比较 | 纸尿裤列表预留 "比较" 按钮 |
| 开放平台 | "我的"页面预留"开放平台"入口 |
| NBW 第三方登录 | 登录页预留 "宝宝新天地登录" 按钮位置 |

---

## 四、API 接口信息

### Base URL
```
https://api.abdl-space.top
```

### 认证方式
- JWT Bearer Token（登录后获取）
- 请求头：`Authorization: Bearer <token>`
- Cookie 方式也支持（`credentials: 'include'`）

### 响应格式
```json
// 成功
{ "data": { ... } }  // 或直接返回对象/数组

// 错误
{ "error": "错误信息" }
```

### 分页格式
```json
{
  "items": [...],
  "total": 100,
  "page": 1,
  "limit": 20
}
```

### 主要接口详细文档
参见：`/home/ZYongX/projects/git/abdl-space/API.md`（完整 API 规格，约 1300 行）

---

## 五、数据库表结构（参考）

共 22 张表，核心表：
- `users` — 用户（id, email, username, avatar, role, bio...）
- `diapers` — 纸尿裤（id, brand, model, product_type, thickness, absorbency...）
- `ratings` — 评分（6 维度：absorption/fit/comfort/thickness/appearance/value）
- `posts` — 论坛帖子
- `post_comments` — 评论
- `messages` — 私信
- `follows` — 关注关系
- `notifications` — 通知

完整 Schema：`/home/ZYongX/projects/git/abdl-space/schemas/schema.sql`

---

## 六、资源文件

### Logo（嵌入 App，不要联网加载）
```
网站 icon (SVG)：https://img.abdl-space.top/file/1779879250278_ABDL_icon.svg
横版 logo (PNG)：https://img.abdl-space.top/file/1779879241082_ABDL.png
竖版 logo (SVG)：https://img.abdl-space.top/file/1779879267209_ABDL_logo_word.svg
```

**要求**：将这些资源下载后放入 `app/src/main/res/` 对应目录（drawable/mipmap），不要在运行时联网加载。

### 字体
- 使用系统默认字体（Roboto / Noto Sans CJK）
- 不要额外下载字体文件

### 图标
- 使用 Material Symbols Rounded 图标集
- 关键图标可自定义 SVG 放入 `res/drawable`

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
- **前端 Pages**：`ABDL-Space-V2`、`abdl-space-mobile`

### 部署命令
```bash
# 后端部署（在 git/abdl-space 目录）
cd /home/ZYongX/projects/git/abdl-space
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

### 必须做到
1. **纯原生**：使用 Jetpack Compose，不要 WebView 嵌套
2. **资源内置**：图标、Logo、默认图片全部打入 APK，不联网加载
3. **离线优先**：列表数据本地缓存（Room 数据库），无网络时显示缓存
4. **流畅动画**：所有页面切换、列表加载、按钮交互都要有动效
5. **深色模式**：支持 Dark Theme，跟随系统设置
6. **适配**：支持手机和平板（7 寸以下用手机布局）

### 不要做
1. ❌ 不要用 WebView 嵌套网页
2. ❌ 不要运行时加载远程图标/Logo
3. ❌ 不要使用已废弃的 API
4. ❌ 不要在主线程执行网络请求
5. ❌ 不要硬编码 API Key（使用 BuildConfig 或 Secrets Manager）
6. ❌ 不要跳过错误处理（所有 API 调用必须有 try-catch）

---

## 十、项目结构建议

```
abdl-space-app/
├── app/
│   ├── src/main/
│   │   ├── java/top/abdl/space/
│   │   │   ├── App.kt                    # Application 类
│   │   │   ├── MainActivity.kt           # 主 Activity
│   │   │   ├── navigation/               # 导航图
│   │   │   ├── ui/
│   │   │   │   ├── theme/                # 主题（颜色、字体、形状）
│   │   │   │   ├── components/           # 通用组件（卡片、按钮、输入框）
│   │   │   │   ├── home/                 # 首页
│   │   │   │   ├── diapers/              # 纸尿裤模块
│   │   │   │   ├── forum/                # 论坛模块
│   │   │   │   ├── auth/                 # 认证模块
│   │   │   │   ├── profile/              # 个人中心
│   │   │   │   └── settings/             # 设置
│   │   │   ├── data/
│   │   │   │   ├── api/                  # Retrofit API 接口
│   │   │   │   ├── repository/           # 数据仓库
│   │   │   │   ├── model/                # 数据模型
│   │   │   │   └── local/                # Room 数据库
│   │   │   └── util/                     # 工具类
│   │   └── res/                          # 资源文件
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 十一、开发顺序

1. **项目初始化**：创建 Gradle 项目、配置依赖、设置主题
2. **主题系统**：MIUI 风格的色彩/字体/形状/动画系统
3. **通用组件**：AppCard、AppButton、AppTextField、LoadingAnimation
4. **认证模块**：登录 → 注册 → 忘记密码
5. **首页**：Tab 布局 + 纸尿裤推荐卡片 + 预留 AI 区域
6. **纸尿裤模块**：列表 → 详情 → 评分
7. **论坛模块**：帖子列表 → 详情 → 发帖
8. **个人中心**：资料 → 设置 → 通知
9. **排行榜**
10. **深色模式适配**

---

## 十二、关键注意事项

1. **验证码集成**：登录/注册需要调用 `/api/captcha/risk` 判断风险等级，然后根据 `flow` 字段决定使用 Turnstile 或 Quantum 验证。Turnstile 使用 Android SDK（`com.cloudflare:turnstile-android`）。

2. **图片加载**：使用 Coil 库加载网络图片（纸尿裤图片、用户头像），但 App 本身的 Logo/Icon 必须内置。

3. **Token 管理**：登录后保存 access_token 和 refresh_token 到 EncryptedSharedPreferences，过期时自动刷新。

4. **错误处理**：所有 API 错误统一处理，显示 Toast 或 Snackbar，不要崩溃。

5. **性能**：列表使用 Paging 3 分页加载，图片使用 Coil 的内存+磁盘缓存。

---

*此提示词基于 ABDL Space 项目实际情况编写，最后更新：2026-05-29*
