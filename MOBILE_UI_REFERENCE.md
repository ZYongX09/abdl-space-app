# ABDL Space 移动端 Web UI 详细参考

> 供 Android App 开发参照。基于 `/home/ZYongX/projects/abdl-space-mobile/` 项目。

---

## 一、设计系统

### 1.1 色彩系统（CSS 变量）

#### 浅色主题（默认）
```css
--primary: #A8D8F0          /* 主色 — 柔和蓝 */
--primary-dark: #6AAEC8     /* 主色深 */
--primary-light: #DEEEFF    /* 主色浅底 */
--accent: #FFB7C5           /* 强调色 — 粉 */
--accent-dark: #F5989E      /* 强调色深 */
--bg: #F5F8FC               /* 页面背景 — 极浅蓝灰 */
--bg-card: #FFFFFF          /* 卡片背景 */
--border: #E8F0F8           /* 边框 */
--text: #2C3E50             /* 主文字 — 深蓝灰 */
--text-light: #7F8C9B       /* 次要文字 */
--text-muted: #999          /* 弱化文字 */
--success: #7BC67E          /* 成功 — 薄荷绿 */
--danger: #E8837C           /* 危险 — 柔和红 */
--warning: #F0C040          /* 警告 — 琥珀 */
--radius: 16px              /* 大圆角 */
--radius-sm: 10px           /* 小圆角 */
--shadow: 0 2px 12px rgba(168, 216, 240, 0.25)
--shadow-hover: 0 8px 30px rgba(168, 216, 240, 0.4)
--hero-bg: linear-gradient(135deg, #A8D8F0 0%, #D4EEF8 50%, #FFE0E8 100%)
--hero-text: #4A7D95
```

#### 深色主题
```css
--primary: #7EB8D4
--bg: #1A1D23
--bg-card: #252830
--border: #383C44
--text: #E0E4EA
--hero-bg: linear-gradient(135deg, #2A3F50 0%, #283540 50%, #3A2830 100%)
```

#### 多彩主题
```css
--bg: transparent
--bg-card: rgba(255, 255, 255, 0.55)  /* 半透明毛玻璃 */
--hero-bg: linear-gradient(135deg, rgba(168,216,240,0.4) 0%, rgba(255,183,197,0.3) 50%, rgba(197,184,232,0.3) 100%)
```

### 1.2 圆角规范
- 卡片：`16px`
- 按钮/输入框：`10px`
- 头像：`50%`（圆形）
- 小元素：`8px`

### 1.3 字体
```css
font-family: 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', system-ui, sans-serif
```

---

## 二、动画系统（MIUI 风格）

### 2.1 核心弹簧参数
```css
--miui-spring: cubic-bezier(0.175, 0.885, 0.32, 1.275)
```
- dampingRatio ≈ 0.65（比 M3 默认 0.85 更弹）
- 带 overshoot 效果（1.275 > 1.0）

### 2.2 页面入场动画
```css
@keyframes miuiFadeInUp {
  from { opacity: 0; transform: translateY(24px) scale(0.97); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}
```
- 整个页面从下方滑入 + 轻微缩放
- 时长 0.45s，弹性曲线

### 2.3 卡片入场动画（Stagger）
```css
.miui-list-enter > * {
  animation: miuiCardIn 0.35s var(--miui-spring) both;
}
.miui-list-enter > *:nth-child(1) { animation-delay: 0s; }
.miui-list-enter > *:nth-child(2) { animation-delay: 0.04s; }
.miui-list-enter > *:nth-child(3) { animation-delay: 0.08s; }
// ...每项延迟 40ms
```

### 2.4 卡片悬停/按压效果
```css
.miui-hover-lift {
  transition: transform 0.35s var(--miui-spring), box-shadow 0.3s ease;
}
.miui-hover-lift:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-hover);
}
```

### 2.5 按钮按压弹性
```css
.miui-press:active {
  transform: scale(0.95);
  transition-duration: 0.08s;
}
```

### 2.6 底部导航动画
```css
.bottom-nav-floating a:active {
  transform: scale(0.88);
  transition-duration: 0.08s;
}
.bottom-nav-floating a.active {
  animation: navPop 0.3s cubic-bezier(0.22, 1, 0.36, 1);
}
@keyframes navPop {
  0% { transform: scale(1); }
  40% { transform: scale(1.12); }
  100% { transform: scale(1); }
}
```

### 2.7 点赞动画
```css
.miui-like.liked {
  animation: likePop 0.35s var(--miui-spring);
}
@keyframes likePop {
  0% { transform: scale(1); }
  50% { transform: scale(1.3); }
  100% { transform: scale(1); }
}
```

### 2.8 Hero 卡片光晕效果
```css
.hero-card::after {
  content: '';
  position: absolute;
  top: 0; left: -100%;
  width: 60%; height: 100%;
  background: linear-gradient(90deg, transparent 0%, rgba(255,255,255,0.08) 40%, rgba(255,255,255,0.15) 50%, rgba(255,255,255,0.08) 60%, transparent 100%);
  animation: heroShine 3s ease-in-out infinite;
}
@keyframes heroShine {
  0% { left: -100%; }
  100% { left: 200%; }
}
```

