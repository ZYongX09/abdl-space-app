package top.abdl.space.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.HazeMaterials
import org.koin.androidx.compose.koinViewModel
import top.abdl.space.ui.auth.AuthViewModel
import top.abdl.space.ui.auth.ForgotPasswordScreen
import top.abdl.space.ui.auth.LoginScreen
import top.abdl.space.ui.auth.RegisterScreen
import top.abdl.space.ui.diapers.DiaperDetailScreen
import top.abdl.space.ui.diapers.DiaperViewModel
import top.abdl.space.ui.diapers.SubmitRatingScreen
import top.abdl.space.ui.forum.CreatePostScreen
import top.abdl.space.ui.forum.ForumScreen
import top.abdl.space.ui.forum.ForumViewModel
import top.abdl.space.ui.forum.PostDetailScreen
import top.abdl.space.ui.home.HomeScreen
import top.abdl.space.ui.notifications.NotificationScreen
import top.abdl.space.ui.splash.SplashScreen
import top.abdl.space.ui.notifications.NotificationViewModel
import top.abdl.space.ui.profile.EditProfileScreen
import top.abdl.space.ui.profile.ProfileScreen
import top.abdl.space.ui.profile.ProfileViewModel
import top.abdl.space.ui.search.SearchScreen
import top.abdl.space.ui.search.SearchViewModel
import top.abdl.space.ui.settings.AboutScreen
import top.abdl.space.ui.settings.AppearanceSettingsScreen
import top.abdl.space.ui.settings.OpenSourceLicensesScreen
import top.abdl.space.ui.settings.SettingsScreen
import top.abdl.space.ui.settings.SettingsViewModel
import top.yukonga.miuix.kmp.basic.NavigationBar as MiuixNavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarDisplayMode
import top.yukonga.miuix.kmp.basic.NavigationBarItem as MiuixNavigationBarItem
// 性能优化
import top.abdl.space.ui.performance.BlurBudget
import top.abdl.space.ui.performance.BlurSurfaceType
import top.abdl.space.ui.performance.MotionTier
import top.abdl.space.ui.performance.rememberAppBackgroundState
import top.abdl.space.ui.performance.rememberMotionTier
import top.abdl.space.ui.performance.rememberScrollingState
import top.abdl.space.ui.performance.resolveBlurBudget

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Home : Screen("home")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object ForgotPassword : Screen("forgot_password")
    data object Forum : Screen("forum")
    data object PostDetail : Screen("post/{postId}") {
        fun createRoute(postId: Int) = "post/$postId"
    }
    data object CreatePost : Screen("create_post")
    data object Diapers : Screen("diapers")
    data object DiaperDetail : Screen("diaper/{diaperId}") {
        fun createRoute(diaperId: Int) = "diaper/$diaperId"
    }
    data object SubmitRating : Screen("submit_rating/{diaperId}") {
        fun createRoute(diaperId: Int) = "submit_rating/$diaperId"
    }
    data object Search : Screen("search")
    data object Notifications : Screen("notifications")
    data object Profile : Screen("profile/{userId}") {
        fun createRoute(userId: Int) = "profile/$userId"
    }
    data object EditProfile : Screen("edit_profile")
    data object Settings : Screen("settings")
    data object AppearanceSettings : Screen("appearance_settings")
    data object OpenSourceLicenses : Screen("open_source_licenses")
    data object About : Screen("about")
}

data class BottomNavItem(
    val screen: Screen,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, Icons.Filled.Home, Icons.Outlined.Home, "首页"),
    BottomNavItem(Screen.Diapers, Icons.Filled.Star, Icons.Outlined.StarOutline, "发现"),
    BottomNavItem(Screen.Notifications, Icons.Filled.Notifications, Icons.Outlined.Notifications, "通知"),
    BottomNavItem(Screen.Profile, Icons.Filled.Person, Icons.Outlined.Person, "我的")
)

/**
 * 全局滚动状态 — HomeScreen 通过这个回调通知导航栏
 */
