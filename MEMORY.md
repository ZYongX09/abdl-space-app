# MEMORY.md — 长期记忆

> 最后更新：2026-05-30

---

## 项目基本信息

- **项目名**：ABDL Space Android App
- **包名**：`top.abdl.space`
- **仓库**：`ZYongX09/abdl-space-app`
- **技术栈**：Kotlin + Jetpack Compose + Material 3 Expressive
- **最低 SDK**：API 26 (Android 8.0)
- **目标 SDK**：API 36 (Android 16)
- **编译 SDK**：API 36

## 关键决策记录

- 使用 Material 3 Expressive (alpha) 作为设计系统
- 采用 MIUI 风格弹簧动画（dampingRatio=0.65f, stiffness=280f）
- Token 存储使用 EncryptedSharedPreferences
- 图片加载使用 Coil 2.x
- 依赖注入使用 Koin
- 分页使用 Paging 3
- 本地缓存使用 Room

## 开发进度 (2026-05-30)

所有 Phase 1 模块已完成：
- ✅ 项目初始化（Gradle、依赖、主题）
- ✅ 通用组件（AppCard、AppButton 等）
- ✅ 网络层（Retrofit API、Token 拦截器）
- ✅ 认证模块（登录、注册、忘记密码）
- ✅ Feed 页（帖子列表、发帖、点赞、评论）
- ✅ 个人主页（我的主页、他人主页、关注/取关）
- ✅ 搜索（帖子和纸尿裤搜索）
- ✅ 通知（通知列表、已读）
- ✅ 纸尿裤评分（列表、详情、提交评分）
- ✅ 设置（主题切换、关于页）
- ✅ 深色模式适配

## UI 重设计 (2026-05-30)

用户反馈 opencode 设计的 UI "极为丑陋"，进行了全面重设计：

### 设计风格
- 参考 Twitter App + 微博 App
- 不照抄网页端（abdl-space-mobile）
- 内容优先、简洁克制、减少渐变

### 主要变更
- 配色系统重写（Primary: #4A9CC7, Secondary: #E8909A）
- 圆角规范修正（符合 PROMPT.md 规定）
- 导航从 5 Tab 改为 4 Tab（首页/发现/通知/我的）
- 首页从 4 宫格改为 Twitter 风格 Feed 流
- 个人主页改为微博风格（封面+头像半浮）
- 新增 StaggerItem / ImageGrid / RatingBar 组件
- 修复 AppButton pressed 状态 bug
- 修复 SplashScreen 引用不存在 drawable 的 bug

### 设计原则
- Twitter 风格：Feed 无 Card 包裹，分割线分隔
- 微博风格：个人主页封面渐变 + 头像半浮
- MIUI 风格：Stagger 入场动画
- 克制使用渐变和装饰

## 待办事项

- [x] 项目编译测试（已完成于 2026-05-30）
- [x] 修复潜在的编译错误（已完成于 2026-05-30）
- [x] 添加 Gradle Wrapper（已完成于 2026-05-30）
- [x] Git 初始化和首次提交（已完成于 2026-05-30）
- [x] 创建 GitHub 仓库（已完成于 2026-05-30）
- [x] UI 全面重设计（已完成于 2026-05-30）

## 踩坑记录

- opencode 生成的 UI 严重偏离设计规范（配色、圆角、组件结构）
- AppButton 的 pressed 状态用 `mutableStateOf` 设置后不会自动重置
- SplashScreen 引用了不存在的 `R.drawable.splash_logo`

## 踩坑记录

（待补充）

## 常用命令

```bash
# 构建
./gradlew assembleDebug

# 清理
./gradlew clean

# 检查依赖
./gradlew dependencies
```

## 重要文件位置

- 详细开发规范：`PROMPT.md`
- API 文档：`/home/ZYongX/projects/git/abdl-space/API.md`
- 数据库 Schema：`/home/ZYongX/projects/git/abdl-space/schemas/schema.sql`
- 后端代码：`/home/ZYongX/projects/git/abdl-space`
- 主站前端：`/home/ZYongX/projects/abdl-space-v2`
- 移动端 Web：`/home/ZYongX/projects/abdl-space-mobile`