---

## 三、页面结构

### 3.1 整体布局
```
┌──────────────────────────┐
│     MobileHeader         │ ← 固定顶部，毛玻璃背景
│  (返回 | 标题 | 操作)    │
├──────────────────────────┤
│                          │
│     PageLayout           │ ← 可滚动内容区
│  ┌──────────────────┐    │
│  │   Hero Card      │    │ ← 渐变背景 + 图标 + 标题 + 副标题
│  │  (页面标识)       │    │
│  └──────────────────┘    │
│                          │
│  ┌──────────────────┐    │
│  │   Card 1         │    │ ← 白色卡片，16px 圆角，轻投影
│  │   (stagger 入场)  │    │
│  └──────────────────┘    │
│  ┌──────────────────┐    │
│  │   Card 2         │    │
│  └──────────────────┘    │
│  ...                     │
│                          │
├──────────────────────────┤
│   MobileBottomNav        │ ← 固定底部，5 个 Tab，毛玻璃
│  [首页][私信][纸尿裤][AI][我的] │
└──────────────────────────┘
```

### 3.2 底部导航栏
```jsx
const TABS = [
  { to: '/', icon: 'fa-solid fa-house', label: '首页' },
  { to: '/messages', icon: 'fa-solid fa-envelope', label: '私信', badge: 'message' },
  { to: '/diapers', icon: 'fa-solid fa-baby', label: '纸尿裤' },
  { to: '/recommend', icon: 'fa-solid fa-wand-magic-sparkles', label: 'AI' },
  { to: '/profile', icon: 'fa-solid fa-user', label: '我的' },
];
```
- 固定底部，`z-index: 100`
- 毛玻璃背景：`backdrop-filter: blur(24px) saturate(200%)`
- 高度 48px
- 选中态：仅图标变色，无背景
- 按压缩放 0.88 + navPop 弹性动画
- 私信有未读角标

### 3.3 顶部导航栏
```jsx
<MobileHeader title="标题" leftActions={[...]} actions={[...]} />
```
- 固定顶部
- 左侧：返回按钮（首页不显示）+ 自定义操作
- 中间：标题
- 右侧：自定义操作按钮
- 毛玻璃背景

---

## 四、核心页面

### 4.1 首页/广场（ForumFeed）
- Hero：图标 `fa-comments` + "广场" + "分享你的 ABDL 生活"
- 搜索栏 + 发帖按钮
- 帖子列表（PullToRefresh 下拉刷新）：
  - 头像（圆形）+ 用户名 + 关注按钮 + 官方徽章
  - 时间戳
  - 内容（RichContent 识别 URL）
  - 图片网格（ImageGrid）
  - NSFW 标签
  - 操作栏：❤️ 点赞（带 likePop 动画）| 💬 评论 | 🛡️ 举报
- 置顶帖子：左边框 3px 强调色 + 📌 标签
- Stagger 入场动画

### 4.2 登录页（Login）
- NBW 第三方登录按钮（带 logo）
- 分割线 "或使用账号密码登录"
- 用户名/邮箱输入框
- 密码输入框（带显示/隐藏切换）
- 验证码区域（失败 2 次后显示）
- 隐私政策复选框
- 登录按钮
- 注册/忘记密码链接

### 4.3 个人主页（Profile）— MIUI 风格
- 顶部封面区：渐变背景 + 头像（带动画入场）
- 用户名 + 官方徽章
- 等级/经验值展示
- 统计卡片：帖子数 | 关注 | 粉丝（带 countUp 动画）
- Tab 切换：帖子 | 评分 | 感受
- 编辑资料按钮

### 4.4 纸尿裤列表（Diapers）
- 搜索 + 筛选
- 卡片列表：品牌 + 型号 + 评分 + 图片
- 点击进入详情

### 4.5 纸尿裤详情（DiaperDetail）
- 基本信息（品牌、型号、类型、厚度）
- 6 维度评分展示
- 用户评分列表
- 提交评分表单

### 4.6 发帖页（CreatePost）
- 文本输入区
- 图片上传（ImageUploader）
- 关联纸尿裤选择
- 发布按钮

### 4.7 帖子详情（PostDetail）
- 帖子内容
- 评论列表
- 评论输入框
- 点赞/举报

### 4.8 设置页（Settings）
- 分组设置项：
  - 主题切换（浅色/深色/多彩）
  - NSFW 模糊开关
  - 账户隐私
  - 关于
- 每个设置项：图标 + 标签 + 描述 + 右侧操作/箭头
- 开关组件：圆角滑块，弹性动画

### 4.9 通知页（NotificationsPage）
- 通知列表
- 类型图标：关注 ❤️ | 点赞 👍 | 评论 💬
- 已读/未读状态
- 全部标为已读