object GlobalScrollState {
    var isScrolling: Boolean = false
        internal set
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = koinViewModel()
    val authUiState by authViewModel.uiState.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Diapers.route,
        Screen.Notifications.route,
        Screen.Profile.route
    )

    // ─── 性能优化：MotionTier + BlurBudget ───
    val motionTier = rememberMotionTier()
    val isInBackground = rememberAppBackgroundState()

    // 滚动状态追踪
    var isFeedScrolling by remember { mutableStateOf(false) }
    val isScrolling = rememberScrollingState(isFeedScrolling)

    // 底栏 blur 预算
    val blurBudget = resolveBlurBudget(
        surfaceType = BlurSurfaceType.BOTTOM_BAR,
        motionTier = motionTier,
        isScrolling = isScrolling
    )

    // HazeState — 后台时禁用节省内存
    val hazeEnabled = !isInBackground && motionTier != MotionTier.Reduced
    val hazeState = remember(hazeEnabled) {
        HazeState(initialBlurEnabled = hazeEnabled)
    }

    // LiquidGlass backdrop — 仅 Normal/Enhanced 且非滚动时启用
    val contentBackdrop = rememberLayerBackdrop()
    val liquidGlassEnabled = blurBudget.allowRealtime && motionTier != MotionTier.Reduced

    val surfaceGlassColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
    val barShape = remember { RoundedCornerShape(28.dp) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                    val barModifier = Modifier
                        .let { mod ->
                            // Haze 毛玻璃 — 仅 blur 预算允许时
                            if (blurBudget.maxBlurLevel > 0) {
                                mod.hazeEffect(hazeState, HazeMaterials.ultraThin())
                            } else {
                                mod
                            }
                        }
                        .let { mod ->
                            // LiquidGlass — 仅允许实时 blur 时
                            if (liquidGlassEnabled) {
                                mod.drawBackdrop(
                                    backdrop = contentBackdrop,
                                    shape = { barShape },
                                    effects = {
                                        blur((20 * blurBudget.inputScale).dp.toPx())
                                        lens(
                                            refractionHeight = (4 * blurBudget.inputScale).dp.toPx(),
                                            refractionAmount = (2 * blurBudget.inputScale).dp.toPx(),
                                            depthEffect = true,
                                            chromaticAberration = false
                                        )
                                    },
                                    onDrawSurface = { drawRect(surfaceGlassColor) }
                                )
                            } else {
                                mod
                            }
                        }

                    MiuixNavigationBar(
                        modifier = barModifier,
                        color = Color.Transparent,
                        showDivider = false,
                        defaultWindowInsetsPadding = false,
                        mode = NavigationBarDisplayMode.IconAndText
                    ) {
                        bottomNavItems.forEach { item ->
                            val selected = currentRoute == item.screen.route
                            MiuixNavigationBarItem(
                                selected = selected,
                                onClick = {
                                    if (item.screen == Screen.Profile) {
                                        val userId = authUiState.currentUser?.id
                                        if (userId != null) {
                                            navController.navigate(Screen.Profile.createRoute(userId)) {
                                                popUpTo(Screen.Home.route) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        } else {
                                            navController.navigate(Screen.Login.route)
                                        }
                                    } else {
                                        navController.navigate(item.screen.route) {
                                            popUpTo(Screen.Home.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = if (selected) item.selectedIcon else item.unselectedIcon,
                                label = item.label
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Screen.Splash.route,
                modifier = Modifier
                    .padding(padding)
                    .hazeSource(hazeState)
                    .layerBackdrop(contentBackdrop),
                enterTransition = {
                    fadeIn(animationSpec = tween(250)) + slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(250)
                    )
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(250)) + slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(250)
                    )
                },
                popEnterTransition = {
                    fadeIn(animationSpec = tween(250)) + slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(250)
                    )
                },
                popExitTransition = {
                    fadeOut(animationSpec = tween(250)) + slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(250)
                    )
                }
            ) {
                composable(Screen.Splash.route) {
                    SplashScreen(onSplashFinished = {
                        val dest = if (authUiState.isLoggedIn) Screen.Home.route else Screen.Login.route
                        navController.navigate(dest) { popUpTo(Screen.Splash.route) { inclusive = true } }
                    })
                }
                composable(Screen.Login.route) {
                    LoginScreen(authViewModel,
                        { navController.navigate(Screen.Register.route) },
                        { navController.navigate(Screen.ForgotPassword.route) },
                        { navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { inclusive = true } } }
                    )
                }
                composable(Screen.Register.route) {
                    RegisterScreen(authViewModel, { navController.popBackStack() }, {
                        navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                    })
                }
                composable(Screen.ForgotPassword.route) {
                    ForgotPasswordScreen(authViewModel, { navController.popBackStack() }, {
                        navController.navigate(Screen.Login.route) { popUpTo(Screen.ForgotPassword.route) { inclusive = true } }
                    })
                }
                composable(Screen.Home.route) {
                    HomeScreen(koinViewModel(), authViewModel,
                        { navController.navigate(Screen.Search.route) },
                        { navController.navigate(Screen.Notifications.route) },
                        { navController.navigate(Screen.CreatePost.route) },
                        { navController.navigate(Screen.PostDetail.createRoute(it)) },
                        { navController.navigate(Screen.Profile.createRoute(it)) },
                        onScrollStateChanged = { isFeedScrolling = it }
                    )
                }
                composable(Screen.CreatePost.route) {
                    CreatePostScreen(koinViewModel(), { navController.popBackStack() }, { navController.popBackStack() })
                }
                composable(Screen.PostDetail.route, listOf(navArgument("postId") { type = NavType.IntType })) {
                    PostDetailScreen(koinViewModel(), it.arguments?.getInt("postId") ?: return@composable,
                        { navController.popBackStack() }, { navController.navigate(Screen.Profile.createRoute(it)) }
                    )
                }
                composable(Screen.Diapers.route) {
                    ForumScreen(koinViewModel(),
                        { navController.navigate(Screen.DiaperDetail.createRoute(it)) },
                        { navController.navigate(Screen.Search.route) }
                    )
                }
                composable(Screen.DiaperDetail.route, listOf(navArgument("diaperId") { type = NavType.IntType })) {
                    DiaperDetailScreen(koinViewModel(), it.arguments?.getInt("diaperId") ?: return@composable,
                        { navController.popBackStack() }, { navController.navigate(Screen.SubmitRating.createRoute(it)) }
                    )
                }
                composable(Screen.SubmitRating.route, listOf(navArgument("diaperId") { type = NavType.IntType })) {
                    SubmitRatingScreen(koinViewModel(), it.arguments?.getInt("diaperId") ?: return@composable,
                        { navController.popBackStack() }, { navController.popBackStack() }
                    )
                }
                composable(Screen.Search.route) {
                    SearchScreen(koinViewModel(),
                        { navController.navigate(Screen.PostDetail.createRoute(it)) },
                        { navController.navigate(Screen.DiaperDetail.createRoute(it)) },
                        { navController.navigate(Screen.Profile.createRoute(it)) }
                    )
                }
                composable(Screen.Notifications.route) {
                    NotificationScreen(koinViewModel(), { navController.navigate(Screen.PostDetail.createRoute(it)) })
                }
                composable(Screen.Profile.route, listOf(navArgument("userId") { type = NavType.IntType })) {
                    ProfileScreen(koinViewModel(), it.arguments?.getInt("userId") ?: return@composable,
                        { navController.popBackStack() },
                        { navController.navigate(Screen.EditProfile.route) },
                        { navController.navigate(Screen.Profile.createRoute(it)) }
                    )
                }
                composable(Screen.EditProfile.route) {
                    EditProfileScreen(koinViewModel(), { navController.popBackStack() }, { navController.popBackStack() })
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(koinViewModel(), { navController.popBackStack() },
                        { navController.navigate(Screen.AppearanceSettings.route) },
                        { navController.navigate(Screen.OpenSourceLicenses.route) },
                        { navController.navigate(Screen.About.route) }
                    )
                }
                composable(Screen.AppearanceSettings.route) {
                    AppearanceSettingsScreen(koinViewModel(), { navController.popBackStack() })
                }
                composable(Screen.OpenSourceLicenses.route) {
                    OpenSourceLicensesScreen { navController.popBackStack() }
                }
                composable(Screen.About.route) {
                    AboutScreen { navController.popBackStack() }
                }
            }
        }
    }
}