### 4.10 消息页（MessagesPage）
- 对话列表
- 最后一条消息预览
- 未读角标
- 新建对话

---

## 五、通用组件

### 5.1 PageLayout
```jsx
<PageLayout hero={{ icon: 'fa-comments', title: '广场', subtitle: '...' }}>
  {children}
</PageLayout>
```
- Hero 卡片：渐变背景 + 图标 + 标题 + 副标题
- 内容区卡片 stagger 入场动画
- 响应式：移动端隐藏 hero

### 5.2 LoadingSkeleton
```jsx
<LoadingSkeleton count={4} height={100} />
```
- 骨架屏加载动画
- 闪烁效果

### 5.3 EmptyState
```jsx
<EmptyState icon="fa-comments" title="暂无帖子" description="快来发第一帖吧！" />
```
- 图标 + 标题 + 描述
- 居中显示

### 5.4 ImageGrid
- 帖子图片网格展示
- 1 图：大图
- 2 图：并排
- 3+ 图：网格
- 点击放大查看

### 5.5 RichContent
- 自动识别文本中的 URL
- 渲染为可点击链接
- 支持 https、www、裸域名

### 5.6 OfficialBadge
- 蓝色 "官方" 徽章
- 管理员专属

### 5.7 PullToRefresh
- 下拉刷新手势
- 加载指示器

### 5.8 ReportModal
- 举报弹窗
- 原因选择：NSFW / 垃圾广告 / 其他
- 描述输入

### 5.9 VerifyModal
- 验证码弹窗
- 风险评估 → Turnstile / Quantum / 两者
- 流程指示器（both 模式）

### 5.10 ToastPopup
- 轻量提示
- 成功/错误/警告
- 自动消失

---

## 六、交互模式

### 6.1 乐观更新（Optimistic Update）
```javascript
// 点赞时先更新 UI，失败再回滚
setPosts(prev => prev.map(p => p.id === postId ? {
  ...p, has_liked: !p.has_liked,
  like_count: p.has_liked ? p.like_count - 1 : p.like_count + 1,
} : p));
try {
  await forumAPI.like({ target_type: 'post', target_id: postId });
} catch (e) {
  // 回滚
  setPosts(prev => prev.map(p => p.id === postId ? { ...p, has_liked: !p.has_liked, ... } : p));
}
```

### 6.2 防抖搜索
```javascript
useEffect(() => {
  const timer = setTimeout(() => { loadPosts(); }, 300);
  return () => clearTimeout(timer);
}, [search]);
```

### 6.3 防重复提交
```javascript
const likingRef = useRef(new Set());
const handleLike = async (postId) => {
  if (likingRef.current.has(postId)) return;
  likingRef.current.add(postId);
  // ...操作...
  likingRef.current.delete(postId);
};
```

### 6.4 关注状态管理
```javascript
const [followMap, setFollowMap] = useState({});
// 乐观更新
setFollowMap(prev => ({ ...prev, [userId]: !wasFollowing }));
try {
  await followsAPI.follow/unfollow(userId);
} catch {
  setFollowMap(prev => ({ ...prev, [userId]: wasFollowing })); // 回滚
}
```

---

## 七、响应式断点

```css
/* 平板 */
@media (min-width: 768px) {
  .hero-card { padding: 1.25rem; }
  .bottom-nav-floating { max-width: 480px; margin: 0 auto; }
}

/* 桌面 */
@media (min-width: 1024px) {
  .hero-card { display: none; }  /* 桌面隐藏 hero */
  .bottom-nav-floating { display: none; }  /* 桌面用侧边栏 */
}
```

---

## 八、关键 CSS 类名速查

| 类名 | 效果 |
|------|------|
| `.card` | 白色卡片，16px 圆角，轻投影 |
| `.miui-hover-lift` | 悬停上浮 2px + 加深投影 |
| `.miui-press` | 按压缩放 0.95 |
| `.miui-list-enter` | 子元素 stagger 入场 |
| `.miui-page-in` | 页面整体入场 |
| `.miui-card-in` | 单个卡片入场 |
| `.miui-like.liked` | 点赞弹跳动画 |
| `.hero-card` | 渐变背景 hero 区域 |
| `.hero-card::after` | 光晕扫过效果 |
| `.bottom-nav-floating` | 底部导航，毛玻璃 |
| `.mobile-header` | 顶部导航，毛玻璃 |
| `.notif-badge` | 未读角标，红色圆形 |
| `.post-pinned` | 置顶帖子左边框 |
| `.post-actions` | 帖子操作栏 |
| `.miui-input-group` | 输入框组 |
| `.skeleton` | 骨架屏闪烁 |
| `.empty-state` | 空状态居中 |
| `.btn-primary` | 主色按钮 |
| `.btn-outline` | 描边按钮 |
| `.btn-xs` | 小号按钮（关注用） |
| `.form-control` | 输入框样式 |
| `.spinner` | 加载旋转 |

---

*此文档基于移动端 Web 源码扫描生成，最后更新：2026-05-30*
